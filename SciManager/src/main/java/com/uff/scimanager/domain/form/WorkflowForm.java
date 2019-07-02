package com.uff.scimanager.domain.form;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.uff.scimanager.domain.Workflow;
import com.uff.scimanager.domain.WorkflowSystem;

public class WorkflowForm {
	
	@NotEmpty(message = "Nome do workflow não pode ser vazio.")
	private String workflowName;
	
	@NotNull(message = "O projeto científico associado ao workflow não pode ser nulo.")
	private Long scientificProjectId;
	
	@NotNull(message = "O experimento científico associado ao workflow não pode ser nulo.")
	private Long scientificExperimentId;
	
	@NotEmpty(message = "O Nome do grupo responsável pelo workflow não pode ser vazio.")
	private String responsibleGroupName;

	private String swfms;
	
	public WorkflowForm() {}
	
	public WorkflowForm(Workflow worlflow) {
		if (worlflow != null) {
			this.workflowName = worlflow.getWorkflowName();
			this.swfms = worlflow.getSwfms();
			this.scientificProjectId = worlflow.getScientificProject().getScientificProjectId();
			this.scientificExperimentId = worlflow.getScientificExperiment().getScientificExperimentId();
			this.responsibleGroupName = worlflow.getResponsibleGroup().getGroupName();
		}
	}
	
	public static WorkflowForm buildWorkflowFormWithDefaultSwfms() {
		WorkflowForm workflowForm = new WorkflowForm();
		workflowForm.setSwfms(WorkflowSystem.SCICUMULUS.getSwfms());
		return workflowForm;
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