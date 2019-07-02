package com.uff.scimanager.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.uff.scimanager.domain.dto.TaskDTO;

@Document(collection = "taskHistory")
public class TaskHistory {
	
	@Id
	private String taskHistoryId;
	private Long taskId;
	private Long workflowId;
	private Long scientificProjectId;
	private Long phaseId;
	private Date creationDate;
	private Date actionDate;
	private TaskStatus currentStatus;
	private TaskHistoryType historyType;
	private List<Field> changeSet;
	private Long userAgentId;
	
	public TaskHistory() {}

	public TaskHistory(TaskHistoryBuilder taskHistoryBuilder) {
		this.taskHistoryId = taskHistoryBuilder.taskHistoryId;
		this.taskId = taskHistoryBuilder.taskId;
		this.workflowId = taskHistoryBuilder.workflowId;
		this.scientificProjectId = taskHistoryBuilder.scientificProjectId;
		this.currentStatus = taskHistoryBuilder.currentStatus;
		this.phaseId = taskHistoryBuilder.phaseId;
		this.creationDate = taskHistoryBuilder.creationDate;
		this.actionDate = taskHistoryBuilder.actionDate;
		this.historyType = taskHistoryBuilder.historyType;
		this.changeSet = taskHistoryBuilder.changeSet;
		this.userAgentId = taskHistoryBuilder.userAgentId;
	}
	
	public String getTaskHistoryId() {
		return taskHistoryId;
	}

	public void setTaskHistoryId(String taskHistoryId) {
		this.taskHistoryId = taskHistoryId;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public Long getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(Long workflowId) {
		this.workflowId = workflowId;
	}

	public Long getScientificProjectId() {
		return scientificProjectId;
	}

	public void setScientificProjectId(Long scientificProjectId) {
		this.scientificProjectId = scientificProjectId;
	}

	public Long getPhaseId() {
		return phaseId;
	}

	public void setPhaseId(Long phaseId) {
		this.phaseId = phaseId;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getActionDate() {
		return actionDate;
	}

	public void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
	}

	public TaskStatus getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(TaskStatus currentStatus) {
		this.currentStatus = currentStatus;
	}

	public TaskHistoryType getHistoryType() {
		return historyType;
	}

	public void setHistoryType(TaskHistoryType historyType) {
		this.historyType = historyType;
	}

	public List<Field> getChangeSet() {
		if (changeSet == null) {
			changeSet = new ArrayList<Field>();
		}
		
		return changeSet;
	}

	public void setChangeSet(List<Field> changeSet) {
		this.changeSet = changeSet;
	}

	public Long getUserAgentId() {
		return userAgentId;
	}

	public void setUserAgentId(Long userAgentId) {
		this.userAgentId = userAgentId;
	}
	
	public Field getStatusField() {
		for (Field changeSetField : getChangeSet()) {
			if ("status".equals(changeSetField.getName())) {
				return changeSetField;
			}
		}
		
		return null;
	}

	public static TaskHistoryBuilder builder() {
		return new TaskHistoryBuilder();
	}
	
	public static class TaskHistoryBuilder {
		
		private String taskHistoryId;
		private Long taskId;
		private Long workflowId;
		private Long scientificProjectId;
		private Long phaseId;
		private Date creationDate;
		private Date actionDate;
		private TaskStatus currentStatus;
		private TaskHistoryType historyType;
		private List<Field> changeSet;
		private Long userAgentId;
		
		public TaskHistoryBuilder buildTaskDTOFields(TaskDTO task) {
			if (task != null) {
				this.taskId = task.getTaskId();
				
				if (task.getWorkflow() != null) {
					this.workflowId = task.getWorkflow().getWorkflowId();
				}
				
				if (task.getScientificProject() != null) {
					this.scientificProjectId = task.getScientificProject().getScientificProjectId();
				}
				
				if (task.getPhase() != null) {
					this.phaseId = task.getPhase().getPhaseId();
				}
				
				if (task.getStatus() != null) {
					this.currentStatus = task.getStatus();
				}
			}
			
			return this;
		}
		
		public TaskHistoryBuilder taskHistoryId(String taskHistoryId) {
			this.taskHistoryId = taskHistoryId;
			return this;
		}
		
		public TaskHistoryBuilder taskId(Long taskId) {
			this.taskId = taskId;
			return this;
		}
		
		public TaskHistoryBuilder workflowId(Long workflowId) {
			this.workflowId = workflowId;
			return this;
		}
		
		public TaskHistoryBuilder workflowName(String workflowName) {
			return this;
		}
		
		public TaskHistoryBuilder scientificProjectId(Long scientificProjectId) {
			this.scientificProjectId = scientificProjectId;
			return this;
		}
		
		public TaskHistoryBuilder projectName(String projectName) {
			return this;
		}
		
		public TaskHistoryBuilder phaseId(Long phaseId) {
			this.phaseId = phaseId;
			return this;
		}
		
		public TaskHistoryBuilder creationDate(Date creationDate) {
			this.creationDate = creationDate;
			return this;
		}
		
		public TaskHistoryBuilder actionDate(Date actionDate) {
			this.actionDate = actionDate;
			return this;
		}
		
		public TaskHistoryBuilder currentStatus(TaskStatus currentStatus) {
			this.currentStatus = currentStatus;
			return this;
		}
		
		public TaskHistoryBuilder historyType(TaskHistoryType historyType) {
			this.historyType = historyType;
			return this;
		}
		
		public TaskHistoryBuilder changeSet(List<Field> changeSet) {
			this.changeSet = changeSet;
			return this;
		}
		
		public TaskHistoryBuilder userAgentId(Long userAgentId) {
			this.userAgentId = userAgentId;
			return this;
		}
		
		public TaskHistory build() {
			return new TaskHistory(this);
		}
	}
	
}