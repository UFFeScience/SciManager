package com.uff.workflow.invoker.exception;

public class WorkflowExecutionException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	private String executionLog;

	public WorkflowExecutionException(String message) {
        super(message);
    }
	
	public WorkflowExecutionException(String message, String executionLog) {
        super(message);
        this.executionLog = executionLog;
    }

    public WorkflowExecutionException(String message, Throwable throwable) {
        super(message, throwable);
    }
    
    public String getExecutionLog() {
    	return executionLog;
    }
	
}