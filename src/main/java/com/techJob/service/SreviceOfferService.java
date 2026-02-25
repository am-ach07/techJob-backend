package com.techJob.service;

import java.time.LocalDateTime;
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
public class SreviceOfferService {
    private static final Logger log = LoggerFactory.getLogger(SreviceOfferService.class);

    private final ServiceOfferRepository serviceOfferRepository;
    private final UserRepository userRepository;
    private final GeneralMapper generalMapper;
    private final ReaderJson readerJson;
    private final ImageService imageService;
    private final NotificationsServiceImp notificationsServiceImp;
    private final ExtraServiceOfferRepository extraServiceOfferRepository;
    private final AuthAuditLogService auditLogService;

    public SreviceOfferService(
    		ServiceOfferRepository serviceOfferRepository,
    		UserRepository userRepository,
    		ImageService imageService,
    		ReaderJson readerJson,
    		AuthAuditLogService auditLogService,
    		GeneralMapper generalMapper,
    		NotificationsServiceImp notificationsServiceImp,
    		ExtraServiceOfferRepository extraServiceOfferRepository) {
        this.serviceOfferRepository = serviceOfferRepository;
        this.userRepository = userRepository;
		this.auditLogService=auditLogService;
        this.generalMapper = generalMapper;
        this.readerJson=readerJson;
        this.imageService=imageService;
        this.notificationsServiceImp = notificationsServiceImp;
        this.extraServiceOfferRepository = extraServiceOfferRepository;
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

    private void verifyAdmin(User user) {
        if (!Roles.ADMIN.equals(user.getRole())) {
            log.warn("User {} attempted admin action without admin role", user.getEmail());
            throw new InvalidOrderException("You are not allowed to perform this action");
        }
    }

    private ServiceOffer getOfferByPublicIDOrThrow(String offerPublicID) {
        return serviceOfferRepository.findByOfferPublicID(offerPublicID)
                .orElseThrow(() -> new OfferNotFoundException(offerPublicID));
    }

    private ExtraServiceOffer getExtraServiceByPublicIDOrThrow(String extraOfferPublicID) {
        return extraServiceOfferRepository.findByExtraOfferPublicID(extraOfferPublicID)
                .orElseThrow(() -> new OfferNotFoundException("ExtraServiceOffer not found: " + extraOfferPublicID));
    }

    

    private void checkDuplicateExtraServiceTitle(ServiceOffer offer, String title, String excludePublicID) {
        boolean exists = offer.getExtraServiceOffers().stream()
                .anyMatch(extra -> extra.getTitle().equalsIgnoreCase(title)
                        && (excludePublicID == null || !extra.getExtraOfferPublicID().equals(excludePublicID)));
        if (exists) {
            throw new InvalidOfferException("Duplicate extra service title for this offer");
        }
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
    public ServiceOfferDTO createOffer(String json, MultipartFile file)
            throws JsonProcessingException {

        User user = getCurrentUser();

        CreateServiceOfferRequest dto =
                readerJson.readJsonCreating(json);

        CreateOfferDTO offerDto = dto.getOfferDto();
        CreateExtraServiceOfferDTO extraDto = dto.getExtraDto();

        // create main offer
        ServiceOfferDTO offer = createOffer(offerDto);

        // create extra service
        if (extraDto != null && extraDto.getTitle() != null) {

            ExtraServiceOfferDTO extra =
                    createExtraServiceOffer(extraDto, offer.getOfferPublicID());

            offer.addServiceExtras(extra);
        }

        // upload image
        if (file != null && !file.isEmpty()) {

            ImageDTO image =
                    imageService.uploadOfferImage(offer.getOfferPublicID(), file);

            offer.addImages(image);
        }

        log.info("Offer created successfully by user {}", user.getEmail());

        return offer;
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
	

    
    
    // =================== ExtraServiceOffer CRUD ===================
    @Transactional
    public ExtraServiceOfferDTO createExtraServiceOffer(CreateExtraServiceOfferDTO dto,String offerPublicID) {
        User user = getCurrentUser();
        verifyEmail(user);
        verifyArtisan(user);
        ServiceOffer offer = getOfferByPublicIDOrThrow(offerPublicID);
        if (!offer.getArtisan().getUser().equals(user)) {
            log.warn("User {} tried to add extra service to offer {} not owned by them", user.getEmail(), offer.getOfferPublicID());
            throw new InvalidOfferException("You are not allowed to add extra services to this offer");
        }
        checkDuplicateExtraServiceTitle(offer, dto.getTitle(), null);
        ExtraServiceOffer extra = generalMapper.toEntity(dto);
        extra.setExtraOfferPublicID(UUID.randomUUID().toString());
        extra.setServiceOffer(offer);
        extraServiceOfferRepository.save(extra);
        log.info("ExtraServiceOffer created: {} for offer {} by user {}", extra.getExtraOfferPublicID(), offer.getOfferPublicID(), user.getEmail());
        return generalMapper.toDTO(extra);
    }

    @Transactional
    public ExtraServiceOfferDTO updateExtraServiceOffer(UpdateExtraServiceOfferDTO dto,String offerPublicID,String extraServicePublicID) {
        User user = getCurrentUser();
        verifyEmail(user);
        verifyArtisan(user);
        ServiceOffer offer = getOfferByPublicIDOrThrow(offerPublicID);
        ExtraServiceOffer extra = getExtraServiceByPublicIDOrThrow(extraServicePublicID);
        
        if(!offer.getExtraServiceOffers().contains(extra)) {
        	log.warn("User {} tried to update extra service {}   not owned by them ",user.getEmail(), extraServicePublicID);
        	throw new InvalidOfferException("extra Service not found in this offers "+extraServicePublicID);
        }
        
        if (!offer.getArtisan().getUser().equals(user)) {
            log.warn("User {} tried to update extra service {} not owned by them", user.getEmail(), extra.getExtraOfferPublicID());
            throw new InvalidOfferException("You are not allowed to update this extra service");
        }
        checkDuplicateExtraServiceTitle(offer, dto.getTitle(), extra.getExtraOfferPublicID());
        generalMapper.updateExtraServiceOfferFromDTO(dto, extra);
        extraServiceOfferRepository.save(extra);
        log.info("ExtraServiceOffer updated: {} for offer {} by user {}", extra.getExtraOfferPublicID(), offer.getOfferPublicID(), user.getEmail());
        return generalMapper.toDTO(extra);
    }

    @Transactional
    public void deleteExtraServiceOffer(String offerPublicID,String extraServicePublicID) {
        User user = getCurrentUser();
        verifyEmail(user);
        verifyArtisan(user);
        ServiceOffer offer = getOfferByPublicIDOrThrow(offerPublicID);
        ExtraServiceOffer extra = getExtraServiceByPublicIDOrThrow(extraServicePublicID);
        if(!offer.getExtraServiceOffers().contains(extra)) {
        	log.warn("User {} tried to delete extra service {}   not owned by them ",user.getEmail(), extraServicePublicID);
        	throw new InvalidOfferException("extra Service not found in this offers "+extraServicePublicID);
        }
        if (!offer.getArtisan().getUser().equals(user)) {
            log.warn("User {} tried to delete extra service {} not owned by them", user.getEmail(), extra.getExtraOfferPublicID());
            throw new InvalidOfferException("You are not allowed to delete this extra service");
        }
        
        log.info("ExtraServiceOffer deleted: {} for offer {} by user {}", extra.getExtraOfferPublicID(), offer.getOfferPublicID(), user.getEmail());
        offer.removeExtraService(extra);
        serviceOfferRepository.save(offer);

    }

    @Transactional
    public List<ExtraServiceOfferDTO> getMyExtraServicesForOffer(String offerPublicID) {
        ServiceOffer offer = getOfferByPublicIDOrThrow(offerPublicID);
        Set<ExtraServiceOffer> extras = offer.getExtraServiceOffers();
        log.info("Fetched extra services for offer {}", offerPublicID);
        return extras.stream().map(generalMapper::toDTO).collect(Collectors.toList());
    }
    @Transactional
    public List<ExtraServiceOfferDTO> getPublicExtraServicesForOffer(String offerPublicID) {
        ServiceOffer offer = serviceOfferRepository.findByOfferPublicIDAndOffersStatus(offerPublicID, OffersStatus.ACTIVE)
                .orElseThrow(() -> new OfferNotFoundException(offerPublicID));
        Set<ExtraServiceOffer> extras = offer.getExtraServiceOffers();
        log.info("Fetched extra services for offer {}", offerPublicID);
        return extras.stream().map(generalMapper::toDTO).collect(Collectors.toList());
    }
}
