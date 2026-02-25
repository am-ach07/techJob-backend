package com.techJob.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.techJob.Constants;
import com.techJob.DTOs.PaginationAndSortDTO;
import com.techJob.DTOs.image.ImageDTO;
import com.techJob.DTOs.order.CreateOrderDTO;
import com.techJob.DTOs.order.OrderDTO;
import com.techJob.DTOs.payments.PaymentDTO;
import com.techJob.DTOs.user.UserDTO;
import com.techJob.domain.entity.ArtisanProfile;
import com.techJob.domain.entity.ExtraServiceOffer;
import com.techJob.domain.entity.Order;
import com.techJob.domain.entity.OrderExtra;
import com.techJob.domain.entity.ServiceOffer;
import com.techJob.domain.entity.User;
import com.techJob.domain.enums.DepositStatus;
import com.techJob.domain.enums.FinalStatus;
import com.techJob.domain.enums.OffersStatus;
import com.techJob.domain.enums.OrdersStatus;
import com.techJob.domain.enums.PaymentStatus;
import com.techJob.domain.enums.Roles;
import com.techJob.exception.InvalidActionException;
import com.techJob.exception.emailVerification.AccountNotVerifiedException;
import com.techJob.exception.offer.InvalidOfferException;
import com.techJob.exception.offer.OfferNotFoundException;
import com.techJob.exception.order.InvalidOrderException;
import com.techJob.exception.order.OrderNotFoundException;
import com.techJob.exception.user.UserNotFoundException;
import com.techJob.mapper.GeneralMapper;
import com.techJob.repository.ArtisanProfileRepository;
import com.techJob.repository.ExtraServiceOfferRepository;
import com.techJob.repository.OrderExtraRepository;
import com.techJob.repository.OrderRepository;
import com.techJob.repository.ServiceOfferRepository;
import com.techJob.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class OrderService {

	
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
	
	
	private final OrderRepository orderRepository;
	private final OrderExtraRepository orderExtraRepository;
	private final NotificationsServiceImp notificationsServiceImp;
	private final UserRepository userRepository;
	private final GeneralMapper generalMapper;
	private final ExtraServiceOfferRepository extraServiceOfferRepository;
	private final ServiceOfferRepository serviceOfferRepository;
	private final ArtisanProfileRepository artisanProfileRepository;
	private final PaymentService paymentService;
	
	
	
	public OrderService(
			OrderRepository orderRepository,
			OrderExtraRepository orderExtraRepository,
			NotificationsServiceImp notificationsServiceImp,
			UserRepository userRepository,
			ArtisanProfileRepository artisanProfileRepository,
			GeneralMapper generalMapper,
			ExtraServiceOfferRepository extraServiceOfferRepository,
			ServiceOfferRepository serviceOfferRepository,
			PaymentService paymentService) {
		super();
		this.orderRepository = orderRepository;
		this.orderExtraRepository = orderExtraRepository;
		this.notificationsServiceImp = notificationsServiceImp;
		this.userRepository = userRepository;
		this.artisanProfileRepository=artisanProfileRepository;
		this.generalMapper = generalMapper;
		this.extraServiceOfferRepository = extraServiceOfferRepository;
		this.serviceOfferRepository = serviceOfferRepository;
		this.paymentService = paymentService;
	}
	private User getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    String email = auth.getName();

	    return userRepository
	            .findByUsernameOrEmail(email, email)
	            .orElseThrow(() -> new UserNotFoundException(email));
	}
	//verify admin role
	 private void verficationAdmin(User user) {
			if(!user.getRole().equals(Roles.ADMIN)) {
				throw new InvalidOrderException("You are not allowed to view this order");
			}
		}
	 //verify email verification
	private void verificationEmail(User user) {
		if(!user.getEmailVerified())
	    	throw new AccountNotVerifiedException("Please verify your email to view your orders.");
	    
	}
	//calcul time 
	private void cancelOrderAfterAccepted(Order order) {
		 Duration duration = Duration.between(
				 order.getAcceptedAt(), LocalDateTime.now());
				 
			    

			    if(duration.toMinutes() > 60) {
			        throw new InvalidOrderException("Cancel time expired");
			    }
			    order.setCancelledAt(LocalDateTime.now());
			    order.setStatus(OrdersStatus.CANCELED);
	}
	
	private Order validateArtisanAndGetOrder(String orderPublicId) {

	    User user = getCurrentUser();
	        verificationEmail(user);
	    

	    if (user.getArtisanProfile() == null) {
	        throw new InvalidOrderException("You are not an artisan");
	    }

	    Order order = orderRepository.findByOrderPublicID(orderPublicId)
	            .orElseThrow(() -> new OrderNotFoundException(orderPublicId));

	    if (!order.getService().getArtisan().equals(user.getArtisanProfile())) {
	        throw new InvalidOrderException("You are not allowed to access this order");
	    }

	    return order;
	}

	private OrderDTO mapOrderWithProfile(Order order) {

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

        return dto;
    }
	//reaction of artisan
	private OrderDTO acceptOrder(Order order) {
                
        
        if (order.getStatus() != OrdersStatus.PENDING_ARTISAN_ACCEPTANCE) {
            throw new InvalidActionException("Invalid Action ");
        }
        order.setAcceptedAt(LocalDateTime.now());
        order.setStatus(OrdersStatus.ACCEPTED_PENDING_PAYMENT);
        order.setPaymentStatus(PaymentStatus.UNPAID);
        notificationsServiceImp.createNotification(
                order.getClient().getPublicID(),
                "Order Update",
                "Your order was accepted. Please complete the deposit payment."

        );
        return mapOrderWithProfile(order);
    }
	//reject order
	private OrderDTO rejectOrder(Order order) {
                
        
        if (order.getStatus() != OrdersStatus.PENDING_ARTISAN_ACCEPTANCE) {
            throw new InvalidOrderException("Invalid Action !!!");
        }
        order.setStatus(OrdersStatus.REJECTED);
        
        notificationsServiceImp.createNotification(
                order.getClient().getPublicID(),
                "Order Update",
                "Your order has been " +OrdersStatus.REJECTED.toString().toLowerCase()
        );
        return mapOrderWithProfile(order);
    }
	//cancel order 
	
	private OrderDTO cancelOrder(Order order) {
        
        if(order.getStatus()!=OrdersStatus.ACCEPTED_PENDING_PAYMENT)
        	throw new InvalidActionException("Invalid Action !!!");
        
        	cancelOrderAfterAccepted(order);
        
        notificationsServiceImp.createNotification(
                order.getClient().getPublicID(),
                "Order Update",
                " Order"+order.getOrderPublicID()+" has been " +OrdersStatus.CANCELED.toString().toLowerCase()
        );
		return mapOrderWithProfile(order);
		
	}
	//complete order 
	private OrderDTO completeOrder(Order order) {

		if (order.getStatus() != OrdersStatus.IN_PROGRESS) {
		    throw new InvalidActionException("Order must be in progress before completion");
		}


	    order.setCompletedAt(LocalDateTime.now());
	    order.setStatus(OrdersStatus.COMPLETED);

	    ArtisanProfile artisan = order.getService().getArtisan();

	    // atomic increment (recommended)
	    artisanProfileRepository.incrementCompletedOrders(artisan.getId());

	    try {
	        notificationsServiceImp.createNotification(
	                order.getClient().getPublicID(),
	                "Order Update",
	                "Your order has been completed"
	        );
	    } catch (Exception e) {
	        log.error("Notification failed for completed order {}", order.getOrderPublicID());
	    }

	    return mapOrderWithProfile(order);
	}

	
	// payment
	private void advancePayment(Order order) {
	    PaymentDTO dto = paymentService.createDepositPayment(order);

	    notificationsServiceImp.createNotification(
	        order.getClient().getPublicID(),
	        "Complete payment",
	        "Please pay the deposit: " + dto.getPaymentUrl()
	    );
	}

	 	    
	// get my orders (as client)
	public Page<OrderDTO> clientOrders(PaginationAndSortDTO dto) {

	    User client = getCurrentUser();
	    
	    verificationEmail(client);
	    
	    
	    Pageable pageable = PageRequest.of(
	            dto.getPage(),
	            dto.getSize(),
	            dto.getSort() != null
	                    ? dto.getSort().toSpringSort()
	                    : Sort.by("createdAt").descending()
	    );

	    Page<Order> page = orderRepository.findByClient(client, pageable);

	    return page.map(this::mapOrderWithProfile);
	}
	// get my orders (as artisan)
		public Page<OrderDTO> artisanOrders(PaginationAndSortDTO dto) {

			 User user = getCurrentUser();

			 verificationEmail(user);
			    if (user.getArtisanProfile() == null) {
			        throw new InvalidOrderException("You are not an artisan");
			    }

			    Pageable pageable = PageRequest.of(
			            dto.getPage(),
			            dto.getSize(),
			            dto.getSort() != null
			                    ? dto.getSort().toSpringSort()
			                    : Sort.by("createdAt").descending()
			    );

			    Page<Order> page = orderRepository.findByArtisan(
			            user.getArtisanProfile(),
			            pageable
			    );

			    return page.map(this::mapOrderWithProfile);
			}
		//get order by publicID (as artisan&&as client)
		public OrderDTO getOrderByPublicID(String orderPublicID) {

		    User user = getCurrentUser();
		    verificationEmail(user);
		    Order order = orderRepository.findByOrderPublicID(orderPublicID)
		            .orElseThrow(() -> new OrderNotFoundException(orderPublicID));

		    // تحقق الملكية (عن طريق الزبون أو الحرفي)
		    if (!order.getClient().equals(user) &&
		        (user.getArtisanProfile() == null ||
		         !order.getService().getArtisan().equals(user.getArtisanProfile()))) {
		        throw new InvalidOrderException("You are not allowed to view this order");
		    }
		    return mapOrderWithProfile(order);
		    
		}
	
	//send a request order
		@Transactional
		public OrderDTO createOrder(String offerPublicID, CreateOrderDTO dto) {
		    User client = getCurrentUser();

		        verificationEmail(client);
		    

		    // جلب العرض الأساسي
		    ServiceOffer offer = serviceOfferRepository.findByOfferPublicIDAndOffersStatus(
		            offerPublicID, OffersStatus.ACTIVE
		    ).orElseThrow(() -> new OfferNotFoundException(offerPublicID));

		    if (offer.getArtisan().getUser().equals(client)) {
		        throw new InvalidOfferException("You cannot order your own service");
		    }

		    boolean hasActiveOrder = orderRepository.existsByClientAndServiceAndStatusIn(
		            client,
		            offer,
		            List.of(OrdersStatus.PENDING_ARTISAN_ACCEPTANCE, OrdersStatus.ACCEPTED)
		    );
		    if (hasActiveOrder) {
		        throw new InvalidOrderException("You already have an active order for this service");
		    }

		    // إنشاء الطلب
		    Order order = new Order();
		    order.setClient(client);
		    order.setService(offer);
		    order.setOfferTitleSnapshot(offer.getTitle());
		    order.setOfferPriceSnapshot(offer.getPrice());
		    order.setStatus(OrdersStatus.PENDING_ARTISAN_ACCEPTANCE);
		    order.setOrderPublicID(UUID.randomUUID().toString());

		    //payment
		    order.setDepositStatus(DepositStatus.UNPAID);
		    order.setFinalStatus(FinalStatus.UNPAID);
		    order.setPaymentStatus(PaymentStatus.UNPAID);

		    
		    BigDecimal total = order.getTotalPriceSnapshot();

		    BigDecimal deposit = total.multiply(BigDecimal.valueOf(0.30));
		    BigDecimal finalAmount = total.subtract(deposit);

		    order.setDepositAmount(deposit);
		    order.setFinalAmount(finalAmount);

		    
		    
		    
		    // التعامل مع العروض الإضافية
		    BigDecimal totalExtrasPrice = BigDecimal.ZERO;
		    Set<OrderExtra> extrasList = new HashSet<>();
		   if(dto!=null)
		    if (dto.getExtraOfferPublicID() != null && !dto.getExtraOfferPublicID().isEmpty()) {
		        for (String extraOfferPublicID : dto.getExtraOfferPublicID()) {

		            ExtraServiceOffer extra = extraServiceOfferRepository
		                    .findByExtraOfferPublicID(extraOfferPublicID)
		                    .orElseThrow(() -> new InvalidOfferException(
		                            "Extra service not found: " + extraOfferPublicID
		                    ));

		            // تحقق أن Extra مرتبط بنفس ServiceOffer
		            if (!extra.getServiceOffer().equals(offer)) {
		                throw new InvalidOfferException(
		                        "Extra service " + extraOfferPublicID + " is not linked to this service"
		                );
		            }

		            // حفظ Snapshot لكل Extra
		            OrderExtra orderExtra = new OrderExtra();
		            orderExtra.setOrder(order);
		            orderExtra.setExtra(extra);
		            orderExtra.setExtraTitleSnapshot(extra.getTitle());
		            orderExtra.setExtraPriceSnapshot(extra.getPrice());
		            extrasList.add(orderExtra);

		            totalExtrasPrice = totalExtrasPrice.add(extra.getPrice());
		        }
		    }

		    order.setExtras(extrasList);
		    order.setTotalPriceSnapshot(offer.getPrice().add(totalExtrasPrice));

		    // حفظ الطلب (Extras سيتم حفظها تلقائيًا عبر Cascade)
		    orderRepository.save(order);

		    
		    // Logging أساسي
		    log.info("Order {} created by user {} for service {}. Total price: {}",
		            order.getOrderPublicID(), client.getUsername(), offer.getTitle(), order.getTotalPriceSnapshot());

		    // إشعارات ديناميكية
		    try {
		        notificationsServiceImp.createNotification(
		                offer.getArtisan().getUser().getPublicID(),
		                "New Order: " + offer.getTitle(),
		                "You received a new order from " + client.getUsername() +
		                        ". Total: $" + order.getTotalPriceSnapshot()
		        );
		        notificationsServiceImp.createNotification(
		                client.getPublicID(),
		                "Order Created: " + offer.getTitle(),
		                "Your order has been placed successfully. Total: $" + order.getTotalPriceSnapshot()
		        );
		    } catch (Exception e) {
		        log.error("Failed to send notifications for order {}: {}", order.getOrderPublicID(), e.getMessage());
		        // لا نرمي الاستثناء حتى لا يفشل حفظ الطلب
		    }

		    return mapOrderWithProfile(order);
		}



	
	


	//respond to order 
	@Transactional
	public OrderDTO respondToOrder(String orderPublicId, OrdersStatus status) {

	    Order order = validateArtisanAndGetOrder(orderPublicId);

	    switch (status) {

	        case ACCEPTED -> {
	        	acceptOrder(order);
	        	//advance payment
			    advancePayment(order);
			    
	        }

	        case REJECTED -> rejectOrder(order);

	        case COMPLETED -> completeOrder(order);

	        case CANCELED -> cancelOrder(order);

	        default -> throw new InvalidActionException("Invalid action");
	    }

	    return mapOrderWithProfile(order);
	}

	
	
	
	//get order by publicID (admin)
		public OrderDTO getByPublicID(String publicID) {
			User user=getCurrentUser();
			verficationAdmin(user);
			Order order=orderRepository.findByOrderPublicID(publicID)
					.orElseThrow(()->new OrderNotFoundException(publicID));
			
			return mapOrderWithProfile(order);
		
		}
	
	//get all orders (admin)
	public Page<OrderDTO> getAllOrders(PaginationAndSortDTO dto) {

		User user=getCurrentUser();
		
		verficationAdmin(user);
	    Pageable pageable = PageRequest.of(
	            dto.getPage(),
	            dto.getSize(),
	            dto.getSort() != null
	                    ? dto.getSort().toSpringSort()
	                    : Sort.by("createdAt").descending()
	    );

	    Page<Order> page = orderRepository.findAll(pageable);

	    return page.map(this::mapOrderWithProfile);
	}
	
	
}