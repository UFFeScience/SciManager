package com.uff.scimanager.domain.form;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.uff.scimanager.domain.Phase;

public class PhaseForm {
	
	@NotEmpty(message = "O Nome da fase não pode ser vazia.")
	private String phaseName;
	
	@NotNull(message = "Informe se a fase habilita execução de SGWfC.")
	private Boolean allowExecution;
	
	@NotNull(message = "Projeto referente não pode ser vazio.")
	private Long projectId;
	
	public PhaseForm() {}
	
	public PhaseForm(Phase phase) {
		if (phase != null) {
			this.phaseName = phase.getPhaseName();
			this.allowExecution = phase.getAllowExecution();
			this.projectId = phase.getScientificProject().getScientificProjectId();
		}
	}
	
	public static PhaseForm buildEmptyPhaseForm() {
		return new PhaseForm();
	}

	public String getPhaseName() {
		return phaseName;
	}

	public void setPhaseName(String phaseName) {
		this.phaseName = phaseName;
	}
	
	public Boolean getAllowExecution() {
		return allowExecution;
	}

	public void setAllowExecution(Boolean allowExecution) {
		this.allowExecution = allowExecution;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

}