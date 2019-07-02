package com.uff.scimanager.domain.form;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

public class WorkflowFormUpdate {
	
	@NotNull(message = "O id do workflow não pode ser nulo.")
	private Long workflowId;
	
	@NotNull(message = "O nome do workflow não pode ser nulo.")
	@NotEmpty(message = "O nome do workflow não pode ser vazio.")
	private String workflowName;
	
	@NotNull(message = "O SGWfC do workflow não pode ser nulo.")
	@NotEmpty(message = "O SGWfC do workflow não pode ser vazio.")
	private String swfms;
	
	@NotNull(message = "O projeto científico do workflow não pode ser nulo.")
	private Long scientificProjectId;
	
	@NotNull(message = "O experimento científico do workflow não pode ser nulo.")
	private Long scientificExperimentId;
	
	@NotNull(message = "O nome do grupo responsável pelo workflow não pode ser nulo.")
	@NotEmpty(message = "O nome do grupo responsável pelo workflow não pode ser vazio.")
	private String responsibleGroupName;
	
	public Long getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(Long workflowId) {
		this.workflowId = workflowId;
	}

	public String getWorkflowName() {
		return workflowName;
	}

	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}

	public String getSwfms() {
		return swfms;
	}

	public void setSwfms(String swfms) {
		this.swfms = swfms;
	}
	
	public Long getScientificProjectId() {
		return scientificProjectId;
	}

	public void setScientificProjectId(Long scientificProjectId) {
		this.scientificProjectId = scientificProjectId;
	}

	public Long getScientificExperimentId() {
		return scientificExperimentId;
	}

	public void setScientificExperimentId(Long scientificExperimentId) {
		this.scientificExperimentId = scientificExperimentId;
	}

	public String getResponsibleGroupName() {
		return responsibleGroupName;
	}

	public void setResponsibleGroupName(String responsibleGroupName) {
		this.responsibleGroupName = responsibleGroupName;
	}
	
}
