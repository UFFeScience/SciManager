package com.uff.scimanager.domain.swfms.scicumulus;

public enum SciCumulusExecutionStatus {
	
	RUNNING("RUNNING", "Executando"), 
	READY("READY", "Pronto"), 
	FINISHED("FINISHED", "Finalizado"),
	BLOCKED("BLOCKED", "Bloqueado"), 
	FAILURE("FINISHED_WITH_ERROR", "Finalizado com erro");
	
	private final String executionStatusName;
	private final String executionStatusDetail;

	SciCumulusExecutionStatus(String executionStatusName, String executionStatusDetail) {
		this.executionStatusName = executionStatusName;
		this.executionStatusDetail = executionStatusDetail;
	}
	
	public String getExecutionStatusName() {
		return executionStatusName;
	}
	
	public String getExecutionStatusDetail() {
		return executionStatusDetail;
	}
	
	public static SciCumulusExecutionStatus getSciCumulusExecutionStatusFromString(String executionStatusName) {
		for (SciCumulusExecutionStatus status : SciCumulusExecutionStatus.values()) {
			if (status.getExecutionStatusName().equals(executionStatusName)) {
				return status;
			}
		}
		
		return null;
	}
	
}
