package com.service;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.Constants;
import com.DTOs.image.ImageDTO;
import com.domain.entity.Image;
import com.domain.entity.ServiceOffer;
import com.domain.entity.User;
import com.domain.enums.OffersStatus;
import com.exception.emailVerification.AccountNotVerifiedException;
import com.exception.image.ImageException;
import com.exception.offer.InvalidOfferException;
import com.exception.offer.OfferNotFoundException;
import com.exception.user.UserNotFoundException;
import com.mapper.GeneralMapper;
import com.repository.ImageRepository;
import com.repository.ServiceOfferRepository;
import com.repository.UserRepository;

import jakarta.transaction.Transactional;



@Service
public class ImageService {

	
	private static final Logger log = LoggerFactory.getLogger(SreviceOfferService.class);

	
	private final ServiceOfferRepository serviceOfferRepository;
    private final UserRepository userRepository;
    private final GeneralMapper generalMapper;
    private final ImageRepository imageRepository;
    private final FileStorageService fileStorageService;
    private final AuthAuditLogService auditLogService;

    public ImageService(
    		ServiceOfferRepository serviceOfferRepository,
    		ImageRepository imageRepository,
    		FileStorageService fileStorageService,
    		UserRepository userRepository,
    		AuthAuditLogService auditLogService,
    		GeneralMapper generalMapper,
    		NotificationsServiceImp notificationsServiceImp) {
        this.userRepository = userRepository;
		this.auditLogService=auditLogService;
        this.imageRepository = imageRepository;
        this.fileStorageService = fileStorageService;
        this.generalMapper = generalMapper;
        this.serviceOfferRepository=serviceOfferRepository;
        
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
    
    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ImageException("Image file is empty");
        }
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new ImageException("Only image files are allowed");
        }
        if (file.getSize() > Constants.IMAGE_MAX_SIZE) {
            throw new ImageException("Image size must be less than 5MB");
        }
    }
    private ServiceOffer getOfferByPublicIDOrThrow(String offerPublicID) {
        return serviceOfferRepository.findByOfferPublicID(offerPublicID)
                .orElseThrow(() -> new OfferNotFoundException(offerPublicID));
    }
	
	//=================== Update Image ===================
    @Transactional
    public ImageDTO updateOfferImage(String offerPublicID, String imagePublicID, MultipartFile file) {
        User user = getCurrentUser();
        verifyEmail(user);
        validateImage(file);
        Image image = imageRepository.findByImagePublicID(imagePublicID)
                .orElseThrow(() -> new ImageException("Image not found"));
        if (!image.getOffer().getOfferPublicID().equals(offerPublicID)) {
            throw new ImageException("Image does not belong to this offer");
        }
        if (!image.getOffer().getArtisan().equals(user.getArtisanProfile())) {
            throw new ImageException("Unauthorized");
        }
        String newUrl = fileStorageService.saveFile(file, "offers");
        String oldUrl = image.getUrl();
        image.setUrl(newUrl);
        imageRepository.save(image);
        // Only delete old file after successful save
        fileStorageService.deleteFile(oldUrl);
        log.info("Image updated for offer {} by user {}", offerPublicID, user.getEmail());
        return generalMapper.toDTO(image);
    }
    //=================== Delete Image ===================
    @Transactional
    public void deleteOfferImage(String offerPublicID, String imagePublicID) {
        User user = getCurrentUser();
        verifyEmail(user);
        Image image = imageRepository.findByImagePublicID(imagePublicID)
                .orElseThrow(() -> new ImageException("Image not found"));
        if (!image.getOffer().getOfferPublicID().equals(offerPublicID)) {
            throw new ImageException("Image does not belong to this offer");
        }
        if (!image.getOffer().getArtisan().equals(user.getArtisanProfile())) {
            throw new ImageException("Unauthorized");
        }
        String oldUrl = image.getUrl();
        imageRepository.delete(image);
        fileStorageService.deleteFile(oldUrl);
        log.info("Image deleted for offer {} by user {}", offerPublicID, user.getEmail());
    }
	//=================== Get Images ===================
    public List<ImageDTO> getMyImage(String offerPublicID) {
    	
        ServiceOffer offer = getOfferByPublicIDOrThrow(offerPublicID);
        log.info("Fetched images for offer {}", offerPublicID);
        return offer.getImages()
        		.stream()
                .map(generalMapper::toDTO)
                .toList();
    }
    public List<ImageDTO> getPublicImage(String offerPublicID) {
        ServiceOffer offer = serviceOfferRepository.findByOfferPublicIDAndOffersStatus(offerPublicID, OffersStatus.ACTIVE)
                .orElseThrow(() -> new OfferNotFoundException(offerPublicID));
        log.info("Fetched images for offer {}", offerPublicID);
        return offer.getImages()
        		.stream()
        		.map(generalMapper::toDTO)
                .toList();
    }
    //==================Get Image By publicID===================
    public ImageDTO getImageByPublicID(String offerPublicID,String imagePublicID) {
    	ServiceOffer offer = serviceOfferRepository.findByOfferPublicIDAndOffersStatus(offerPublicID, OffersStatus.ACTIVE)
                .orElseThrow(() -> new OfferNotFoundException(offerPublicID));
        Image image = imageRepository.findByOfferAndImagePublicID(offer, imagePublicID)
                .orElseThrow(() -> new ImageException("Image not found"));
        return generalMapper.toDTO(image);
    }
    public ImageDTO getPublicImageByPublicID(String offerPublicID,String imagePublicID) {
    	User user=getCurrentUser();
    	verifyArtisan(user);
    	verifyEmail(user);
    	ServiceOffer offer = getOfferByPublicIDOrThrow(offerPublicID);
        Image image = imageRepository.findByOfferAndImagePublicID(offer, imagePublicID)
                .orElseThrow(() -> new ImageException("Image not found"));
        return generalMapper.toDTO(image);
    }
    
 // =================== Image Handling ===================
    @Transactional
    public ImageDTO uploadOfferImage(String offerPublicID, MultipartFile file) {
        User user = getCurrentUser();
        verifyEmail(user);
        verifyArtisan(user);
        ServiceOffer offer = getOfferByPublicIDOrThrow(offerPublicID);
        if (!offer.getArtisan().equals(user.getArtisanProfile())) {
            log.warn("User {} tried to upload image to offer {} not owned by them", user.getEmail(), offerPublicID);
            throw new AccessDeniedException("You are not allowed to upload image to this offer");
        }
        validateImage(file);
        String url = fileStorageService.saveFile(file, "offers");
        Image image = new Image();
        image.setUrl(url);
        image.setImagePublicID(UUID.randomUUID().toString());
        image.setPosition(offer.getImages().size() + 1);
        image.setOffer(offer);
        imageRepository.save(image);
        log.info("Image uploaded for offer {} by user {}", offerPublicID, user.getEmail());
        
        return generalMapper.toDTO(image);
        
        
    }
    
    

	//get profile image
	public String getProfileImageUrl() {
	    User user = getCurrentUser();
	    if(user.getProfilImageUrl()!=null) 
	    	return user.getProfilImageUrl();
	    
	    return "/uploads/profiles/default-profile.png";
	}

	
	//upload profile image
	@Transactional
	public String uploadProfileImage(MultipartFile file) {

	    User user = getCurrentUser();

	    validateImage(file);

	    String newUrl = fileStorageService.saveFile(file, "profiles");

	    String image=user.getProfilImageUrl();
	    if (image != null) { 

	        fileStorageService.deleteFile(image);
	    }
	    user.setProfilImageUrl(newUrl);

	    
	    
	    userRepository.save(user);
	    return newUrl;
	}


	
	//delete image
	public void deleteProfileImage() {

	    User user = getCurrentUser();

	    String image=user.getProfilImageUrl();
	    
	    
	    if(image!=null) {
	    	fileStorageService.deleteFile(image);
	    	user.setProfilImageUrl(null);
	    	userRepository.save(user);
	    }
	    else throw new ImageException("Image Not Foumd !");
	}

}
