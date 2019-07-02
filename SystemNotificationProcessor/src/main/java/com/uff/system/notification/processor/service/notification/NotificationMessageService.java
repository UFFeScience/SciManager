package com.uff.system.notification.processor.service.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Throwables;
import com.uff.system.notification.processor.domain.NotificationMessage;
import com.uff.system.notification.processor.domain.User;
import com.uff.system.notification.processor.domain.UserGroup;
import com.uff.system.notification.processor.domain.dto.amqp.NotificationMessageDTO;
import com.uff.system.notification.processor.exception.NotificationProcessingException;
import com.uff.system.notification.processor.http.client.SystemNotifierClient;
import com.uff.system.notification.processor.repository.NotificationMessageRepository;
import com.uff.system.notification.processor.service.user.UserService;
import com.uff.system.notification.processor.service.user.group.UserGroupService;

@Service
public class NotificationMessageService {

	private static final Logger log = LoggerFactory.getLogger(NotificationMessageService.class);
	
	private final UserService userService;
	private final UserGroupService userGroupService;
	private final MailService mailService;
	private final NotificationMessageRepository notificationMessageRepository;
	private final SystemNotifierClient systemNotifierClient;
	
	@Value("${system.url}")
	protected String systemUrl;
	
	@Autowired
	public NotificationMessageService(UserService userService, MailService mailService, UserGroupService userGroupService,
									  NotificationMessageRepository notificationMessageRepository, 
									  SystemNotifierClient systemNotifierClient) {
		
		this.userService = userService;
		this.userGroupService = userGroupService;
		this.mailService = mailService;
		this.notificationMessageRepository = notificationMessageRepository;
		this.systemNotifierClient = systemNotifierClient;
	}
	
	@Async
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void processNotification(NotificationMessageDTO notificationMessageDTO) {
		try {
			log.info("Iniciando processamento da mensagem de notificacao");
			
			if (notificationMessageDTO.getUserSubjectId() != null) {
				handleNotifySingleUser(notificationMessageDTO);
			}
			else if (notificationMessageDTO.getUserGroupId() != null) {
				handleNotifyGroupUsers(notificationMessageDTO);
			}
		}
		catch(Exception e) {
			log.info("Erro ao processar mensagem de notificacao\n{}", Throwables.getStackTraceAsString(e));
		}
	}

	private void handleNotifyGroupUsers(NotificationMessageDTO notificationMessageDTO) throws NotificationProcessingException {
		log.info("Processando mensagem destinada aos usuarios do grupo de id {}", notificationMessageDTO.getUserGroupId());
		UserGroup userGroupSubject = userGroupService.getUserGroupById(notificationMessageDTO.getUserGroupId());
		
		if (userGroupSubject == null) {
			log.error("Erro ao processar dados para notificacao, dados nulos: userGroupSubject={}", userGroupSubject);
			throw new NotificationProcessingException("Erro ao processar notificacao, dados nulos");
		}
		
		for (User user : userGroupSubject.getGroupUsers()) {
			NotificationMessage notificationMessage = NotificationMessage.buildByDTO(notificationMessageDTO);
			
			notificationMessage.setUserSubjectId(user.getUserId());
			notificationMessageRepository.save(notificationMessage);
			
			notifyMessage(user, notificationMessage, notificationMessageDTO.getNotificationTypeName());
		}
	}

	private void handleNotifySingleUser(NotificationMessageDTO notificationMessageDTO) throws NotificationProcessingException {
		log.info("Processando mensagem destinada ao usuario de id {}", notificationMessageDTO.getUserSubjectId());
		User userSubject = userService.getUserById(notificationMessageDTO.getUserSubjectId());
		
		if (userSubject == null) {
			log.error("Erro ao processar dados para notificacao, dados nulos: userSubject={}", userSubject);
			throw new NotificationProcessingException("Erro ao processar notificacao, dados nulos");
		}
		
		NotificationMessage notificationMessage = NotificationMessage.buildByDTO(notificationMessageDTO);
		notificationMessageRepository.save(notificationMessage);

		notifyMessage(userSubject, notificationMessage, notificationMessageDTO.getNotificationTypeName());
	}

	private void notifyMessage(User userSubject, NotificationMessage notificationMessage, String notificationType) {
		if (notificationMessage.getUserAgentId() == null || (notificationMessage.getUserAgentId() != null &&
			!userSubject.getUserId().equals(notificationMessage.getUserAgentId()))) {
			
			mailService.sendEmail(userSubject.getEmail(), 
					notificationMessage.getMessageBody().concat("<br /><br /><a target=\"_blank\" href=\"")
					.concat(systemUrl)
					.concat(notificationMessage.getMessageLink())
					.concat("\">Ver detalhes</a>"),
					("[SciManager] ").concat(notificationMessage.getMessageTitle()));
			
			systemNotifierClient.notifyNewMessage(userSubject.getUserId(), notificationType);
		}
	}

}