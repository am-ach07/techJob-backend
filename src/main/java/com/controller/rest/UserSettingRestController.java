package com.controller.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.DTOs.UpdateProfileRequest;
import com.DTOs.user.AddressDTO;
import com.DTOs.user.CreateAddressDTO;
import com.DTOs.user.EmailRequest;
import com.DTOs.user.UpdatePasswordRequest;
import com.DTOs.user.UpdatePhoneNumberRequest;
import com.DTOs.user.UpdateUsernameRequest;
import com.response.ApiResponse;
import com.response.ApiResponseFactory;
import com.response.ProfileResponse;
import com.service.ImageService;
import com.service.UserSettingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/me")
public class UserSettingRestController {

	
	private final UserSettingService userSettingService;
	private final ImageService imageService;
	public UserSettingRestController(UserSettingService userSettingService, ImageService imageService) {
		this.userSettingService = userSettingService;
		this.imageService = imageService;
	}
	

	
	@GetMapping
	public ResponseEntity<ApiResponse<ProfileResponse>> showMyProfil() {
		ProfileResponse profile = userSettingService.getMyProfile();
		return ApiResponseFactory.ok(profile, "User fetched successfully");
	}
	


	@PutMapping
	public ResponseEntity<ApiResponse<ProfileResponse>> updateUser(
			@Valid @RequestBody UpdateProfileRequest dto) {
		
		
		ProfileResponse profile = userSettingService.updateMyProfile(dto);
		return ApiResponseFactory.ok(profile, "Profile updated successfully");
	}
	
	
	@DeleteMapping
	public ResponseEntity<ApiResponse<Void>> deleteUser() {
		userSettingService.deleteMyAccount();
		return ApiResponseFactory.ok(null, "User deleted successfully");
	}
	@GetMapping("/image")
	public ResponseEntity<ApiResponse<String>> getProfileImage() {
		String imageUrl = imageService.getProfileImageUrl();
		return ApiResponseFactory.ok(imageUrl, "Profile image fetched successfully");
	}
	@PostMapping("/image")
	public ResponseEntity<ApiResponse<String>> uploadProfileImage(
			 @RequestParam("file") MultipartFile file) {
		String url=imageService.uploadProfileImage(file);
		return ApiResponseFactory.ok(url, "Profile image uploaded successfully");
	}
	@PatchMapping("/image")
	public ResponseEntity<ApiResponse<String>> updateProfileImage(
			 @RequestParam("file") MultipartFile file) {
		String url= imageService.uploadProfileImage(file);
		return ApiResponseFactory.ok(url, "Profile image updated successfully");
	}
	@DeleteMapping("/image")
	public ResponseEntity<ApiResponse<Void>> deleteProfileImage() {
		imageService.deleteProfileImage();
		return ApiResponseFactory.ok(null, "Profile image deleted successfully");
	}
	@PutMapping("/email")
	public ResponseEntity<ApiResponse<String>> updateEmail(
			@RequestBody @Valid EmailRequest dto) {
		String email=userSettingService.updateEmail(dto);
		return ApiResponseFactory.ok(email, "Email updated successfully");
	}
	@PutMapping("/password")
	public ResponseEntity<ApiResponse<Void>> updatePassword(
			@RequestBody @Valid UpdatePasswordRequest dto) {
		userSettingService.updatePassword(dto);
		return ApiResponseFactory.ok(null, "Password updated successfully");
	}
	@PutMapping("/username")
	public ResponseEntity<ApiResponse<String>> updateUsername(
			@RequestBody @Valid UpdateUsernameRequest dto) {
		String username=userSettingService.updateUsername(dto);
		return ApiResponseFactory.ok(username, "Username updated successfully");
	}
	@PutMapping("/phone-number")
	public ResponseEntity<ApiResponse<String>> updatePhoneNumber(
			@RequestBody @Valid UpdatePhoneNumberRequest dto) {
		String phoneNumber=userSettingService.updatePhoneNumber(dto);
		return ApiResponseFactory.ok(phoneNumber, "Phone number updated successfully");
	}
	
	@PutMapping("/address")
	public ResponseEntity<ApiResponse<AddressDTO>> updateAddress(
			@RequestBody @Valid CreateAddressDTO dto) {
		AddressDTO address=userSettingService.updateAddress(dto);
		return ApiResponseFactory.ok(address, "Address updated successfully");
	}
	
	
	
	

	
}