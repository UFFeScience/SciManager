package com.uff.workflow.invoker.service.workflow;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Throwables;
import com.uff.workflow.invoker.dao.workflow.WorkflowExecutionDao;
import com.uff.workflow.invoker.domain.Role;
import com.uff.workflow.invoker.domain.User;
import com.uff.workflow.invoker.domain.Workflow;
import com.uff.workflow.invoker.domain.WorkflowExecution;
import com.uff.workflow.invoker.domain.WorkflowExecutionStatus;
import com.uff.workflow.invoker.domain.dto.amqp.WorkflowExecutionMessageDTO;
import com.uff.workflow.invoker.exception.WorkflowExecutionException;
import com.uff.workflow.invoker.strategy.WorkflowSystemInvokerStrategy;

@Service
public class WorkflowService {

	private static final Logger log = LoggerFactory.getLogger(WorkflowService.class);
	
	private final BeanFactory beanFactory;
	private final WorkflowExecutionDao workflowExecutionDao;
	
    @Autowired
	public WorkflowService(BeanFactory beanFactory,  WorkflowExecutionDao workflowExecutionDao) {
		this.workflowExecutionDao = workflowExecutionDao;
		this.beanFactory = beanFactory;
	}
    
	public void invokeWorkflowSystem(WorkflowExecutionMessageDTO workflowExecutionMessageDTO) {
		log.info("Iniciando execução de workflow de id {} e SGWfC {}", workflowExecutionMessageDTO.getWorkflowId(), workflowExecutionMessageDTO.getSwfms());
		
		WorkflowExecution workflowExecution = workflowExecutionDao.findById(workflowExecutionMessageDTO.getWorkflowExecutionId());
		
		if (workflowExecution == null) {
			log.error("Erro ao realizar processo de execucao, workflowExecution de id {} nao encontrado.", workflowExecutionMessageDTO.getWorkflowExecutionId());
			return;
		}
		
		if (!workflowExecution.getExecutionStatus().equals(WorkflowExecutionStatus.RUNNING)) {
			log.error("Erro ao realizar processo de execucao, workflowExecution de id {} já está finalizado com status {}.", 
					  workflowExecution.getWorkflowExecutionId(), workflowExecution.getExecutionStatus().name());
			return;
		}
		
		try {
			if (!canAccessWorkflow(workflowExecution.getUserAgent(), workflowExecution.getWorkflow())) {
				log.error("Usuário de id {} não tem permissão para manipular workflow de id {}", 
						   workflowExecution.getUserAgent().getUserId(), workflowExecution.getWorkflow().getWorkflowId());
				
				workflowExecution.setExecutionStatus(WorkflowExecutionStatus.FAILURE);
				workflowExecutionDao.saveOrUpdateWorkflowExecution(workflowExecution);
				return;
			}
			
			String strategyBeanName = String.format("%sInvokerStrategy", StringUtils.uncapitalize(workflowExecution.getSwfms()));
			beanFactory.getBean(strategyBeanName, WorkflowSystemInvokerStrategy.class).invokeWorkflowSystem(workflowExecution);
		}
		catch (WorkflowExecutionException e) {
			log.error("Erro inesperado durante execução do SGWfS {}\n{}", workflowExecutionMessageDTO.getSwfms(), Throwables.getStackTraceAsString(e));
			
			workflowExecution.setExecutionStatus(WorkflowExecutionStatus.FAILURE);
			workflowExecution.setExecutionLog(e.getExecutionLog());
			workflowExecutionDao.saveOrUpdateWorkflowExecution(workflowExecution);
			return;
		}
		catch (Exception e) {
			log.error("Erro inesperado ao executar o SGWfS {}\n{}", workflowExecutionMessageDTO.getSwfms(), Throwables.getStackTraceAsString(e));
			
			workflowExecution.setExecutionStatus(WorkflowExecutionStatus.FAILURE);
			workflowExecutionDao.saveOrUpdateWorkflowExecution(workflowExecution);
			return;
		}
		
		log.info("Processo de execução de workflow iniciado com sucesso para o Workflow de id {}, execTag {}, versão {}, swfms {} e modelFile de Id {}", 
				 workflowExecution.getWorkflow().getWorkflowId(), workflowExecution.getExecTag(), workflowExecution.getWorkflowVersion(),
				 workflowExecution.getSwfms(), workflowExecution.getModelFile().getModelFileId());
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Boolean canAccessWorkflow(User user, Workflow workflow) {
		log.info("Checando se usuário de id {} tem acesso ao workflow de id {}", user.getUserId(), workflow.getWorkflowId());
		
		if (user.getUserRole().equals(Role.ADMIN)) {
			return Boolean.TRUE;
		}
		
		checkPermissionsForWorkflow(user.getUserId(), workflow);
		
		return workflow.getIsEditablebyUser();
    }
	
	public void checkPermissionsForWorkflow(Long userId, Workflow workflow) {
		if (workflow == null) {
			return;
		}
		
		for (User user : workflow.getResponsibleGroup().getGroupUsers()) {

			if (user.getUserId().equals(userId)) {
				workflow.setIsEditablebyUser(Boolean.TRUE);
			}
		}
	}

}