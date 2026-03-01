package com.techJob.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.techJob.Constants;
import com.techJob.DTOs.PaginationAndSortDTO;
import com.techJob.DTOs.image.ImageDTO;
import com.techJob.DTOs.order.OrderDTO;
import com.techJob.DTOs.payments.PaymentDTO;
import com.techJob.DTOs.serviceOffer.ServiceOfferDTO;
import com.techJob.DTOs.user.UserDTO;
import com.techJob.domain.entity.Image;
import com.techJob.domain.entity.Order;
import com.techJob.domain.entity.ServiceOffer;
import com.techJob.domain.entity.User;
import com.techJob.domain.enums.OffersStatus;
import com.techJob.domain.enums.Roles;
import com.techJob.exception.offer.OfferNotFoundException;
import com.techJob.exception.order.InvalidOrderException;
import com.techJob.exception.order.OrderNotFoundException;
import com.techJob.exception.user.UserNotFoundException;
import com.techJob.mapper.GeneralMapper;
import com.techJob.repository.OrderRepository;
import com.techJob.repository.ServiceOfferRepository;
import com.techJob.repository.UserRepository;
import com.techJob.response.ProfileResponse;

import jakarta.transaction.Transactional;

@Service
public class AdminService {

	
	
	
	private static final Logger log = LoggerFactory.getLogger(AdminService.class);

    private final ServiceOfferRepository serviceOfferRepository;
    private final UserRepository userRepository;
    private final GeneralMapper generalMapper;
    private final AuthAuditLogService auditLogService;
    private final RefreshTokenService refreshTokenService;
    private final NotificationService notificationService;

	private final OrderRepository orderRepository;

    public AdminService(
    		ServiceOfferRepository serviceOfferRepository,
    		UserRepository userRepository,
    		AuthAuditLogService auditLogService,
    		GeneralMapper generalMapper,
    		RefreshTokenService refreshTokenService,
    		NotificationService notificationService,
    		OrderRepository orderRepository) {
        this.serviceOfferRepository = serviceOfferRepository;
        this.userRepository = userRepository;
		this.auditLogService=auditLogService;
        this.generalMapper = generalMapper;
		this.refreshTokenService = refreshTokenService;
		this.notificationService = notificationService;
		this.orderRepository = orderRepository;
    }
    
    
    
    
    
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByUsernameOrEmail(email, email)
                .orElseThrow(() -> new UserNotFoundException(email));
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
 
    private ServiceOfferDTO mapWithProfile(ServiceOffer offer) {

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
            
            profile.getUser().
            setProfilImageUrl("/uploads/profiles/default-profiles.png")
            ;
        }

        dto.setProfile(profile);

        return dto;
    }

    
    private OrderDTO mapWithProfile(Order order) {

        OrderDTO dto = generalMapper.toDTO(order);

        

        UserDTO client=generalMapper.toDTO(order.getClient());

        // Profile image null-safe handling
        String profileImage = client.getProfilImageUrl();

        if (profileImage == null) {
            client.setProfilImageUrl("/uploads/profiles/default-profiles.png"
            );
        }

        dto.setClient(client);
	    dto.setOfferPath(Constants.PATH_ME_OFFERS+ order.getService().getOfferPublicID());

	    if(order.getPayment()!=null &&!order.getPayment().isEmpty()) {
	    		        PaymentDTO paymentDTO = generalMapper.toDTO(order.getPayment().get(0));
	    		        paymentDTO.setPaymentUrl(Constants.PATH_ME_PAYMENTS + paymentDTO.getPaymentPublicID());
	        dto.setPayment(List.of(paymentDTO));
	    }
	    
        return dto;
    }
	
	
	
	
	
	
	// Soft delete offer by admin (set OffersStatus to DELETED)
    @Transactional
    public void deleteOfferByAdmin(String offerPublicID) {

        User user = getCurrentUser();
        verifyAdmin(user);

        ServiceOffer offer = getOfferByPublicIDOrThrow(offerPublicID);

        offer.setOffersStatus(OffersStatus.DELETED);
        offer.setDeletedAt(LocalDateTime.now());

        serviceOfferRepository.save(offer);

        log.info("Offer permanently deleted by admin {}", offerPublicID);
    }
    
    
  //get all offer by admin--------------------------------------------------------------------------------
    public Page<ServiceOfferDTO> getAllOffer(PaginationAndSortDTO dto) {
        User user = getCurrentUser();
        verifyAdmin(user);
        Pageable pageable = PageRequest.of(
                dto.getPage(),
                dto.getSize(),
                dto.getSort() != null ? dto.getSort().toSpringSort() : Sort.by("createdAt").descending()
        );
        Page<ServiceOffer> page = serviceOfferRepository.findAll(pageable);
        log.info("Admin fetched all offers");
        return page.map(this::mapWithProfile);
    }
    
    
    
  //get offer by publicID for admin--------------------------------------------------------------------------------
    public ServiceOfferDTO getOfferByPublicIDForAdmin(String offerPublicID) {
        User user = getCurrentUser();
        verifyAdmin(user);
        ServiceOffer offer = getOfferByPublicIDOrThrow(offerPublicID);
        log.info("Admin fetched offer {}", offerPublicID);
        return mapWithProfile(offer);
    }
	
	
	
  //get all users for admin
  	public Page<ProfileResponse> getAllUsers(PaginationAndSortDTO dto) {
  		User admin=getCurrentUser();
  		verifyAdmin(admin);
  	    Pageable pageable = PageRequest.of(
  	            dto.getPage(),
  	            dto.getSize(),
  	            dto.getSort() != null
  	                    ? dto.getSort().toSpringSort()
  	                    : Sort.by("createdAt").ascending()
  	    );

  	    Page<User> page = userRepository.findAll(pageable);

  	    // تحويل كل مستخدم إلى ProfileResponse
  	    Page<ProfileResponse> profileResponses = page.map(user -> {
  	        ProfileResponse response = new ProfileResponse();
  	        response.setUser(generalMapper.toDTO(user));
  	        if (user.getArtisanProfile() != null) {
  	            response.setArtisan(generalMapper.toDTO(user.getArtisanProfile()));
  	        }
  	        return response;
  	    });
  	    return profileResponses;
  	}
  	

  	//get user by public ID for admin
  	public ProfileResponse getUserByPublicID(String publicID) {
  		User admin=getCurrentUser();
  		verifyAdmin(admin);
  		User user = userRepository
  				.findByPublicID(publicID)
  				.orElseThrow(() -> new UserNotFoundException(publicID));
  		
  		ProfileResponse response = new ProfileResponse();
  	    response.setUser(generalMapper.toDTO(user));

  	    if (user.getArtisanProfile() != null) {
  	        response.setArtisan(
  	            generalMapper.toDTO(user.getArtisanProfile())
  	        );
  	    }

  	    return response;
  	}
  	
  	// delete user by public ID for admin
  	
  	public void deleteUserByPublicID(String publicID) {
  		User admin=getCurrentUser();
  		verifyAdmin(admin);
  		User user = userRepository
  				.findByPublicID(publicID)
  				.orElseThrow(() -> new UserNotFoundException(publicID));
  		// 1️⃣ حذف Refresh Tokens
  	    refreshTokenService.deleteByUser(user);

  	    // delete notification
  	    notificationService.deleteByUser(user);
  		userRepository.delete(user);
  	}
	

	//get order by publicID (admin)
		public OrderDTO getByPublicID(String publicID) {
			User user=getCurrentUser();
			verifyAdmin(user);
			Order order=orderRepository.findByOrderPublicID(publicID)
					.orElseThrow(()->new OrderNotFoundException(publicID));
			
			return mapWithProfile(order);
		
		}
	
	





	//get all orders (admin)
	public Page<OrderDTO> getAllOrders(PaginationAndSortDTO dto) {

		User user=getCurrentUser();
		
		verifyAdmin(user);
	    Pageable pageable = PageRequest.of(
	            dto.getPage(),
	            dto.getSize(),
	            dto.getSort() != null
	                    ? dto.getSort().toSpringSort()
	                    : Sort.by("createdAt").descending()
	    );

	    Page<Order> page = orderRepository.findAll(pageable);

	    return page.map(this::mapWithProfile);
	}
	
}
