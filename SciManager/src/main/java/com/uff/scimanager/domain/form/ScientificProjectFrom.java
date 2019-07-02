package com.uff.scimanager.domain.form;

import org.hibernate.validator.constraints.NotEmpty;

import com.uff.scimanager.domain.ScientificProject;

public class ScientificProjectFrom {
	
	@NotEmpty(message = "O Nome do projeto científico não pode ser vazio.")
	private String projectName;
	
	public ScientificProjectFrom() {}
	
	public ScientificProjectFrom(ScientificProject scientificProject) {
		if (scientificProject != null) {
			this.projectName = scientificProject.getProjectName();
		}
	}
	
	public static ScientificProjectFrom buildEmptyScientificProjectForm() {
		return new ScientificProjectFrom();
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

}