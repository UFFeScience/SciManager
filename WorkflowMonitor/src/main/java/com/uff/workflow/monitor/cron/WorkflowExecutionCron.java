package com.uff.workflow.monitor.cron;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.uff.workflow.monitor.service.workflow.WorkflowService;

@Component
public class WorkflowExecutionCron {
	
	@Autowired
	private WorkflowService workflowService;
	
	@Scheduled(cron = "${cron.periodicity}", zone = "${cron.zone}")
	public void processWorkflowExecutionStatus() {
		workflowService.updateWorkflowEecutions();
	}
	
}