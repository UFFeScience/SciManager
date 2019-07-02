package com.uff.workflow.invoker.amqp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uff.workflow.invoker.config.RabbitMqConfig;
import com.uff.workflow.invoker.domain.dto.amqp.WorkflowExecutionMessageDTO;
import com.uff.workflow.invoker.service.workflow.WorkflowService;

@Service
public class WorkflowExecutionListener {
	
	private static final Logger log = LoggerFactory.getLogger(WorkflowExecutionListener.class);
	
	@Autowired
	private WorkflowService workflowService;
	
	@RabbitListener(queues = RabbitMqConfig.WORKFLOW_EXECUTION_QUEUE)
    public void processMessage(WorkflowExecutionMessageDTO workflowExecutionMessageDTO) {
		log.info("Mensagem recebida do workflowExecution de id {}", workflowExecutionMessageDTO.getWorkflowExecutionId());
		workflowService.invokeWorkflowSystem(workflowExecutionMessageDTO);
	}
	
}