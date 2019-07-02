package com.uff.scimanager.domain;

public enum WorkflowExecutionStatus {
	RUNNING("Executando"), FINISHED("Finalizado"), FAILURE("Finalizado com erro");
	
	private final String executionStatusName;

	WorkflowExecutionStatus(String executionStatusName) {
		this.executionStatusName = executionStatusName;
	}
	
	public String getExecutionStatusName() {
		return executionStatusName;
	}

	public static WorkflowExecutionStatus getExecutionStatusFromString(String executionStatusName) {
		for (WorkflowExecutionStatus status : WorkflowExecutionStatus.values()) {
			if (status.name().equals(executionStatusName) || status.getExecutionStatusName().equals(executionStatusName)) {
				return status;
			}
		}
		
		return null;
	}
}
