package com.techJobservice;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techJob.DTOs.notifications.NotificationsDTO;
import com.techJob.domain.entity.Notifications;
import com.techJob.domain.entity.User;
import com.techJob.exception.notifications.NotificationsNotFoundException;
import com.techJob.exception.user.UserNotFoundException;
import com.techJob.mapper.GeneralMapper;
import com.techJob.repository.NotificationsRepository;
import com.techJob.repository.UserRepository;

@Service
@Transactional
public class NotificationsServiceImp implements NotificationService {

    private final NotificationsRepository notificationRepository;
    private final UserRepository userRepository;
    private final GeneralMapper notificationMapper;

    public NotificationsServiceImp(NotificationsRepository notificationRepository,
                                   UserRepository userRepository,
                                   GeneralMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.notificationMapper = notificationMapper;
    }

    // ========== helpers ==========
    private User getCurrentUser() {
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    String email = auth.getName();

	    return  userRepository
	            .findByUsernameOrEmail(email, email)
	            .orElseThrow(() -> new UserNotFoundException(email));

         
    }

    // ========== create ==========
    @Override
    public void createNotification(String publicID ,String title, String message) {
        User user = userRepository.findByPublicID(publicID)
                .orElseThrow(() -> new UserNotFoundException(publicID));

        Notifications notification = new Notifications();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setRead(false);

        notificationRepository.save(notification);
    }

    // ========== read ==========
    @Override
    @Transactional(readOnly = true)
    public List<NotificationsDTO> getMyNotifications() {
        User user = getCurrentUser();

        return notificationRepository
                .findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(notificationMapper::toDTO)
                .toList();
    }

 

    // ========== update ==========
    @Override
    public void markAsRead(Long notificationId) {
        User user = getCurrentUser();

        Notifications notification = notificationRepository
                .findByIdAndUser(notificationId, user)
                .orElseThrow(() -> new NotificationsNotFoundException(user));

        if (!notification.isRead()) {
            notification.setRead(true);
        }
    }

    @Override
    public void markAllAsRead() {
        User user = getCurrentUser();
        notificationRepository.markAllAsReadByUser(user);
    }
    
    // ========== delete ==========
    @Override
    public void deleteNotificationsByID(Long notificationId) {
    			User user = getCurrentUser();

		Notifications notification = notificationRepository
				.findByIdAndUser(notificationId, user)
				.orElseThrow(() -> new NotificationsNotFoundException(user));

		notificationRepository.delete(notification);
		System.out.println("notification delted "+notificationId);
    }
    
    @Transactional
    public void deleteByUser(User user) {
        notificationRepository.deleteByUser(user);
    }

}
