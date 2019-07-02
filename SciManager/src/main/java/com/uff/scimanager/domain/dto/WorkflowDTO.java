package com.uff.scimanager.domain.dto;

import java.util.ArrayList;
import java.util.List;

import com.uff.scimanager.domain.Workflow;
import com.uff.scimanager.util.EncrypterUtils;

public class WorkflowDTO {
	
	private Long workflowId;
	private String workflowName;
	private String encodedWorkflowName;
	private String swfms;
	private String currentVersion;
	private ScientificExperimentDTO scientificExperiment;
	private ScientificProjectDTO scientificProject;
	private ModelFileDTO currentModelFile;
	private UserGroupDTO responsibleGroup;
	private Boolean isEditablebyUser = Boolean.FALSE;
	
	public WorkflowDTO() {}
	
	public WorkflowDTO(WorkflowDTOBuilder workflowDTOBuilder) {
		this.workflowId = workflowDTOBuilder.workflowId;
		this.workflowName = workflowDTOBuilder.workflowName;
		this.encodedWorkflowName = workflowDTOBuilder.encodedWorkflowName;
		this.swfms = workflowDTOBuilder.swfms;
		this.currentVersion = workflowDTOBuilder.currentVersion;
		this.scientificExperiment = workflowDTOBuilder.scientificExperiment;
		this.scientificProject = workflowDTOBuilder.scientificProject;
		this.currentModelFile = workflowDTOBuilder.currentModelFile;
		this.responsibleGroup = workflowDTOBuilder.responsibleGroup;
		this.isEditablebyUser = workflowDTOBuilder.isEditablebyUser;
	}
	
	public static WorkflowDTO buildDTOByEntity(Workflow workflow) {
		if (workflow == null) {
			return WorkflowDTO.buildEmptyDTO();
		}
		
		return WorkflowDTO.builder()
						 .workflowId(workflow.getWorkflowId())
						 .workflowName(workflow.getWorkflowName())
						 .encodedWorkflowName(EncrypterUtils.encodeValueToURL(workflow.getWorkflowName()))
						 .swfms(workflow.getSwfms())
						 .currentVersion(workflow.getCurrentVersion())
						 .scientificExperiment(ScientificExperimentDTO.buildDTOByEntity(workflow.getScientificExperiment()))
						 .scientificProject(ScientificProjectDTO.buildDTOByEntity(workflow.getScientificProject()))
						 .responsibleGroup(UserGroupDTO.buildDTOWithChildrenByEntity(workflow.getResponsibleGroup())).build();
	}
	
	private static WorkflowDTO buildEmptyDTO() {
		return new WorkflowDTO();
	}

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
	
	public String getEncodedWorkflowName() {
		return encodedWorkflowName;
	}

	public void setEncodedWorkflowName(String encodedWorkflowName) {
		this.encodedWorkflowName = encodedWorkflowName;
	}

	public String getSwfms() {
		return swfms;
	}
	
	public void setSwfms(String swfms) {
		this.swfms = swfms;
	}

	public String getCurrentVersion() {
		return currentVersion;
	}
	
	public void setCurrentVersion(String currentVersion) {
		this.currentVersion = currentVersion;
	}
	
	public ScientificExperimentDTO getScientificExperiment() {
		return scientificExperiment;
	}

	public void setScientificExperiment(ScientificExperimentDTO scientificExperiment) {
		this.scientificExperiment = scientificExperiment;
	}

	public ScientificProjectDTO getScientificProject() {
		return scientificProject;
	}
	
	public void setScientificProject(ScientificProjectDTO scientificProject) {
		this.scientificProject = scientificProject;
	}
	
	public ModelFileDTO getCurrentModelFile() {
		return currentModelFile;
	}
	
	public void setCurrentModelFile(ModelFileDTO currentModelFile) {
		this.currentModelFile = currentModelFile;
	}
	
	public UserGroupDTO getResponsibleGroup() {
		return responsibleGroup;
	}
	
	public void setResponsibleGroup(UserGroupDTO responsibleGroup) {
		this.responsibleGroup = responsibleGroup;
	}
	
	public Boolean getIsEditablebyUser() {
		return isEditablebyUser;
	}

	public void setIsEditablebyUser(Boolean isEditablebyUser) {
		this.isEditablebyUser = isEditablebyUser;
	}
	
	public static List<WorkflowDTO> convertEntityListToDTOList(List<Workflow> workflows) {
		List<WorkflowDTO> workflowsDTO = new ArrayList<WorkflowDTO>();
		
		if (workflows != null) {
			for (Workflow workflow : workflows) {
				workflowsDTO.add(WorkflowDTO.buildDTOByEntity(workflow));
			}
		}
		
		return workflowsDTO;
	}

	public static WorkflowDTOBuilder builder() {
		return new WorkflowDTOBuilder();
	}
	
	public static class WorkflowDTOBuilder {
	
		private Long workflowId;
		private String workflowName;
		private String encodedWorkflowName;
		private String swfms;
		private String currentVersion;
		private ScientificExperimentDTO scientificExperiment;
		private ScientificProjectDTO scientificProject;
		private ModelFileDTO currentModelFile;
		private UserGroupDTO responsibleGroup;
		private Boolean isEditablebyUser = Boolean.FALSE;
		
		public WorkflowDTOBuilder workflowId(Long workflowId) {
			this.workflowId = workflowId;
			return this;
		}
		
		public WorkflowDTOBuilder workflowName(String workflowName) {
			this.workflowName = workflowName;
			return this;
		}
		
		public WorkflowDTOBuilder encodedWorkflowName(String encodedWorkflowName) {
			this.encodedWorkflowName = encodedWorkflowName;
			return this;
		}
		
		public WorkflowDTOBuilder swfms(String swfms) {
			this.swfms = swfms;
			return this;
		}
		
		public WorkflowDTOBuilder currentVersion(String currentVersion) {
			this.currentVersion = currentVersion;
			return this;
		}
		
		public WorkflowDTOBuilder scientificExperiment(ScientificExperimentDTO scientificExperiment) {
			this.scientificExperiment = scientificExperiment;
			return this;
		}
		
		public WorkflowDTOBuilder scientificProject(ScientificProjectDTO scientificProject) {
			this.scientificProject = scientificProject;
			return this;
		}
		
		public WorkflowDTOBuilder currentModelFile(ModelFileDTO currentModelFile) {
			this.currentModelFile = currentModelFile;
			return this;
		}
		
		public WorkflowDTOBuilder responsibleGroup(UserGroupDTO responsibleGroup) {
			this.responsibleGroup = responsibleGroup;
			return this;
		}
		
		public WorkflowDTOBuilder isEditablebyUser(Boolean isEditablebyUser) {
			this.isEditablebyUser = isEditablebyUser;
			return this;
		}
		
		public WorkflowDTO build() {
			return new WorkflowDTO(this);
		}
	}

}