package com.uff.workflow.monitor.amqp;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.uff.workflow.monitor.config.RabbitMqConfig;
import com.uff.workflow.monitor.domain.dto.amqp.NotificationMessageDTO;

@Service
public class NotificationMessageSender {
	
	private static final Logger log = LoggerFactory.getLogger(NotificationMessageSender.class);
	
	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private AmqpAdmin amqpAdmin;

	@PostConstruct
	public void setUpQueue() {
		this.amqpAdmin.declareQueue(new Queue(RabbitMqConfig.NOTIFICATION_QUEUE));
	}

	@Async
	public void sendMessage(NotificationMessageDTO notificationMessageDTO) {
		log.info("Enviando mensagem de notificacao para usuario de id", notificationMessageDTO.getUserSubjectId());
		this.rabbitTemplate.convertAndSend(RabbitMqConfig.NOTIFICATION_QUEUE, notificationMessageDTO);
	}
	
}