package com.service;

import java.util.List;

import com.DTOs.notifications.NotificationsDTO;
import com.domain.entity.User;



public interface NotificationService {

    void createNotification(String publicID,String title, String message);

    List<NotificationsDTO> getMyNotifications();
    
    void deleteNotificationsByID(Long ID);

    void markAsRead(Long notificationId);
    void deleteByUser(User user );

    void markAllAsRead();
}
