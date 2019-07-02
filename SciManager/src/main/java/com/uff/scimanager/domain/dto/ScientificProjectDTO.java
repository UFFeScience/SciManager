package com.uff.scimanager.domain.dto;

import java.util.ArrayList;
import java.util.List;

import com.uff.scimanager.domain.ScientificProject;
import com.uff.scimanager.util.EncrypterUtils;

public class ScientificProjectDTO {
	
	private Long scientificProjectId;
	private String projectName;
	private String encodedProjectName;
	private List<ScientificExperimentDTO> scientificExperiments;
	private List<WorkflowDTO> workflows;
	private List<PhaseDTO> phases;
	private Boolean isEditablebyUser = Boolean.FALSE;
	private String responsibleGroupNames;
	
	public ScientificProjectDTO() {}

	public ScientificProjectDTO(ScientificProjectDTOBuilder scientificProjectDTOBuilder) {
		this.scientificProjectId = scientificProjectDTOBuilder.scientificProjectId;
		this.projectName = scientificProjectDTOBuilder.projectName;
		this.encodedProjectName = scientificProjectDTOBuilder.encodedProjectName;
		this.scientificExperiments = scientificProjectDTOBuilder.scientificExperiments;
		this.workflows = scientificProjectDTOBuilder.workflows;
		this.phases = scientificProjectDTOBuilder.phases;
		this.isEditablebyUser = scientificProjectDTOBuilder.isEditablebyUser;
		this.responsibleGroupNames = scientificProjectDTOBuilder.responsibleGroupNames;
		
		if (responsibleGroupNames == null || "".equals(responsibleGroupNames)) {
			this.responsibleGroupNames = ScientificExperimentDTO.buildGroupNames(workflows, responsibleGroupNames);
		}
	}
	
	public static ScientificProjectDTO buildEmptyScientificProjectDTO() {
		return new ScientificProjectDTO();
	}
	
	public static ScientificProjectDTO buildDTOByEntity(ScientificProject scientificProject) {
		if (scientificProject == null) {
			return buildEmptyScientificProjectDTO();
		}
		
		return ScientificProjectDTO.builder()
								  .scientificProjectId(scientificProject.getScientificProjectId())
								  .projectName(scientificProject.getProjectName())
								  .encodedProjectName(EncrypterUtils.encodeValueToURL(scientificProject.getProjectName())).build();
	}
	
	public static ScientificProjectDTO buildDTOWithChildrenByEntity(ScientificProject scientificProject) {
		if (scientificProject == null) {
			return buildEmptyScientificProjectDTO();
		}
		
		return ScientificProjectDTO.builder()
								   .scientificProjectId(scientificProject.getScientificProjectId())
							       .projectName(scientificProject.getProjectName())
							       .encodedProjectName(EncrypterUtils.encodeValueToURL(scientificProject.getProjectName()))
								   .phases(PhaseDTO.convertEntityListToDTOList(scientificProject.getPhases()))
								   .scientificExperiments(ScientificExperimentDTO.convertEntityListToDTOList(scientificProject.getScientificExperiments()))
								   .workflows(WorkflowDTO.convertEntityListToDTOList(scientificProject.getWorkflows())).build();
	}

	public Long getScientificProjectId() {
		return scientificProjectId;
	}

	public void setScientificProjectId(Long scientificProjectId) {
		this.scientificProjectId = scientificProjectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	public String getEncodedProjectName() {
		return encodedProjectName;
	}

	public void setEncodedProjectName(String encodedProjectName) {
		this.encodedProjectName = encodedProjectName;
	}

	public List<WorkflowDTO> getWorkflows() {
		return workflows;
	}
	
	public void setWorkflows(List<WorkflowDTO> workflows) {
		this.workflows = workflows;
		setResponsibleGroupNames(ScientificExperimentDTO.buildGroupNames(workflows, responsibleGroupNames));
	}
	
	public List<ScientificExperimentDTO> getScientificExperiments() {
		return scientificExperiments;
	}

	public void setScientificExperiments(List<ScientificExperimentDTO> scientificExperiments) {
		this.scientificExperiments = scientificExperiments;
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

	public List<PhaseDTO> getPhases() {
		return phases;
	}

	public void setPhases(List<PhaseDTO> phases) {
		this.phases = phases;
	}

	public static List<ScientificProjectDTO> convertEntityListToDTOList(List<ScientificProject> scientificProjects) {
		List<ScientificProjectDTO> scientificProjectDTOs = new ArrayList<ScientificProjectDTO>();
		
		if (scientificProjects != null) {
			for (ScientificProject scientificProject : scientificProjects){
				scientificProjectDTOs.add(ScientificProjectDTO.buildDTOByEntity(scientificProject));
			}
		}
		
		return scientificProjectDTOs;
	}
	
	public static List<ScientificProjectDTO> convertEntityListToDTOListWithChildren(List<ScientificProject> scientificProjects) {
		List<ScientificProjectDTO> scientificProjectDTOs = new ArrayList<ScientificProjectDTO>();
		
		if (scientificProjects != null) {
			for (ScientificProject scientificProject : scientificProjects) {
				scientificProjectDTOs.add(ScientificProjectDTO.buildDTOWithChildrenByEntity(scientificProject));
			}
		}
		
		return scientificProjectDTOs;
	}
	
	public List<UserDTO> getProjectMembers() {
		List<UserDTO> userDTOs = new ArrayList<UserDTO>();
		
		for (WorkflowDTO workflow : getWorkflows()) {
			if (workflow.getIsEditablebyUser()) {

				for (UserDTO groupUser : workflow.getResponsibleGroup().getGroupUsers()) {
				
					Boolean containsUser = Boolean.FALSE;
					
					for (UserDTO projectMember : userDTOs) {
						if (projectMember.getUserId().equals(groupUser.getUserId())) {
							containsUser= Boolean.TRUE;
							break;
						}
					}
					
					if (!containsUser) {
						userDTOs.add(groupUser);
					}
				}
			}
		}
		
		return userDTOs;
	}
	
	public static ScientificProjectDTOBuilder builder() {
		return new ScientificProjectDTOBuilder();
	}
	
	public static class ScientificProjectDTOBuilder {
		
		private Long scientificProjectId;
		private String projectName;
		private String encodedProjectName;
		private List<ScientificExperimentDTO> scientificExperiments;
		private List<WorkflowDTO> workflows;
		private List<PhaseDTO> phases;
		private Boolean isEditablebyUser = Boolean.FALSE;
		private String responsibleGroupNames;
		
		public ScientificProjectDTOBuilder scientificProjectId(Long scientificProjectId) {
			this.scientificProjectId = scientificProjectId;
			return this;
		}
		
		public ScientificProjectDTOBuilder projectName(String projectName) {
			this.projectName = projectName;
			return this;
		}
		
		public ScientificProjectDTOBuilder encodedProjectName(String encodedProjectName) {
			this.encodedProjectName = encodedProjectName;
			return this;
		}
		
		public ScientificProjectDTOBuilder scientificExperiments(List<ScientificExperimentDTO> scientificExperiments) {
			this.scientificExperiments = scientificExperiments;
			return this;
		}
		
		public ScientificProjectDTOBuilder workflows(List<WorkflowDTO> workflows) {
			this.workflows = workflows;
			return this;
		}
		
		public ScientificProjectDTOBuilder phases(List<PhaseDTO> phases) {
			this.phases = phases;
			return this;
		}
		
		public ScientificProjectDTOBuilder isEditablebyUser(Boolean isEditablebyUser) {
			this.isEditablebyUser = isEditablebyUser;
			return this;
		}
		
		public ScientificProjectDTOBuilder responsibleGroupNames(String responsibleGroupNames) {
			this.responsibleGroupNames = responsibleGroupNames;
			return this;
		}
		
		public ScientificProjectDTO build() {
			return new ScientificProjectDTO(this);
		}
	}

}	