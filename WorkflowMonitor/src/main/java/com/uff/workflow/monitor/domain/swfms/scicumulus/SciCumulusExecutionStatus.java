package com.uff.workflow.monitor.domain.swfms.scicumulus;

public enum SciCumulusExecutionStatus {
	RUNNING("RUNNING"), READY("READY"), FINISHED("FINISHED"), BLOCKED("BLOCKED"), FAILURE("FINISHED_WITH_ERROR");
	
	private final String executionStatusName;

	SciCumulusExecutionStatus(String executionStatusName) {
		this.executionStatusName = executionStatusName;
	}
	
	public String getExecutionStatusName() {
		return executionStatusName;
	}
	
}
