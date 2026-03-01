package com.techJob.service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.techJob.DTOs.serviceOffer.CreateExtraServiceOfferDTO;
import com.techJob.DTOs.serviceOffer.ExtraServiceOfferDTO;
import com.techJob.DTOs.serviceOffer.UpdateExtraServiceOfferDTO;
import com.techJob.domain.entity.ExtraServiceOffer;
import com.techJob.domain.entity.ServiceOffer;
import com.techJob.domain.entity.User;
import com.techJob.domain.enums.OffersStatus;
import com.techJob.exception.emailVerification.AccountNotVerifiedException;
import com.techJob.exception.offer.InvalidOfferException;
import com.techJob.exception.offer.OfferNotFoundException;
import com.techJob.exception.user.UserNotFoundException;
import com.techJob.mapper.GeneralMapper;
import com.techJob.repository.ExtraServiceOfferRepository;
import com.techJob.repository.ServiceOfferRepository;
import com.techJob.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class ExtraServiceOfferService {

	
	
	
	private static final Logger log = LoggerFactory.getLogger(ExtraServiceOfferService.class);

    private final ServiceOfferRepository serviceOfferRepository;
    private final UserRepository userRepository;
    private final GeneralMapper generalMapper;
    private final NotificationsServiceImp notificationsServiceImp;
    private final ExtraServiceOfferRepository extraServiceOfferRepository;
    private final AuthAuditLogService auditLogService;

    public ExtraServiceOfferService(
    		ServiceOfferRepository serviceOfferRepository,
    		UserRepository userRepository,
    		AuthAuditLogService auditLogService,
    		GeneralMapper generalMapper,
    		NotificationsServiceImp notificationsServiceImp,
    		ExtraServiceOfferRepository extraServiceOfferRepository) {
        this.serviceOfferRepository = serviceOfferRepository;
        this.userRepository = userRepository;
		this.auditLogService=auditLogService;
        this.generalMapper = generalMapper;
        this.notificationsServiceImp = notificationsServiceImp;
        this.extraServiceOfferRepository = extraServiceOfferRepository;
    }
	
	
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
    private ServiceOffer getOfferByPublicIDOrThrow(String offerPublicID) {
        return serviceOfferRepository.findByOfferPublicID(offerPublicID)
                .orElseThrow(() -> new OfferNotFoundException(offerPublicID));
    }
    
	private void validateUniqueTitles(Set<CreateExtraServiceOfferDTO> extras) {

        Set<String> titles = new HashSet<>();

        for (CreateExtraServiceOfferDTO extra : extras) {

            String title = extra.getTitle();

            
            String normalized = title.trim().toLowerCase();

            if (!titles.add(normalized)) {
                throw new InvalidOfferException(
                    "Duplicate title found inside extra services: " + title
                );
            }
        }
    }
	private void titleValidation(String title) {
		if (title == null || title.trim().isEmpty()) {
			throw new InvalidOfferException("Title cannot be empty");
		}
	}
	private void priceValidation(BigDecimal price) {
		if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
			throw new InvalidOfferException("Price must be greater than 0");
		}
	}
	
	
	// =================== ExtraServiceOffer CRUD ===================
    @Transactional
    public Set<ExtraServiceOfferDTO> createExtraServiceOffer(Set<CreateExtraServiceOfferDTO> dto,String offerPublicID) {
    	
    	
        User user = getCurrentUser();
        verifyEmail(user);
        verifyArtisan(user);
        ServiceOffer offer = getOfferByPublicIDOrThrow(offerPublicID);
        if (!offer.getArtisan().getUser().equals(user)) {
            log.warn("User {} tried to add extra service to offer {} not owned by them", user.getEmail(), offer.getOfferPublicID());
            throw new InvalidOfferException("You are not allowed to add extra services to this offer");
        }
        
        validateUniqueTitles(dto);
        Set<ExtraServiceOfferDTO> extras = new HashSet<>();
        for (CreateExtraServiceOfferDTO extraDTO : dto) {
        	checkDuplicateExtraServiceTitle(offer,extraDTO.getTitle(),null);
        	titleValidation(extraDTO.getTitle());
        	priceValidation(extraDTO.getPrice());
	        ExtraServiceOffer extra = generalMapper.toEntity(extraDTO);
	        extra.setExtraOfferPublicID(UUID.randomUUID().toString());
	        extra.setServiceOffer(offer);
	        extraServiceOfferRepository.save(extra);
	        extras.add(generalMapper.toDTO(extra));
	        log.info("ExtraServiceOffer created: {} for offer {} by user {}", extra.getExtraOfferPublicID(), offer.getOfferPublicID(), user.getEmail());
        }
        
        return extras;
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
        
        if(dto.getTitle()!=null&&!dto.getTitle().trim().isEmpty()) {
			extra.setTitle(dto.getTitle());
		}
        if(dto.getPrice()!=null&&dto.getPrice().compareTo(BigDecimal.ZERO) >0) {
			extra.setPrice(dto.getPrice());
        }
        
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
