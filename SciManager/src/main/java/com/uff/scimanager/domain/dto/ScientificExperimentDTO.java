package com.uff.scimanager.domain.dto;

import java.util.ArrayList;
import java.util.List;

import com.uff.scimanager.domain.ScientificExperiment;
import com.uff.scimanager.util.EncrypterUtils;

public class ScientificExperimentDTO {
	
	private Long scientificExperimentId;
	private String experimentName;
	private String encodedExperimentName;
	private ScientificProjectDTO scientificProject;
	private List<WorkflowDTO> workflows;
	private Boolean isEditablebyUser = Boolean.FALSE;
	private String responsibleGroupNames;
	
	public ScientificExperimentDTO() {}

	public ScientificExperimentDTO(ScientificExperimentDTOBuilder scientificExperimentDTOBuilder) {
		this.scientificExperimentId = scientificExperimentDTOBuilder.scientificExperimentId;
		this.experimentName = scientificExperimentDTOBuilder.experimentName;
		this.encodedExperimentName = scientificExperimentDTOBuilder.encodedExperimentName;
		this.scientificProject = scientificExperimentDTOBuilder.scientificProject;
		this.workflows = scientificExperimentDTOBuilder.workflows;
		this.isEditablebyUser = scientificExperimentDTOBuilder.isEditablebyUser;
		this.responsibleGroupNames = scientificExperimentDTOBuilder.responsibleGroupNames;
		
		if (responsibleGroupNames == null || "".equals(responsibleGroupNames)) {
			this.responsibleGroupNames = ScientificExperimentDTO.buildGroupNames(workflows, responsibleGroupNames);
		}
	}
	
	public static ScientificExperimentDTO buildEmptyScientificExperimentDTO() {
		return new ScientificExperimentDTO();
	}
	
	public static ScientificExperimentDTO buildDTOByEntity(ScientificExperiment scientificExperiment) {
		if (scientificExperiment == null) {
			return buildEmptyScientificExperimentDTO();
		}
		
		return ScientificExperimentDTO.builder()
								  	 .scientificExperimentId(scientificExperiment.getScientificExperimentId())
								  	 .experimentName(scientificExperiment.getExperimentName())
								  	 .encodedExperimentName(EncrypterUtils.encodeValueToURL(scientificExperiment.getExperimentName()))
								  	 .scientificProject(ScientificProjectDTO.buildDTOByEntity(scientificExperiment.getScientificProject())).build();
	}
	
	public static ScientificExperimentDTO buildDTOWithChildrenByEntity(ScientificExperiment scientificExperiment) {
		if (scientificExperiment == null) {
			return buildEmptyScientificExperimentDTO();
		}
		
		return ScientificExperimentDTO.builder()
									 .scientificExperimentId(scientificExperiment.getScientificExperimentId())
									 .experimentName(scientificExperiment.getExperimentName())
									 .encodedExperimentName(EncrypterUtils.encodeValueToURL(scientificExperiment.getExperimentName()))
									 .scientificProject(ScientificProjectDTO.buildDTOByEntity(scientificExperiment.getScientificProject()))
									 .workflows(WorkflowDTO.convertEntityListToDTOList(scientificExperiment.getWorkflows())).build();
	}

	public Long getScientificExperimentId() {
		return scientificExperimentId;
	}

	public void setScientificExperimentId(Long scientificExperimentId) {
		this.scientificExperimentId = scientificExperimentId;
	}

	public String getExperimentName() {
		return experimentName;
	}

	public void setExperimentName(String projectName) {
		this.experimentName = projectName;
	}
	
	public String getEncodedExperimentName() {
		return encodedExperimentName;
	}

	public void setEncodedExperimentName(String encodedExperimentName) {
		this.encodedExperimentName = encodedExperimentName;
	}
	
	public ScientificProjectDTO getScientificProject() {
		return scientificProject;
	}

	public void setScientificProject(ScientificProjectDTO scientificProject) {
		this.scientificProject = scientificProject;
	}

	public List<WorkflowDTO> getWorkflows() {
		return workflows;
	}
	
	public void setWorkflows(List<WorkflowDTO> workflows) {
		this.workflows = workflows;
		setResponsibleGroupNames(ScientificExperimentDTO.buildGroupNames(workflows, responsibleGroupNames));
	}
	
	public static String buildGroupNames(List<WorkflowDTO> entityWorkflows, String groupNames) {
		groupNames = "--";
		
		if (entityWorkflows != null && !entityWorkflows.isEmpty()) {
			groupNames = "";
			
			for (int i = 0; i < entityWorkflows.size(); i++) {
				WorkflowDTO workflowDTO = entityWorkflows.get(i);
				
				if (i < (entityWorkflows.size() - 1)) {
					groupNames += workflowDTO.getResponsibleGroup().getGroupName() + ", ";
				}
				else {
					groupNames += workflowDTO.getResponsibleGroup().getGroupName();
				}
			}
		}
		
		return groupNames;
	}

	public Boolean getIsEditablebyUser() {
		return isEditablebyUser;
	}

	public void setIsEditablebyUser(Boolean isEditablebyUser) {
		this.isEditablebyUser = isEditablebyUser;
	}
	
	public String getResponsibleGroupNames() {
		return responsibleGroupNames;
	}

	public void setResponsibleGroupNames(String responsibleGroupNames) {
		this.responsibleGroupNames = responsibleGroupNames;
	}
	
	public static List<ScientificExperimentDTO> convertEntityListToDTOList(List<ScientificExperiment> scientificExperiments) {
		List<ScientificExperimentDTO> scientificExperimentDTOs = new ArrayList<ScientificExperimentDTO>();
		
		if (scientificExperiments != null) {
			for (ScientificExperiment scientificExperiment : scientificExperiments){
				scientificExperimentDTOs.add(ScientificExperimentDTO.buildDTOByEntity(scientificExperiment));
			}
		}
		
		return scientificExperimentDTOs;
	}
	
	public static List<ScientificExperimentDTO> convertEntityListToDTOListWithChildren(List<ScientificExperiment> scientificExperiments) {
		List<ScientificExperimentDTO> scientificExperimentDTOs = new ArrayList<ScientificExperimentDTO>();
		
		if (scientificExperiments != null) {
			for (ScientificExperiment scientificExperiment : scientificExperiments){
				scientificExperimentDTOs.add(ScientificExperimentDTO.buildDTOWithChildrenByEntity(scientificExperiment));
			}
		}
		
		return scientificExperimentDTOs;
	}
	
	public static ScientificExperimentDTOBuilder builder() {
		return new ScientificExperimentDTOBuilder();
	}
	
	public static class ScientificExperimentDTOBuilder {
		
		private Long scientificExperimentId;
		private String experimentName;
		private String encodedExperimentName;
		private ScientificProjectDTO scientificProject;
		private List<WorkflowDTO> workflows;
		private Boolean isEditablebyUser = Boolean.FALSE;
		private String responsibleGroupNames;
		
		public ScientificExperimentDTOBuilder scientificExperimentId(Long scientificExperimentId) {
			this.scientificExperimentId = scientificExperimentId;
			return this;
		}
		
		public ScientificExperimentDTOBuilder experimentName(String projectName) {
			this.experimentName = projectName;
			return this;
		}
		
		public ScientificExperimentDTOBuilder encodedExperimentName(String encodedExperimentName) {
			this.encodedExperimentName = encodedExperimentName;
			return this;
		}
		
		public ScientificExperimentDTOBuilder workflows(List<WorkflowDTO> workflows) {
			this.workflows = workflows;
			return this;
		}
		
		public ScientificExperimentDTOBuilder scientificProject(ScientificProjectDTO scientificProject) {
			this.scientificProject = scientificProject;
			return this;
		}
		
		public ScientificExperimentDTOBuilder isEditablebyUser(Boolean isEditablebyUser) {
			this.isEditablebyUser = isEditablebyUser;
			return this;
		}
		
		public ScientificExperimentDTOBuilder responsibleGroupNames(String responsibleGroupNames) {
			this.responsibleGroupNames = responsibleGroupNames;
			return this;
		}
		
		public ScientificExperimentDTO build() {
			return new ScientificExperimentDTO(this);
		}
	}

}	