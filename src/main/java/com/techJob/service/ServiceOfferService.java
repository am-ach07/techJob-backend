package com.techJob.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.techJob.DTOs.PaginationAndSortDTO;
import com.techJob.DTOs.image.ImageDTO;
import com.techJob.DTOs.serviceOffer.CreateExtraServiceOfferDTO;
import com.techJob.DTOs.serviceOffer.CreateOfferDTO;
import com.techJob.DTOs.serviceOffer.CreateServiceOfferRequest;
import com.techJob.DTOs.serviceOffer.ExtraServiceOfferDTO;
import com.techJob.DTOs.serviceOffer.ServiceOfferDTO;
import com.techJob.DTOs.serviceOffer.UpdateExtraServiceOfferDTO;
import com.techJob.DTOs.serviceOffer.UpdateServiceOfferDTO;
import com.techJob.domain.entity.ArtisanProfile;
import com.techJob.domain.entity.ExtraServiceOffer;
import com.techJob.domain.entity.Image;
import com.techJob.domain.entity.ServiceOffer;
import com.techJob.domain.entity.User;
import com.techJob.domain.enums.OffersStatus;
import com.techJob.domain.enums.Roles;
import com.techJob.exception.InvalidActionException;
import com.techJob.exception.emailVerification.AccountNotVerifiedException;
import com.techJob.exception.offer.InvalidOfferException;
import com.techJob.exception.offer.OfferNotFoundException;
import com.techJob.exception.order.InvalidOrderException;
import com.techJob.exception.user.UserNotFoundException;
import com.techJob.mapper.GeneralMapper;
import com.techJob.mapper.ReaderJson;
import com.techJob.repository.ExtraServiceOfferRepository;
import com.techJob.repository.ServiceOfferRepository;
import com.techJob.repository.UserRepository;
import com.techJob.response.ProfileResponse;

import jakarta.transaction.Transactional;

@Service
public class ServiceOfferService {
    private static final Logger log = LoggerFactory.getLogger(ServiceOfferService.class);

    private final ServiceOfferRepository serviceOfferRepository;
    private final UserRepository userRepository;
    private final GeneralMapper generalMapper;
    private final ImageService imageService;
    private final NotificationsServiceImp notificationsServiceImp;
    private final AuthAuditLogService auditLogService;
    private final ExtraServiceOfferService extraServiceOfferService;

    public ServiceOfferService(
    		ServiceOfferRepository serviceOfferRepository,
    		UserRepository userRepository,
    		ImageService imageService,
    		AuthAuditLogService auditLogService,
    		GeneralMapper generalMapper,
    		NotificationsServiceImp notificationsServiceImp,
    		ExtraServiceOfferService extraServiceOfferService) {
        this.serviceOfferRepository = serviceOfferRepository;
        this.userRepository = userRepository;
		this.auditLogService=auditLogService;
        this.generalMapper = generalMapper;
        this.imageService=imageService;
        this.notificationsServiceImp = notificationsServiceImp;
		this.extraServiceOfferService = extraServiceOfferService;
    }

    // =================== Utility Methods ===================
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByUsernameOrEmail(email, email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    private void verifyEmail(User user) {
        if (!user.getEmailVerified()) {
            log.warn("User {} attempted action without verified email", user.getEmail());
            throw new AccountNotVerifiedException("Please verify your email to perform this action.");
        }
    }

    private void verifyArtisan(User user) {
        if (user.getArtisanProfile() == null) {
            log.warn("User {} attempted artisan action without artisan profile", user.getEmail());
            throw new InvalidOfferException("You are not an artisan");
        }
    }

    

    private ServiceOffer getOfferByPublicIDOrThrow(String offerPublicID) {
        return serviceOfferRepository.findByOfferPublicID(offerPublicID)
                .orElseThrow(() -> new OfferNotFoundException(offerPublicID));
    }

    

 
    private ServiceOfferDTO mapOfferWithProfile(ServiceOffer offer) {

        ServiceOfferDTO dto = generalMapper.toDTO(offer);

        ProfileResponse profile = new ProfileResponse();

        // Map artisan
        profile.setArtisan(
                generalMapper.toDTO(offer.getArtisan())
        );

        // Map user
        profile.setUser(
                generalMapper.toDTO(offer.getArtisan().getUser())
        );

        // Profile image null-safe handling
        String profileImage = offer.getArtisan().getUser().getProfilImageUrl();

        if (profileImage == null) {
            profile.getUser().setProfilImageUrl("/uploads/profiles/default-profiles.png"
            );
        }

        dto.setProfile(profile);

        return dto;
    }
    // create offer--------------------------------------------------------------------------------
    @Transactional
    private ServiceOfferDTO createOffer(CreateOfferDTO dto) {
        User user = getCurrentUser();
        verifyEmail(user);
        verifyArtisan(user);
        ArtisanProfile profile = user.getArtisanProfile();
        ServiceOffer offer = generalMapper.toEntity(dto);
        offer.setOfferPublicID(UUID.randomUUID().toString());
        offer.setOffersStatus(OffersStatus.ACTIVE);
        profile.addService(offer);
        serviceOfferRepository.save(offer);
        notificationsServiceImp.createNotification(user.getPublicID(), "My Offers", "Your offer created successfully");
        log.info("ServiceOffer created: {} by user {}", offer.getOfferPublicID(), user.getEmail());
        return generalMapper.toDTO(offer);
    }
    
    private void validateOffersStatusTransition(OffersStatus current, OffersStatus target) {

        if (current == target) {
            throw new InvalidActionException("Offer already in this status");
        }

        switch (target) {

            case PAUSED -> {
                if (current != OffersStatus.ACTIVE) {
                    throw new InvalidActionException("Only ACTIVE offers can be paused");
                }
            }

            case ACTIVE -> {
                if (current != OffersStatus.PAUSED && current != OffersStatus.ARCHIVED) {
                    throw new InvalidActionException("Only PAUSED or ARCHIVED offers can be activated");
                }
            }

            case ARCHIVED -> {
                if (current == OffersStatus.ARCHIVED) {
                    throw new InvalidActionException("Offer already archived");
                }
            }

            default -> throw new InvalidActionException("Unsupported status");
        }
    }
    
    

    @Transactional
    public ServiceOfferDTO createOffer(CreateServiceOfferRequest dto, MultipartFile[] file)
             {

        User user = getCurrentUser();
        
        CreateOfferDTO offerDto = dto.getOfferDto();
        Set<CreateExtraServiceOfferDTO> extraDto = dto.getExtraDto();

        // create main offer
        ServiceOfferDTO offer = createOffer(offerDto);
        
        // create extra service
        if(extraDto!=null) {
        	Set<ExtraServiceOfferDTO> extras=extraServiceOfferService
        			.createExtraServiceOffer(extraDto,offer.getOfferPublicID());
        	offer.setExtraServiceOffers(extras);
        }
		

        // upload image
        if (file != null && file.length != 0&&file[0] != null && !file[0].isEmpty()) {

            List<ImageDTO> image =
                    imageService.uploadOfferImage(offer.getOfferPublicID(), file);

            offer.setImages(image);
        }

        log.info("Offer created successfully by user {}", user.getEmail());

        return offer;
    }



    

    
   
    
    
   
	//get my offer--------------------------------------------------------------------------------
    public Page<ServiceOfferDTO> getMyOffer(PaginationAndSortDTO dto) {

        User user = getCurrentUser();
        verifyEmail(user);
        verifyArtisan(user);

        Pageable pageable = PageRequest.of(
                dto.getPage(),
                dto.getSize(),
                dto.getSort() != null
                        ? dto.getSort().toSpringSort()
                        : Sort.by("createdAt").descending()
        );

        Page<ServiceOffer> page =
                serviceOfferRepository.findByArtisan(user.getArtisanProfile(), pageable);

        log.info("Fetched offers for artisan {}", user.getEmail());

        return page.map(this::mapOfferWithProfile);
    }
    //get my offer by publicID-----------------------------------------------------------------------------
    public ServiceOfferDTO getMyOfferByPublicID(String offerPublicID) {
        User user = getCurrentUser();
        verifyEmail(user);
        verifyArtisan(user);
        ServiceOffer offer = getOfferByPublicIDOrThrow(offerPublicID);
        if (!offer.getArtisan().equals(user.getArtisanProfile())) {
            log.warn("User {} tried to access offer {} not owned by them", user.getEmail(), offerPublicID);
            throw new InvalidOfferException("You are not allowed to view this offer");
        }
        
        return mapOfferWithProfile(offer);
    }
    //update my offer--------------------------------------------------------------------------------
    @Transactional
    public ServiceOfferDTO updateMyOffer(UpdateServiceOfferDTO dto, String offerPublicID) {
        User user = getCurrentUser();
        verifyEmail(user);
        verifyArtisan(user);
        ServiceOffer offer = getOfferByPublicIDOrThrow(offerPublicID);
        if (!offer.getArtisan().equals(user.getArtisanProfile())) {
            log.warn("User {} tried to update offer {} not owned by them", user.getEmail(), offerPublicID);
            throw new InvalidOfferException("You are not allowed to update this offer");
        }
        generalMapper.updateOfferFromDTO(dto, offer);
        log.info("ServiceOffer updated: {} by user {}", offerPublicID, user.getEmail());
        
        
        return mapOfferWithProfile(offer);
    }
    // Update offer OffersStatus (pause, delete, etc.)
    @Transactional
    public ServiceOfferDTO updateStatusMyOffer(String offerPublicID, OffersStatus newStatus) {

        User user = getCurrentUser();
        verifyEmail(user);
        verifyArtisan(user);

        ServiceOffer offer = getOfferByPublicIDOrThrow(offerPublicID);

        if (!offer.getArtisan().equals(user.getArtisanProfile())) {
            throw new InvalidOfferException("You are not allowed to update this offer");
        }

        OffersStatus currentStatus = offer.getOffersStatus();

        validateOffersStatusTransition(currentStatus, newStatus);

        // ===== ARCHIVE =====
        if (newStatus == OffersStatus.ARCHIVED) {

            // set only once
            if (currentStatus != OffersStatus.ARCHIVED) {
                offer.setDeletedAt(LocalDateTime.now());
            }

        }

        // ===== RESTORE =====
        if (newStatus == OffersStatus.ACTIVE) {

            // clear archive timestamp
            offer.setDeletedAt(null);
        }

        offer.setOffersStatus(newStatus);

        serviceOfferRepository.save(offer);

        log.info("Offer {} status changed {} -> {}", offerPublicID, currentStatus, newStatus);

        return mapOfferWithProfile(offer);
    }


    


    // Soft delete my offer (set OffersStatus to DELETED)
    @Transactional
    public void deleteMyOffer(String offerPublicID) {
    	updateStatusMyOffer(offerPublicID, OffersStatus.ARCHIVED);
    }


    


    //get all public offer--------------------------------------------------------------------------------
    public Page<ServiceOfferDTO> getPublicOffer(PaginationAndSortDTO dto) {
        Pageable pageable = PageRequest.of(
                dto.getPage(),
                dto.getSize(),
                dto.getSort() != null ? dto.getSort().toSpringSort() : Sort.by("createdAt").descending()
        );
        Page<ServiceOffer> page = serviceOfferRepository.findByOffersStatus(OffersStatus.ACTIVE, pageable);
        log.info("Fetched public offers");
        return page.map(this::mapOfferWithProfile);
    }
    
    //get public offer by publicID--------------------------------------------------------------------------------
    public ServiceOfferDTO getOfferByPublicID(String offerPublicID) {
        ServiceOffer offer = serviceOfferRepository.findByOfferPublicIDAndOffersStatus(offerPublicID, OffersStatus.ACTIVE)
                .orElseThrow(() -> new OfferNotFoundException(offerPublicID));
        log.info("Fetched public offer {}", offerPublicID);
        return mapOfferWithProfile(offer);
    }
	

    
    
    
}
