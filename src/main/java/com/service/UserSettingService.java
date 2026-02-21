package com.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.DTOs.UpdateProfileRequest;
import com.DTOs.user.AddressDTO;
import com.DTOs.user.CreateAddressDTO;
import com.DTOs.user.EmailRequest;
import com.DTOs.user.UpdatePasswordRequest;
import com.DTOs.user.UpdatePhoneNumberRequest;
import com.DTOs.user.UpdateUsernameRequest;
import com.domain.entity.Address;
import com.domain.entity.ArtisanProfile;
import com.domain.entity.ArtisanSkill;
import com.domain.entity.User;
import com.exception.auth.EmailAlreadyExistsException;
import com.exception.auth.PhoneNumberAlreadyExistsException;
import com.exception.auth.UsernameAlreadyExistsException;
import com.exception.user.PasswordAreSameOldOne;
import com.exception.user.UserNotFoundException;
import com.mapper.GeneralMapper;
import com.repository.AddressRepository;
import com.repository.ImageRepository;
import com.repository.UserRepository;
import com.response.ProfileResponse;

import jakarta.transaction.Transactional;

@Service
public class UserSettingService {

    private final AddressRepository addressRepository;

	private final UserRepository userRepository;
	private final GeneralMapper generalMapper;
	private final RefreshTokenService refreshTokenService;
	private final PasswordEncoder passwordEncoder;
	private final ImageRepository imageRepository;
	private final FileStorageService fileStorageService;
	private final NotificationsServiceImp notificationsService;
	
	public UserSettingService(UserRepository userRepository,ImageRepository imageRepository,FileStorageService fileStorageService, GeneralMapper generalMapper,RefreshTokenService refreshTokenService, PasswordEncoder passwordEncoder,NotificationsServiceImp notificationsService, AddressRepository addressRepository) {
		this.userRepository = userRepository;
		this.generalMapper = generalMapper;
		this.imageRepository=imageRepository;
		this.fileStorageService=fileStorageService;
		this.refreshTokenService=refreshTokenService;
		this.passwordEncoder=passwordEncoder;
		this.notificationsService=notificationsService;
		this.addressRepository = addressRepository;
	}
	
	
	
	//get current user
	private User getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    String email = auth.getName();

	    return userRepository
	            .findByUsernameOrEmail(email, email)
	            .orElseThrow(() -> new UserNotFoundException(email));
	}
	
	 
	
	//show my profile
	public ProfileResponse getMyProfile() {
	    User user = getCurrentUser();

	    ProfileResponse response = new ProfileResponse();
	    response.setUser(generalMapper.toDTO(user));

	    if (user.getArtisanProfile() != null) {
	        response.setArtisan(
	            generalMapper.toDTO(user.getArtisanProfile())
	        );
	    }
	    if(user.getProfilImageUrl()==null)
	    	response.getUser().setProfilImageUrl("/uploads/profiles/default-profile.png");

	    return response;
	}

	
	//update profile 
	@Transactional
	public ProfileResponse updateMyProfile(
	        UpdateProfileRequest request
	) {
	    User user = getCurrentUser();

	    // تحديث المستخدم
	    generalMapper.updateUserFromDTO(request.getUser(), user);


	    // تحديث الحرفي إن وُجد
	    ArtisanProfile artisanProfile = null;
	    if (request.getArtisan() != null && user.getArtisanProfile() != null) {
	        artisanProfile = user.getArtisanProfile();
	        //skill

	        if(request.getArtisan().getDescription()!=null)
	        	artisanProfile.setDescription(request.getArtisan().getDescription());
	        if(request.getArtisan().getSkills()!=null&&!request.getArtisan().getSkills().isEmpty())
	        	artisanProfile.getSkills().clear();

	        List<ArtisanSkill> newSkills =
	                generalMapper.toEntity(request.getArtisan().getSkills());

	        for (ArtisanSkill skill : newSkills) {
	            skill.setArtisan(artisanProfile);
	        }

	        artisanProfile.getSkills().addAll(newSkills);
	    }


	    // بناء Response ثابت
	    ProfileResponse response =new ProfileResponse() ;
	    response.setUser(generalMapper.toDTO(user));

	    if (artisanProfile != null) {
	        response.setArtisan(generalMapper.toDTO(artisanProfile));
	    }

	    return response;
	}

	//update email
	public String updateEmail(EmailRequest request) {
	    User user = getCurrentUser();
	    
	    if (request.getEmail().equals(user.getEmail())) {
	        return user.getEmail(); // لا تغيير مطلوب
	    }

	    if (userRepository.existsByEmail(request.getEmail())) {
	        throw new EmailAlreadyExistsException(request.getEmail());
	    }

	    user.setEmail(request.getEmail());
	    userRepository.save(user);

	    return request.getEmail();
	}
	//update password
	public void updatePassword(UpdatePasswordRequest request) {
			    User user = getCurrentUser();

	    if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
	        throw new PasswordAreSameOldOne();
	    }

	    user.setPassword(passwordEncoder.encode(request.getPassword()));
	    userRepository.save(user);
	}
	//update username
	public String updateUsername(UpdateUsernameRequest request) {
	    User user = getCurrentUser();
	    	    if (request.getUsername().equals(user.getUsername())) {
	    	    	return user.getUsername(); // لا تغيير مطلوب
	    	    }
	    	    if (userRepository.existsByUsername(request.getUsername())) {
    		        throw new UsernameAlreadyExistsException(request.getUsername());
	    	    }
	    user.setUsername(request.getUsername());
	    userRepository.save(user);
	    
	    return request.getUsername();
	}
	//update Phone Number
	public String updatePhoneNumber(UpdatePhoneNumberRequest request) {
	    User user = getCurrentUser();

	    if (request.getPhoneNumber().equals(user.getPhoneNumber())) {
	        return user.getPhoneNumber(); // لا تغيير مطلوب
	    }

	    if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
	        throw new PhoneNumberAlreadyExistsException(request.getPhoneNumber());
	    }

	    user.setPhoneNumber(request.getPhoneNumber());
	    userRepository.save(user);

	    return request.getPhoneNumber();
	}
	
	
	
	public AddressDTO updateAddress(CreateAddressDTO dto) {
		
		User user=getCurrentUser();
		
		Address address=user.getAddress();
		
		if(address!=null)
			generalMapper.updateAddressFromDTO(dto, address);
		else {
			address=generalMapper.toEntity(dto);
			address.setUser(user);
			
		}
		
		
		addressRepository.save(address);
		
		return generalMapper.toDTO(address);
		
	}
	
	
	
	@Transactional
	//delete my account
	public void deleteMyAccount() {
	   
			    

	    User user = getCurrentUser();

	    // 1️⃣ حذف Refresh Tokens
	    refreshTokenService.deleteByUser(user);

	    user.setDeleted(true);
	    user.setDeletedAt(LocalDateTime.now());

	    user.setTokenVersion(user.getTokenVersion() + 1);

	
	    
	}
	
	
	

	
	
	
	
	

	
	
	    
		
	
	
	

	
	
}

//