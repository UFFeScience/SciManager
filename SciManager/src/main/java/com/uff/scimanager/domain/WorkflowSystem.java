package com.uff.scimanager.domain;

public enum WorkflowSystem {
	
	SCICUMULUS("SciCumulus");
	
	private final String swfms;
	
	WorkflowSystem(String swfms) {
		this.swfms = swfms;
	}
	
	public String getSwfms() {
		return swfms;
	}
	
}