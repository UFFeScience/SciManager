package com.uff.workflow.monitor.service.strategy;

import java.util.List;

import com.uff.workflow.monitor.domain.WorkflowExecution;

public interface WorkflowSystemServiceStrategy {
	
	public void updateWorkflowInformation(List<WorkflowExecution> runningExecutions);
	
}	