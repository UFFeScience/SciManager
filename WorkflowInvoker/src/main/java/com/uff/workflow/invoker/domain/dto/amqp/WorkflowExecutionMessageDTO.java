package com.uff.workflow.invoker.domain.dto.amqp;

import java.io.Serializable;
import java.util.Calendar;

import com.uff.workflow.invoker.domain.WorkflowExecution;

public class WorkflowExecutionMessageDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long workflowExecutionId;
	private Long workflowId;
	private Long modelFileId;
	private Long userId;
	private Calendar executionDate;
	private String executionStatus;
	private String executionLog;
	private String workflowVersion;
	private String swfms;
	private String execTag;
	
	public WorkflowExecutionMessageDTO() {}
	
	public WorkflowExecutionMessageDTO(WorkflowExecutionDTOBuilder workflowExecutionDTOBuilder) {
		this.workflowExecutionId = workflowExecutionDTOBuilder.workflowExecutionId;
		this.workflowId = workflowExecutionDTOBuilder.workflowId;
		this.modelFileId = workflowExecutionDTOBuilder.modelFileId;
		this.userId = workflowExecutionDTOBuilder.userId;
		this.executionDate = workflowExecutionDTOBuilder.executionDate;
		this.executionStatus = workflowExecutionDTOBuilder.executionStatus;
		this.executionLog = workflowExecutionDTOBuilder.executionLog;
		this.workflowVersion = workflowExecutionDTOBuilder.workflowVersion;
		this.swfms = workflowExecutionDTOBuilder.swfms;
		this.execTag = workflowExecutionDTOBuilder.execTag;
	}
	
	public static WorkflowExecutionMessageDTO buildWorkflowExecutionDTOFromEntity(WorkflowExecution workflowExecution) {
		return WorkflowExecutionMessageDTO.builder()
								   .workflowExecutionId(workflowExecution.getWorkflowExecutionId())
								   .workflowId(workflowExecution.getWorkflow().getWorkflowId())
								   .modelFileId(workflowExecution.getModelFile().getModelFileId())
								   .userId(workflowExecution.getUserAgent().getUserId())
								   .execTag(workflowExecution.getExecTag())
								   .executionDate(workflowExecution.getExecutionDate())
								   .swfms(workflowExecution.getSwfms())
								   .executionStatus(workflowExecution.getExecutionStatus().name())
								   .executionLog(workflowExecution.getExecutionLog())
								   .workflowVersion(workflowExecution.getWorkflowVersion())
								   .build();
	}
	
	public Long getWorkflowExecutionId() {
		return workflowExecutionId;
	}

	public void setWorkflowExecutionId(Long workflowExecutionId) {
		this.workflowExecutionId = workflowExecutionId;
	}

	public Long getWorkflowId() {
		return workflowId;
	}
	
	public void setWorkflowId(Long workflowId) {
		this.workflowId = workflowId;
	}
	
	public Long getModelFileId() {
		return modelFileId;
	}
	
	public void setModelFileId(Long modelFileId) {
		this.modelFileId = modelFileId;
	}
	
	public Long getUserId() {
		return userId;
	}
	
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public Calendar getExecutionDate() {
		return executionDate;
	}
	
	public void setExecutionDate(Calendar executionDate) {
		this.executionDate = executionDate;
	}
	
	public String getExecutionStatus() {
		return executionStatus;
	}

	public void setExecutionStatus(String executionStatus) {
		this.executionStatus = executionStatus;
	}
	
	public String getExecutionLog() {
		return executionLog;
	}

	public void setExecutionLog(String executionLog) {
		this.executionLog = executionLog;
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
		private Long workflowId;
		private Long modelFileId;
		private Long userId;
		private Calendar executionDate;
		private String executionStatus;
		private String executionLog;
		private String workflowVersion;
		private String swfms;
		private String execTag;
		
		public WorkflowExecutionDTOBuilder workflowExecutionId(Long workflowExecutionId) {
            this.workflowExecutionId = workflowExecutionId;
            return this;
        }

        public WorkflowExecutionDTOBuilder workflowId(Long workflowId) {
            this.workflowId = workflowId;
            return this;
        }

        public WorkflowExecutionDTOBuilder modelFileId(Long modelFileId) {
            this.modelFileId = modelFileId;
            return this;
        }
        
        public WorkflowExecutionDTOBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }
        
        public WorkflowExecutionDTOBuilder executionDate(Calendar executionDate) {
            this.executionDate = executionDate;
            return this;
        }
        
        public WorkflowExecutionDTOBuilder executionStatus(String executionStatus) {
            this.executionStatus = executionStatus;
            return this;
        }
        
        public WorkflowExecutionDTOBuilder executionLog(String executionLog) {
            this.executionLog = executionLog;
            return this;
        }
        
        public WorkflowExecutionDTOBuilder workflowVersion(String workflowVersion) {
            this.workflowVersion = workflowVersion;
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

        public WorkflowExecutionMessageDTO build() {
            return new WorkflowExecutionMessageDTO(this);
        }
    }
	
}