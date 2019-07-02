package com.uff.workflow.monitor.service.strategy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.uff.workflow.monitor.amqp.NotificationMessageSender;
import com.uff.workflow.monitor.dao.workflow.WorkflowExecutionDao;
import com.uff.workflow.monitor.dao.workflow.WorkflowExecutionMetadataDao;
import com.uff.workflow.monitor.domain.NotificationType;
import com.uff.workflow.monitor.domain.WorkflowExecution;
import com.uff.workflow.monitor.domain.WorkflowExecutionStatus;
import com.uff.workflow.monitor.domain.dto.amqp.NotificationMessageDTO;

@Component("sciCumulusServiceStrategy")
public class SciCumulusServiceStrategy implements WorkflowSystemServiceStrategy {
	
	private static final Logger log = LoggerFactory.getLogger(SciCumulusServiceStrategy.class);
	
	private final WorkflowExecutionDao workflowExecutionDao;
	private final WorkflowExecutionMetadataDao workflowExecutionMetadataDao;
	private final NotificationMessageSender notificationMessageSender;
	
	@Autowired
	public SciCumulusServiceStrategy(WorkflowExecutionDao workflowExecutionDao, 
									 WorkflowExecutionMetadataDao workflowExecutionMetadataDao,
									 NotificationMessageSender notificationMessageSender) {
		
    	this.workflowExecutionDao = workflowExecutionDao;
    	this.workflowExecutionMetadataDao = workflowExecutionMetadataDao;
    	this.notificationMessageSender = notificationMessageSender;
	}
	
	@Override
	public void updateWorkflowInformation(List<WorkflowExecution> runningExecutions) {
		log.info("Atualizando metadados de status de execução do SciCumulus");
		
		updateSuccessExecutions(runningExecutions);
		updatedFailedExecutions(runningExecutions);
	}

	private void updateSuccessExecutions(List<WorkflowExecution> runningExecutions) {
		List<String> executionsExecTagSuccess = workflowExecutionMetadataDao.findExecutionToUpdateSuccess(getExecutionsExecTags(runningExecutions));
		log.info("Total de execuções de workflow para serem atualizadas para {}: {}", WorkflowExecutionStatus.FINISHED.name(), 
				 executionsExecTagSuccess != null ? executionsExecTagSuccess.size() : 0);
		
		if (executionsExecTagSuccess.size() > 0) {
			Integer totalUpdatedRecords = workflowExecutionDao.updateExecutionsByExectag(executionsExecTagSuccess, WorkflowExecutionStatus.FINISHED);
			log.info("{} execuções atualizadas com sucesso para {}", totalUpdatedRecords, WorkflowExecutionStatus.FINISHED.name());
			
			String generalBodyMessage = "A execução do workflow foi concluída com êxito.<br/>workflow:<b>";
			String messageTitle = "Execução de workflow conclúida com sucesso";
			
			notifyEndOfExecution(runningExecutions, generalBodyMessage, messageTitle);
		}
	}

	private void updatedFailedExecutions(List<WorkflowExecution> runningExecutions) {
		List<String> executionsExecTagFailure = workflowExecutionMetadataDao.findExecutionToUpdateFailure(getExecutionsExecTags(runningExecutions));
		log.info("Total de execuções de workflow para serem atualizadas para {}: {}", WorkflowExecutionStatus.FAILURE.name(), 
				 executionsExecTagFailure != null ? executionsExecTagFailure.size() : 0);
		
		if (executionsExecTagFailure.size() > 0) {
			Integer totalUpdatedRecords = workflowExecutionDao.updateExecutionsByExectag(executionsExecTagFailure, WorkflowExecutionStatus.FINISHED);
			log.info("{} execuções atualizadas com sucesso para {}", totalUpdatedRecords, WorkflowExecutionStatus.FAILURE.name());
		
			String generalBodyMessage = "A execução do workflow foi concluída com falha.<br/>workflow:<b>";
			String messageTitle = "Execução de workflow conclúida com erro";
			
			notifyEndOfExecution(runningExecutions, generalBodyMessage, messageTitle);
		}
	}
	
	private List<String> getExecutionsExecTags(List<WorkflowExecution> runningExecutions) {
		List<String> execTags = new ArrayList<String>();
		
		for (WorkflowExecution workflowExecution : runningExecutions) {
			execTags.add(workflowExecution.getExecTag());
		}
		
		return execTags;
	}
	
	private void notifyEndOfExecution(List<WorkflowExecution> runningExecutions, String generalBodyMessage, String generalMessageTitle) {
		log.info("Criando mensagens de notificacao para o termino das execucoes de workflow");
		
		for (WorkflowExecution workflowExecution : runningExecutions) {
			String messageBody = (generalBodyMessage).concat(workflowExecution.getWorkflow().getWorkflowName())
										    	     .concat("</b><br />versão: <b>")
										    	     .concat(workflowExecution.getWorkflowVersion())
										    	     .concat("</b><br />SGWfC: <b>")
										    	     .concat(workflowExecution.getSwfms());
			
			String messageTitle = (generalBodyMessage).concat(" - ").concat(workflowExecution.getWorkflow().getWorkflowName())
										    	      .concat(" ").concat(workflowExecution.getWorkflowVersion());
			
			String messageLink = ("/workflow/" + workflowExecution.getWorkflow().getWorkflowId())
						  .concat("/execution-details/" + workflowExecution.getWorkflowExecutionId());
			
			notificationMessageSender.sendMessage(NotificationMessageDTO.builder()
																		.actionDate(new Date())
																		.messageBody(messageBody)
																		.messageTitle(messageTitle)
																		.messageLink(messageLink)
																		.userGroupId(workflowExecution.getWorkflow().getUserGroupId())
																		.notificationType(NotificationType.EXECUTION)
																		.build());
		}
	}
	
}