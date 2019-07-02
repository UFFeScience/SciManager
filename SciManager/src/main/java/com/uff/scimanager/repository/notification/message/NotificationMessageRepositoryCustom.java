package com.uff.scimanager.repository.notification.message;

import java.util.List;

import com.uff.scimanager.domain.NotificationMessage;

public interface NotificationMessageRepositoryCustom {
	
	List<NotificationMessage> getNotificationMessagesOfUser(Long userId, Integer pageNumber, Integer pageSize, Boolean visualized);
	Long countNewNotificationMessagesOfUser(Long userId);
	Long countNotificationMessagesOfUser(Long userId);
	void updateNotificationsToVisualized(Long userId, String notificationMessageId);
	
}