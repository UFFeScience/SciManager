package com.uff.scimanager.domain.dto;

import java.util.ArrayList;
import java.util.List;

import com.uff.scimanager.domain.WorkflowExecution;
import com.uff.scimanager.domain.WorkflowExecutionStatus;
import com.uff.scimanager.util.CalendarDateUtils;

public class WorkflowExecutionDTO {
	
	private Long workflowExecutionId;
	private WorkflowDTO workflow;
	private ModelFileDTO modelFile;
	private UserDTO userAgent;
	private String executionDate;
	private WorkflowExecutionStatus executionStatus;
	private String executionLog;
	private String workflowVersion;
	private String swfms;
	private String execTag;
	
	public WorkflowExecutionDTO(WorkflowExecutionDTOBuilder workflowExecutionDTOBuilder) {
		this.workflowExecutionId = workflowExecutionDTOBuilder.workflowExecutionId;
		this.workflow = workflowExecutionDTOBuilder.workflow;
		this.modelFile = workflowExecutionDTOBuilder.modelFile;
		this.userAgent = workflowExecutionDTOBuilder.userAgent;
		this.executionDate = workflowExecutionDTOBuilder.executionDate;
		this.executionStatus = workflowExecutionDTOBuilder.executionStatus;
		this.executionLog = workflowExecutionDTOBuilder.executionLog;
		this.workflowVersion = workflowExecutionDTOBuilder.workflowVersion;
		this.swfms = workflowExecutionDTOBuilder.swfms;
		this.execTag = workflowExecutionDTOBuilder.execTag;
	}
	
	public static WorkflowExecutionDTO buildDTOByEntity(WorkflowExecution workflowExecution) {
		return WorkflowExecutionDTO.builder()
								   .workflowExecutionId(workflowExecution.getWorkflowExecutionId())
								   .workflow(WorkflowDTO.buildDTOByEntity(workflowExecution.getWorkflow()))
								   .modelFile(ModelFileDTO.buildDTOByEntity(workflowExecution.getModelFile()))
								   .userAgent(UserDTO.buildDTOByEntity(workflowExecution.getUserAgent()))
								   .execTag(workflowExecution.getExecTag())
								   .executionLog(workflowExecution.getExecutionLog())
								   .executionDate(CalendarDateUtils.formatDate(workflowExecution.getExecutionDate()))
								   .swfms(workflowExecution.getSwfms())
								   .executionStatus(workflowExecution.getExecutionStatus())
								   .workflowVersion(workflowExecution.getWorkflowVersion())
								   .build();
	}
	
	public static List<WorkflowExecutionDTO> convertEntityListToDTOList(List<WorkflowExecution> workflowExecutions) {
		List<WorkflowExecutionDTO> workflowExecutionsDTO = new ArrayList<WorkflowExecutionDTO>();
		
		if (workflowExecutions != null) {
			for (WorkflowExecution workflowExecution : workflowExecutions){
				workflowExecutionsDTO.add(WorkflowExecutionDTO.buildDTOByEntity(workflowExecution));
			}
		}
		
		return workflowExecutionsDTO;
	}
	
	public Long getWorkflowExecutionId() {
		return workflowExecutionId;
	}

	public void setWorkflowExecutionId(Long workflowExecutionId) {
		this.workflowExecutionId = workflowExecutionId;
	}

	public WorkflowDTO getWorkflow() {
		return workflow;
	}
	
	public void setWorkflow(WorkflowDTO workflow) {
		this.workflow = workflow;
	}
	
	public ModelFileDTO getModelFile() {
		return modelFile;
	}
	
	public void setModelFile(ModelFileDTO modelFile) {
		this.modelFile = modelFile;
	}
	
	public UserDTO getUserAgent() {
		return userAgent;
	}
	
	public void setUserAgent(UserDTO userAgent) {
		this.userAgent = userAgent;
	}
	
	public String getExecutionDate() {
		return executionDate;
	}
	
	public void setExecutionDate(String executionDate) {
		this.executionDate = executionDate;
	}
	
	public WorkflowExecutionStatus getExecutionStatus() {
		return executionStatus;
	}

	public void setExecutionStatus(WorkflowExecutionStatus executionStatus) {
		this.executionStatus = executionStatus;
	}
	
	public String getWorkflowVersion() {
		return workflowVersion;
	}

	public void setWorkflowVersion(String workflowVersion) {
		this.workflowVersion = workflowVersion;
	}

	public String getSwfms() {
		return swfms;
	}

	public void setSwfms(String swfms) {
		this.swfms = swfms;
	}

	public String getExecutionLog() {
		return executionLog;
	}

	public void setExecutionLog(String executionLog) {
		this.executionLog = executionLog;
	}

	public String getExecTag() {
		return execTag;
	}

	public void setExecTag(String execTag) {
		this.execTag = execTag;
	}
	
	public static WorkflowExecutionDTOBuilder builder() {
		return new WorkflowExecutionDTOBuilder();
	}

	public static class WorkflowExecutionDTOBuilder {
		
		private Long workflowExecutionId;
		private WorkflowDTO workflow;
		private ModelFileDTO modelFile;
		private UserDTO userAgent;
		private String executionDate;
		private WorkflowExecutionStatus executionStatus;
		private String executionLog;
		private String workflowVersion;
		private String swfms;
		private String execTag;
		
		public WorkflowExecutionDTOBuilder workflowExecutionId(Long workflowExecutionId) {
            this.workflowExecutionId = workflowExecutionId;
            return this;
        }

        public WorkflowExecutionDTOBuilder workflow(WorkflowDTO workflow) {
            this.workflow = workflow;
            return this;
        }

        public WorkflowExecutionDTOBuilder modelFile(ModelFileDTO modelFile) {
            this.modelFile = modelFile;
            return this;
        }
        
        public WorkflowExecutionDTOBuilder userAgent(UserDTO userAgent) {
            this.userAgent = userAgent;
            return this;
        }
        
        public WorkflowExecutionDTOBuilder executionDate(String executionDate) {
            this.executionDate = executionDate;
            return this;
        }
        
        public WorkflowExecutionDTOBuilder executionStatus(WorkflowExecutionStatus executionStatus) {
            this.executionStatus = executionStatus;
            return this;
        }
        
        public WorkflowExecutionDTOBuilder workflowVersion(String workflowVersion) {
            this.workflowVersion = workflowVersion;
            return this;
        }
        
        public WorkflowExecutionDTOBuilder executionLog(String executionLog) {
            this.executionLog = executionLog;
            return this;
        }
        
        public WorkflowExecutionDTOBuilder swfms(String swfms) {
            this.swfms = swfms;
            return this;
        }
        
        public WorkflowExecutionDTOBuilder execTag(String execTag) {
            this.execTag = execTag;
            return this;
        }

        public WorkflowExecutionDTO build() {
            return new WorkflowExecutionDTO(this);
        }
    }
	
}