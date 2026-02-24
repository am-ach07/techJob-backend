package com.techJob.controller.rest;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.techJob.DTOs.notifications.NotificationsDTO;
import com.techJob.response.ApiResponse;
import com.techJob.response.ApiResponseFactory;
import com.techJobservice.NotificationService;
import com.techJobservice.NotificationsServiceImp;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationsRestController {

    private final NotificationsServiceImp notificationService;

    public NotificationsRestController(NotificationsServiceImp notificationService) {
        this.notificationService = notificationService;
    }
    
    
    @Operation(summary = "Get My Notifications", description = "Retrieve all notifications for the authenticated user.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
    					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Notifications retrieved successfully"),
    					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    					
    })

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationsDTO>>> getMyNotifications() {
        return ApiResponseFactory.ok(
                notificationService.getMyNotifications(),
                "Notifications retrieved successfully"
        );
    }

    
    @Operation(summary = "Mark Notification as Read", description = "Mark a specific notification as read by its ID.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
    					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Notification marked as read"),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Notification not found"),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })


    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ApiResponseFactory.ok(null, "Notification marked as read");
    }
    
    
    
    @Operation(summary = "Mark All Notifications as Read", description = "Mark all notifications for the authenticated user as read.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "All notifications marked as read"),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
	})

    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
        notificationService.markAllAsRead();
        return ApiResponseFactory.ok(null, "All notifications marked as read");
    }
    
    @Operation(summary = "Delete Notification", description = "Delete a specific notification by its ID.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Notification deleted successfully"),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Notification not found"),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
							})
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(@PathVariable Long id) {
		notificationService.deleteNotificationsByID(id);
		return ApiResponseFactory.ok(null, "Notification deleted successfully");
	}
    
}
