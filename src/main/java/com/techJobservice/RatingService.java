package com.techJobservice;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.techJob.DTOs.rating.CreateRatingDTO;
import com.techJob.DTOs.rating.RatingDTO;
import com.techJob.domain.entity.ArtisanProfile;
import com.techJob.domain.entity.Order;
import com.techJob.domain.entity.Rating;
import com.techJob.domain.entity.User;
import com.techJob.domain.enums.OrdersStatus;
import com.techJob.domain.enums.Roles;
import com.techJob.exception.emailVerification.AccountNotVerifiedException;
import com.techJob.exception.order.InvalidOrderException;
import com.techJob.exception.order.OrderNotFoundException;
import com.techJob.exception.user.UserNotFoundException;
import com.techJob.mapper.GeneralMapper;
import com.techJob.repository.ArtisanProfileRepository;
import com.techJob.repository.OrderRepository;
import com.techJob.repository.RatingRepository;
import com.techJob.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class RatingService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;
    private final GeneralMapper generalMapper;
    private final ArtisanProfileRepository artisanProfileRepository;
    private final NotificationsServiceImp notificationsServiceImp;

    public RatingService(
            OrderRepository orderRepository,
            ArtisanProfileRepository artisanProfileRepository,
            UserRepository userRepository,
            RatingRepository ratingRepository,
            GeneralMapper generalMapper,
            NotificationsServiceImp notificationsServiceImp
    ) {
        this.orderRepository = orderRepository;
        this.artisanProfileRepository = artisanProfileRepository;
        this.ratingRepository = ratingRepository;
        this.generalMapper = generalMapper;
        this.userRepository=userRepository;
        this.notificationsServiceImp = notificationsServiceImp;
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

    // ===== Incremental Average Calculation =====
    private Double calculateNewAverage(ArtisanProfile artisan, Double newRating) {

        Integer count = artisan.getRatingCount();
        Double currentAvg = artisan.getAverageRating();

        if (count==0.0) {
            return newRating;
        }

        return ((currentAvg * count) + newRating) / (count + 1);
    }

    private Order  validateUserAndStatus(String orderPublicId) {
    	
    	User user = getCurrentUser();
        verificationEmail(user);
        Order order = orderRepository.findByOrderPublicID(orderPublicId)
                .orElseThrow(() -> new OrderNotFoundException(orderPublicId));

        // Only client can rate
        if (!order.getClient().equals(user)) {
            throw new InvalidOrderException("Not your order");
        }

        if (order.getStatus() != OrdersStatus.COMPLETED) {
            throw new InvalidOrderException("Order not completed yet");
        }
        return order;
    }
    
    
    
    
    // ================= CREATE RATING =================

    //put rating
    @Transactional
    public RatingDTO rateOrder(String orderPublicId, CreateRatingDTO dto) {
    	
    	Order order=validateUserAndStatus(orderPublicId);
    	if (ratingRepository.existsByOrder(order)) {
    		return updateRating(order, dto);
        }
    	else return newRating(order, dto);
    	
    }
    
    
    
    private RatingDTO newRating(Order order, CreateRatingDTO dto) {

        
        Rating rating = new Rating();
        rating.setOrder(order);
        rating.setStars(dto.getStars());
        rating.setComment(dto.getComment());

        ArtisanProfile artisan = rating.getOrder().getService().getArtisan();

        // update artisan rating stats
        Double newAverage = calculateNewAverage(artisan, dto.getStars());

        artisan.setAverageRating(newAverage);
        artisan.setRatingCount(artisan.getRatingCount() + 1);

        ratingRepository.save(rating);
        artisanProfileRepository.save(artisan);

        notificationsServiceImp.createNotification(
                artisan.getUser().getPublicID(),
                "New Rating",
                "You received a new rating"
        );

        return generalMapper.toDTO(rating);
    }

    // ================= UPDATE RATING =================

    private RatingDTO updateRating(Order order, CreateRatingDTO dto) {

        

        Rating rating = ratingRepository.findByOrder(order)
                .orElseThrow(() -> new InvalidOrderException("Order not rated"));

        Double oldStars = rating.getStars();

        // update rating fields
        generalMapper.updateRatingFromDTO(dto, rating);

        ArtisanProfile artisan = rating.getOrder().getService().getArtisan();

        // ===== recalculate average correctly =====
        Double totalSum =
                (artisan.getAverageRating() * artisan.getRatingCount())
                - oldStars
                + dto.getStars();

        Double newAverage = totalSum / artisan.getRatingCount();

        artisan.setAverageRating(newAverage);

        ratingRepository.save(rating);
        artisanProfileRepository.save(artisan);

        return generalMapper.toDTO(rating);
    }
    
    @Transactional
    public void deleteOrderRating(String orderPublicId) {

        User user = getCurrentUser();
        verificationEmail(user);
        Order order = orderRepository.findByOrderPublicID(orderPublicId)
                .orElseThrow(() -> new OrderNotFoundException(orderPublicId));

        // Only order owner
        if (!order.getClient().equals(user)) {
            throw new InvalidOrderException("Not your order");
        }

        if (order.getStatus() != OrdersStatus.COMPLETED) {
            throw new InvalidOrderException("Order not completed yet");
        }

        Rating rating = ratingRepository.findByOrder(order)
                .orElseThrow(() -> new InvalidOrderException("Order not rated"));

        ArtisanProfile artisan = rating.getOrder().getService().getArtisan();

        Integer currentCount = artisan.getRatingCount();

        if (currentCount <= 0) {
            artisan.setAverageRating(0.0);
            artisan.setRatingCount(0);
        } else {

            Double totalSum =
                    (artisan.getAverageRating() * currentCount)
                    - rating.getStars();

            Integer newCount = currentCount - 1;

            Double newAverage = newCount == 0 ? 0 : totalSum / newCount;

            artisan.setAverageRating(newAverage);
            artisan.setRatingCount(newCount);
        }

        ratingRepository.delete(rating);
        artisanProfileRepository.save(artisan);

        
    }


}

