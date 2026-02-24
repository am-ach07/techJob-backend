package com.techJob.exception.notifications;

import com.techJob.domain.entity.User;
import com.techJob.exception.BusinessException;

public class NotificationsNotFoundException extends BusinessException {

	public NotificationsNotFoundException( User user ) {
		super("Notifications not found for user: " + user);
		// TODO Auto-generated constructor stub
	}

    
}
