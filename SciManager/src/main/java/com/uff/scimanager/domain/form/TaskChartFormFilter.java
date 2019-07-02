package com.uff.scimanager.domain.form;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

public class TaskChartFormFilter {
	
	private Long scientificProjectId;
	private Long phaseId;
	private Long workflowId;
	private Long userId;
	
	@NotNull(message = "Data inicial não pode ser nula.")
	@NotEmpty(message = "Data inicial não pode ser vazia.")
	private String initialDate;
 	
	@NotNull(message = "Data final não pode ser nula.")
	@NotEmpty(message = "Data final não pode ser vazia.")
 	private String finalDate;
 	

	public Long getScientificProjectId() {
		return scientificProjectId;
	}

	public void setScientificProjectId(Long scientificProjectId) {
		this.scientificProjectId = scientificProjectId;
	}

	public Long getPhaseId() {
		return phaseId;
	}

	public void setPhaseId(Long phaseId) {
		this.phaseId = phaseId;
	}

	public Long getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(Long workflowId) {
		this.workflowId = workflowId;
	}

	public String getInitialDate() {
		return initialDate;
	}

	public void setInitialDate(String initialDate) {
		this.initialDate = initialDate;
	}

	public String getFinalDate() {
		return finalDate;
	}

	public void setFinalDate(String finalDate) {
		this.finalDate = finalDate;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
}
