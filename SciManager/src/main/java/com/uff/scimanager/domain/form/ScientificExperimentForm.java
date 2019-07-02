package com.uff.scimanager.domain.form;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.uff.scimanager.domain.ScientificExperiment;

public class ScientificExperimentForm {
	
	@NotEmpty(message = "Nome do experimento científico não pode ser vazio.")
	private String experimentName;
	
	@NotNull(message = "O id do projeto científico associado ao experimento científico não pode ser nulo.")
	private Long scientificProjectId;
	
	public ScientificExperimentForm() {}
	
	public ScientificExperimentForm(ScientificExperiment scientificExperiment) {
		if (scientificExperiment != null) {
			this.experimentName = scientificExperiment.getExperimentName();
			this.scientificProjectId = scientificExperiment.getScientificProject().getScientificProjectId();
		}
	}
	
	public static ScientificExperimentForm buildEmptyScientificExperimentForm() {
		return new ScientificExperimentForm();
	}
	
	public String getExperimentName() {
		return experimentName;
	}

	public void setExperimentName(String experimentName) {
		this.experimentName = experimentName;
	}

	public Long getScientificProjectId() {
		return scientificProjectId;
	}

	public void setScientificProjectId(Long scientificProjectId) {
		this.scientificProjectId = scientificProjectId;
	}
}