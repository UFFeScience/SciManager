package com.uff.scimanager.service.notification.message;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uff.scimanager.domain.User;
import com.uff.scimanager.domain.dto.NotificationMessageDTO;
import com.uff.scimanager.repository.notification.message.NotificationMessageRepository;
import com.uff.scimanager.service.user.UserService;

@Service
public class NotificationMessageService {

	private static final Logger log = LoggerFactory.getLogger(NotificationMessageService.class);

	private final NotificationMessageRepository notificationMessageRepository;
	private final UserService userService;

    @Autowired
	public NotificationMessageService(NotificationMessageRepository notificationMessageRepository, UserService userService) {
		this.notificationMessageRepository = notificationMessageRepository;
		this.userService = userService;
	}
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<NotificationMessageDTO> getAllUserNotifications(Long userId, Integer pageNumber, Integer pageSize, Boolean notVisualized) {
		log.info("Buscando lista de notificacoes do userId {}", userId);
		User user = userService.getUserEntityById(userId);
		
		if (user == null) {
			log.info("Usuario de id {} nao encontrado", userId);
			return null;
		}
		
		return NotificationMessageDTO.convertEntityListToDTOList(
				notificationMessageRepository.getNotificationMessagesOfUser(userId, pageNumber, pageSize, notVisualized != null ? !notVisualized : null));
	}
    
    public Integer countAllUserNotifications(Long userId) {
		log.info("Buscando contagem de notificacoes do userId {}", userId);
		User user = userService.getUserEntityById(userId);
		
		if (user == null) {
			log.info("Usuario de id {} nao encontrado", userId);
			return null;
		}
		
		return notificationMessageRepository.countNotificationMessagesOfUser(userId).intValue();
	}
    
    public Integer countAllNewUserNotifications(Long userId) {
		log.info("Buscando contagem de notificacoes nao visualizadas do userId {}", userId);
		User user = userService.getUserEntityById(userId);
		
		if (user == null) {
			log.info("Usuario de id {} nao encontrado", userId);
			return null;
		}
		
		return notificationMessageRepository.countNewNotificationMessagesOfUser(userId).intValue();
	}

	public void updateToVisualized(Long userId, String notificationMessageId) {
		log.info("Atualizando notificacoes para visualizada do userId {}", userId);
		notificationMessageRepository.updateNotificationsToVisualized(userId,  notificationMessageId);
	}

}