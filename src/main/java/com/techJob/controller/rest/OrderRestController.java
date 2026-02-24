package com.techJob.controller.rest;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techJob.DTOs.PaginationAndSortDTO;
import com.techJob.DTOs.order.CreateOrderDTO;
import com.techJob.DTOs.order.OrderDTO;
import com.techJob.DTOs.rating.CreateRatingDTO;
import com.techJob.DTOs.rating.RatingDTO;
import com.techJob.domain.enums.OrdersStatus;
import com.techJob.response.ApiResponse;
import com.techJob.response.ApiResponseFactory;
import com.techJobservice.OrderService;
import com.techJobservice.RatingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderRestController {

    private final OrderService orderService;
    private final RatingService ratingService;

    public OrderRestController(OrderService orderService,RatingService ratingService) {
        this.orderService = orderService;
        this.ratingService = ratingService;
    }

    
    //====================add order to offer============================
    
    @PostMapping("/{offerId}")
    public ResponseEntity<ApiResponse<OrderDTO>> creteOrder( 
    		@PathVariable String offerId,
    		@RequestBody(required = false) CreateOrderDTO dto){
    	
    	OrderDTO order=orderService.createOrder(offerId, dto);
    	
    	
    	return ApiResponseFactory.ok(order, "orders created successfully");
    }
    
    //=====================show order artisan received ==================================== 
   
    @PreAuthorize("hasRole('ARTISAN')")
    @GetMapping("/received")
    public ResponseEntity<ApiResponse<Page<OrderDTO>>> myOrders(
            @ModelAttribute @Valid PaginationAndSortDTO dto) {

        Page<OrderDTO> orders = orderService.artisanOrders(dto);
        return ApiResponseFactory.ok(orders, "orders fetched successfully");
    }
    //====================show order client||artisan sent ================================
    
    @GetMapping("/sent")
    public ResponseEntity<ApiResponse<Page<OrderDTO>>> myOrderAsClient(
    		@ModelAttribute @Valid PaginationAndSortDTO dto){
    	Page<OrderDTO> orders = orderService.clientOrders(dto);
        return ApiResponseFactory.ok(orders, "orders fetched successfully");
    }
    //=======================show one order artisan recieved =================================
    
    @GetMapping("/received/{orderId}")
    public ResponseEntity<ApiResponse<OrderDTO>> getArtisanOrderByPublicID(
            @PathVariable String orderId) {

        OrderDTO order = orderService.getOrderByPublicID(orderId);
        return ApiResponseFactory.ok(order, "Order fetched successfully");
    }

    //=========================show one order artisan ||client sent ==================================
    
    @GetMapping("/sent/{orderId}")
    public ResponseEntity<ApiResponse<OrderDTO>> getClientOrderByPublicID(
            @PathVariable String orderId) {

        OrderDTO order = orderService.getOrderByPublicID(orderId);
        return ApiResponseFactory.ok(order, "Order fetched successfully");
    }
    
    //=========================respond of artisan to  order ==================================================================
    
    @PatchMapping("/received/{orderId}/status")
    public ResponseEntity<ApiResponse<OrderDTO>> respondToOffer(
			@PathVariable String orderId,
			@RequestParam OrdersStatus status) {

		OrderDTO order=orderService.respondToOrder(orderId, status);
		return ApiResponseFactory.ok(order, "Order Status are updated successfully");
	}
    
    //=====================rating completed order by client===========================================================
    
    @PutMapping("/sent/{orderId}/rating")
    public ResponseEntity<ApiResponse<RatingDTO>> rateOrder(
            @PathVariable String orderId,
            @Valid @RequestBody CreateRatingDTO dto) {

        RatingDTO rating = ratingService.rateOrder(orderId, dto);
        return ApiResponseFactory.ok(rating, "Order rated successfully");
    }
    
    //====================delete rating of order by client ==============================================================
    
    @DeleteMapping("/sent/{orderId}/rating")
    public ResponseEntity<ApiResponse<RatingDTO>> deleteOrderRating(
    		@PathVariable String orderId){
    	 ratingService.deleteOrderRating(orderId);
        return ApiResponseFactory.ok(null, "rating deleted successfully");
}
}
