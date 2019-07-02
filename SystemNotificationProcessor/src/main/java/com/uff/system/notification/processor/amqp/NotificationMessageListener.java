package com.uff.system.notification.processor.amqp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uff.system.notification.processor.config.RabbitMqConfig;
import com.uff.system.notification.processor.domain.dto.amqp.NotificationMessageDTO;
import com.uff.system.notification.processor.service.notification.NotificationMessageService;

@Service
public class NotificationMessageListener {
	
	private static final Logger log = LoggerFactory.getLogger(NotificationMessageListener.class);
	
	@Autowired
	private NotificationMessageService notificationMessageService;
	
	@RabbitListener(queues = RabbitMqConfig.NOTIFICATION_QUEUE)
    public void processMessage(NotificationMessageDTO notificationMessageDTO) {
		log.info("Mensagem de notificacao para usuario de id {} recebida", notificationMessageDTO.getUserSubjectId());
		notificationMessageService.processNotification(notificationMessageDTO);
	}
	
}