package com.techJob.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.techJob.Constants;
import com.techJob.DTOs.image.ImageDTO;
import com.techJob.domain.entity.Image;
import com.techJob.domain.entity.ServiceOffer;
import com.techJob.domain.entity.User;
import com.techJob.domain.enums.OffersStatus;
import com.techJob.exception.emailVerification.AccountNotVerifiedException;
import com.techJob.exception.image.ImageException;
import com.techJob.exception.offer.InvalidOfferException;
import com.techJob.exception.offer.OfferNotFoundException;
import com.techJob.exception.user.UserNotFoundException;
import com.techJob.mapper.GeneralMapper;
import com.techJob.repository.ImageRepository;
import com.techJob.repository.ServiceOfferRepository;
import com.techJob.repository.UserRepository;

import jakarta.transaction.Transactional;



@Service
public class ImageService {

	
	private static final Logger log = LoggerFactory.getLogger(ImageService.class);

	
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
    
    private void validateImage(MultipartFile[] file) {
        if (file == null || file.length == 0) {
            throw new ImageException("Image file is empty");
        }
        for (MultipartFile f : file) {
			if (f.getContentType() == null || !f.getContentType().startsWith("image/")) {
				throw new ImageException("Only image files are allowed");
			}
			if (f.getSize() > Constants.IMAGE_MAX_SIZE) {
				throw new ImageException("Image size must be less than 5MB");
			}
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
        MultipartFile[] fileArray=new MultipartFile[1];
	    fileArray[0]=file;
        validateImage(fileArray);
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
    public List<ImageDTO> uploadOfferImage(String offerPublicID, MultipartFile[] files) {
        User user = getCurrentUser();
        verifyEmail(user);
        verifyArtisan(user);
        ServiceOffer offer = getOfferByPublicIDOrThrow(offerPublicID);

        if (!offer.getArtisan().equals(user.getArtisanProfile())) {
            throw new AccessDeniedException("You are not allowed to upload image to this offer");
        }

        validateImage(files);

        List<ImageDTO> uploadedImages = new ArrayList<>();

        int nextPosition = offer.getImages().size() + 1;

        for (MultipartFile f : files) {
            // أنشئ كائن جديد داخل كل دورة
            Image image = new Image();

            // احفظ الملف واحصل على url
            String url = fileStorageService.saveFile(f, "offers");

            image.setUrl(url);
            image.setImagePublicID(UUID.randomUUID().toString());
            image.setPosition(nextPosition++);
            image.setOffer(offer);

            Image savedImage = imageRepository.save(image);

            uploadedImages.add(generalMapper.toDTO(savedImage));
        }

        log.info("Uploaded {} images for offer {}", files.length, offerPublicID);

        return uploadedImages;
    }
    
    

	//================================get profile image==============================================
	public String getProfileImageUrl() {
	    User user = getCurrentUser();
	    if(user.getProfilImageUrl()!=null) 
	    	return user.getProfilImageUrl();
	    
	    return "/uploads/profiles/default-profile.png";
	}

	
	//==================================upload profile image============================================================
	@Transactional
	public String uploadProfileImage(MultipartFile file) {
		
		
	    User user = getCurrentUser();
	    MultipartFile[] fileArray=new MultipartFile[1];
	    fileArray[0]=file;
	    validateImage(fileArray);

	    String oldImage = user.getProfilImageUrl();

	    // 1️⃣ حفظ الصورة الجديدة
	    String newUrl = fileStorageService.saveFile(file, "profiles");

	    // 2️⃣ تحديث قاعدة البيانات
	    user.setProfilImageUrl(newUrl);
	    userRepository.save(user);

	    // 3️⃣ حذف الصورة القديمة بعد نجاح كل شيء
	    if (oldImage != null) {
	        fileStorageService.deleteFile(oldImage);
	    }

	    return newUrl;
	}


	
	//=======================================delete image============================================
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
