package com.exception.notifications;

import com.domain.entity.User;
import com.exception.BusinessException;

public class NotificationsNotFoundException extends BusinessException {

	public NotificationsNotFoundException( User user ) {
		super("Notifications not found for user: " + user);
		// TODO Auto-generated constructor stub
	}

    
}
