package com.uff.workflow.monitor.service.workflow;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.uff.workflow.monitor.dao.workflow.WorkflowExecutionDao;
import com.uff.workflow.monitor.domain.WorkflowExecution;
import com.uff.workflow.monitor.domain.WorkflowExecutionStatus;
import com.uff.workflow.monitor.domain.WorkflowSystem;
import com.uff.workflow.monitor.service.strategy.WorkflowSystemServiceStrategy;

@Service
public class WorkflowService {

	private static final Logger log = LoggerFactory.getLogger(WorkflowService.class);
	
	private final BeanFactory beanFactory;
	private final WorkflowExecutionDao workflowExecutionDao;
	
    @Autowired
	public WorkflowService(BeanFactory beanFactory, WorkflowExecutionDao workflowExecutionDao) {
    	this.beanFactory = beanFactory;
    	this.workflowExecutionDao = workflowExecutionDao;
	}
    
    @Async
	public void updateWorkflowEecutions() {
    	log.info("Iniciando processo de atualização de status de execução a partir da base do swfms");
    	List<WorkflowExecution> runningExecutions = workflowExecutionDao.findAllRunningExecutions();

    	log.info("Total de {} workflows com status {} encontrados", runningExecutions != null ? runningExecutions.size() : 0, 
    																WorkflowExecutionStatus.RUNNING.name());
    	
    	if (runningExecutions.size() > 0) {
    		for (WorkflowSystem swfms : WorkflowSystem.values()) {
    			String strategyBeanName = String.format("%sServiceStrategy", StringUtils.uncapitalize(swfms.getSwfms()));
    			beanFactory.getBean(strategyBeanName, WorkflowSystemServiceStrategy.class).updateWorkflowInformation(runningExecutions);
			}
    	}
    	
    	log.info("Finalizado processo de atualização de status de atividades da execução do workflow");
	}

}