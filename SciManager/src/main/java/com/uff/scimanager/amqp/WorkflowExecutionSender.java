package com.uff.scimanager.amqp;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.uff.scimanager.config.RabbitMqConfig;
import com.uff.scimanager.domain.dto.amqp.WorkflowExecutionMessageDTO;

@Service
public class WorkflowExecutionSender {
	
	private static final Logger log = LoggerFactory.getLogger(WorkflowExecutionSender.class);
	
	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private AmqpAdmin amqpAdmin;

	@PostConstruct
	public void setUpQueue() {
		this.amqpAdmin.declareQueue(new Queue(RabbitMqConfig.WORKFLOW_EXECUTION_QUEUE));
	}

	@Async
	public void sendMessage(WorkflowExecutionMessageDTO workflowExecutionDTO) {
		log.info("Enviando mensagem para workflowExecution de id {}", workflowExecutionDTO.getWorkflowExecutionId());
		this.rabbitTemplate.convertAndSend(RabbitMqConfig.WORKFLOW_EXECUTION_QUEUE, workflowExecutionDTO);
	}
	
}