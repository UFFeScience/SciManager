package com.uff.workflow.invoker.strategy;

import java.io.IOException;

import com.uff.workflow.invoker.domain.WorkflowExecution;
import com.uff.workflow.invoker.exception.WorkflowExecutionException;

public interface WorkflowSystemInvokerStrategy {
	
	public void invokeWorkflowSystem(WorkflowExecution workflowExecution) throws IOException, WorkflowExecutionException, InterruptedException;
	
}	