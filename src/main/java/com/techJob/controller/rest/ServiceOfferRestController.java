package com.techJob.controller.rest;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.techJob.DTOs.PaginationAndSortDTO;
import com.techJob.DTOs.image.ImageDTO;
import com.techJob.DTOs.serviceOffer.CreateExtraServiceOfferDTO;
import com.techJob.DTOs.serviceOffer.CreateServiceOfferRequest;
import com.techJob.DTOs.serviceOffer.ExtraServiceOfferDTO;
import com.techJob.DTOs.serviceOffer.ServiceOfferDTO;
import com.techJob.DTOs.serviceOffer.UpdateExtraServiceOfferDTO;
import com.techJob.DTOs.serviceOffer.UpdateServiceOfferDTO;
import com.techJob.domain.enums.OffersStatus;
import com.techJob.mapper.ReaderJson;
import com.techJob.response.ApiResponse;
import com.techJob.response.ApiResponseFactory;
import com.techJob.service.ExtraServiceOfferService;
import com.techJob.service.ImageService;
import com.techJob.service.ServiceOfferService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/v1")
public class ServiceOfferRestController {
	private final ReaderJson readerJson;
	private final ServiceOfferService serviceOfferService;
	private final ImageService imageService;
	private final ExtraServiceOfferService extraServiceOfferService;

	
	
	public ServiceOfferRestController(
			ServiceOfferService serviceOfferService,
			ReaderJson readerJson,
			ImageService imageService,
			ExtraServiceOfferService extraServiceOfferService) {
		super();
		this.serviceOfferService = serviceOfferService;
		this.readerJson=readerJson;
		this.imageService=imageService;
		this.extraServiceOfferService = extraServiceOfferService;
	}

//============================show all offers of artisan ======================================
	@PreAuthorize("hasRole('ARTISAN')")
	@GetMapping("/me/offers")
	public ResponseEntity<ApiResponse<Page<ServiceOfferDTO>>> getMyOffer(
			@ModelAttribute @Valid PaginationAndSortDTO dto) {
		Page <ServiceOfferDTO> page=serviceOfferService.getMyOffer(dto);
		return ApiResponseFactory.ok(page, "Offers fetched successfully");
	}

//============================create an offer by artisan ============================================
	@PreAuthorize("hasRole('ARTISAN')")
	@PostMapping(
		    value = "/me/offers",
		    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
		)
		public ResponseEntity<ApiResponse<ServiceOfferDTO>> createOffer(
		        @RequestPart("data") @Valid CreateServiceOfferRequest dto,
		        @RequestPart(value = "file", required = false) MultipartFile[] file
		) {

		
			ServiceOfferDTO offer=serviceOfferService.createOffer(dto, file);
		

		    return ApiResponseFactory.ok(offer, "Offer created successfully");
		}
	
	
	//=============================show one offer of artian ===================================
	
	@PreAuthorize("hasRole('ARTISAN')")
	@GetMapping("/me/offers/{offerId}")
	public ResponseEntity<ApiResponse<ServiceOfferDTO>> getMyOffer(
			@PathVariable String offerId){
		
		ServiceOfferDTO offer = serviceOfferService.getMyOfferByPublicID(offerId);
		
		
		
		return ApiResponseFactory.ok(offer, "Offer fetched successfully");
	}
	
	
	//============================update offer of artisan ========================
	
	@PreAuthorize("hasRole('ARTISAN')")
	@PatchMapping("/me/offers/{offerId}")
	public ResponseEntity<ApiResponse<ServiceOfferDTO>> updateMyOffer(
			@PathVariable String offerId,
			@Valid @RequestBody UpdateServiceOfferDTO dto){
		
		
		
		ServiceOfferDTO offer = serviceOfferService.updateMyOffer(dto, offerId);
		
		
		return ApiResponseFactory.ok(offer, "Offer updated successfully");

	}
	
	
	//============================delete offer of artisan =======================================
	
	
	@PreAuthorize("hasRole('ARTISAN')")
	@DeleteMapping("/me/offers/{offerId}")
	public ResponseEntity<ApiResponse<Void>> deleteMyOffer(
			@PathVariable String offerId){
		
		 serviceOfferService.deleteMyOffer(offerId);
		
		
		
		return ApiResponseFactory.ok(null, "Offer fetched successfully");
	}
	
	
	//==================update status of offer of artisan ============================
	@PreAuthorize("hasRole('ARTISAN')")
	@PatchMapping("/me/offers/{offerId}/status")
	public ResponseEntity<ApiResponse<ServiceOfferDTO>> updateStatusOfMyOffer(
			@PathVariable String offerId,
			@RequestParam OffersStatus status){
		
		
		
		ServiceOfferDTO offer = serviceOfferService.updateStatusMyOffer(offerId, status);
		
		
		return ApiResponseFactory.ok(offer, "Offer Status  updated successfully");

	}
	
	
	
	//=======================image offers===========================================
	//================show images of offers=================================
	@PreAuthorize("hasRole('ARTISAN')")
	@GetMapping("/me/offers/{offerId}/images")
	public ResponseEntity<ApiResponse<List<ImageDTO>>> getMyOffersImages(
			@PathVariable String offerId){
		
		
		List<ImageDTO> image=imageService.getMyImage(offerId);
		
		
		return ApiResponseFactory.ok(image, "images fetched successfully");
		
		
	}
	//=============================add image to offers ================================
	@PreAuthorize("hasRole('ARTISAN')")
	@PostMapping("/me/offers/{offerId}/images")
	public ResponseEntity<ApiResponse<List<ImageDTO>>> addImagesToOffers(
			@PathVariable String offerId,
			@RequestParam("file") MultipartFile[] file){
		
		
		List<ImageDTO> image =imageService.uploadOfferImage(offerId, file);
		
		
		return ApiResponseFactory.ok(image, "images created successfully");	
	}
	
	//=======================update image of offers===============================
	
	@PreAuthorize("hasRole('ARTISAN')")
	@PatchMapping("/me/offers/{offerId}/images/{imageId}")
	public ResponseEntity<ApiResponse<ImageDTO>> updateImageOfOffers(
			@PathVariable String offerId,
			@PathVariable String imageId,
			@RequestParam("file") MultipartFile file){
		
		
		ImageDTO image =imageService.updateOfferImage(offerId, imageId, file);
		
		
		return ApiResponseFactory.ok(image, "images updated successfully");	
	}
	//===================================delete image of offer=====================================
	@PreAuthorize("hasRole('ARTISAN')")
	@DeleteMapping("/me/offers/{offerId}/images/{imageId}")
	public ResponseEntity<ApiResponse<ImageDTO>> deleteImageOfOffers(
			@PathVariable String offerId,
			@PathVariable String imageId){
		
		
		imageService.deleteOfferImage(offerId, imageId);
		
		
		return ApiResponseFactory.ok(null, "images deleted successfully");	
	}

	//===========================extras offers============================
	//==========================get extras offers of offers================================
	
	@PreAuthorize("hasRole('ARTISAN')")
	@GetMapping("/me/offers/{offerId}/extras")
	public ResponseEntity<ApiResponse<List<ExtraServiceOfferDTO>>> getExtrasServicesOfOffer(
			@PathVariable String offerId){
		
		
		List<ExtraServiceOfferDTO> dto= extraServiceOfferService.getMyExtraServicesForOffer(offerId);
		
		
		return ApiResponseFactory.ok(dto, "extras services fetched successfully");	
	}
	
	//=========================add extra services to offers==============================
	@PreAuthorize("hasRole('ARTISAN')")
	@PostMapping("/me/offers/{offerId}/extras")
	public ResponseEntity<ApiResponse<Set<ExtraServiceOfferDTO>>> addExtrasServicesToOffer(
			@PathVariable String offerId,
			@RequestBody @Valid Set<CreateExtraServiceOfferDTO> dto){
		
		
		Set<ExtraServiceOfferDTO> extraService= extraServiceOfferService.createExtraServiceOffer(dto, offerId);
		
		
		return ApiResponseFactory.ok(extraService, "extras services created successfully");	
	}
	//====================edit extra service of offer =========================================
	@PreAuthorize("hasRole('ARTISAN')")
	@PatchMapping("/me/offers/{offerId}/extras/{extraId}")
	public ResponseEntity<ApiResponse<ExtraServiceOfferDTO>> updateExtrasServicesToOffer(
			@PathVariable String offerId,
			@PathVariable String extraId,
			@RequestBody @Valid UpdateExtraServiceOfferDTO dto){
		
		
		ExtraServiceOfferDTO extraService= extraServiceOfferService.updateExtraServiceOffer(dto, offerId, extraId);
		
		
		return ApiResponseFactory.ok(extraService, "extras services updated successfully");	
	}
	//=================== delete extra service of offer=======================================
	@PreAuthorize("hasRole('ARTISAN')")
	@DeleteMapping("/me/offers/{offerId}/extras/{extraId}")
	public ResponseEntity<ApiResponse<Void>> deleteExtrasServicesToOffer(
			@PathVariable String offerId,
			@PathVariable String extraId){
		
		
		extraServiceOfferService.deleteExtraServiceOffer(offerId, extraId);
		
		return ApiResponseFactory.ok(null, "extras services deleted successfully");	
	}
	//====================public endPoint offers ======================================
	//===========================get public offers========================================
	
	@GetMapping("/offers")
	public ResponseEntity<ApiResponse<Page<ServiceOfferDTO>>> getPublicOffers(
			@Valid @ModelAttribute PaginationAndSortDTO dto){
		
		 Page <ServiceOfferDTO> page =serviceOfferService.getPublicOffer(dto);
		 
		return ApiResponseFactory.ok(page, "public offers fetched successfully");	
	}
	
	//==========================get one public offer===================================
	@GetMapping("/offers/{offerId}")
	public ResponseEntity<ApiResponse<ServiceOfferDTO>> getPublicOfferByPublicID(
			@PathVariable String offerId){
		
		  ServiceOfferDTO offer =serviceOfferService.getOfferByPublicID(offerId);
		 
		return ApiResponseFactory.ok(offer, "public offer fetched successfully");	
	}
	//==========================get  public images of offers===================================
		@GetMapping("/offers/{offerId}/images")
		public ResponseEntity<ApiResponse<List<ImageDTO>>> getPublicImagesOfOffers(
				@PathVariable String offerId){
			
			  List <ImageDTO> images=imageService.getPublicImage(offerId);
			 
			return ApiResponseFactory.ok(images, "public images fetched successfully");	
		}
	//===================get one public image of offers============================================
		@GetMapping("/offers/{offerId}/images/{imageId}")
		public ResponseEntity<ApiResponse<ImageDTO>> getPublicImageOfOffersByImageId(
				@PathVariable String offerId,
				@PathVariable String imageId){
			
			ImageDTO images=imageService.getPublicImageByPublicID(offerId, imageId);
			 
			return ApiResponseFactory.ok(images, "public images fetched successfully");	
		}
	
}
