package com.uff.scimanager.domain.form;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

public class TaskFormUpdate {
	
	@NotNull(message = "O id da tarefa não pode ser nulo.")
	private Long taskId;
	
	@NotNull(message = "O título da tarefa não pode ser nulo.")
	@NotEmpty(message = "O título da tarefa não pode ser vazio.")
	private String taskTitle;
	
	@NotNull(message = "A fase não pode ser nula.")
	@NotEmpty(message = "A fase não pode ser vazio.")
	private String phaseName;
	
	private String workflowName;
	
	private String description;
	private String urlRepository;
	private Integer estimatedTime;
	private String deadline;
	private String userGroupInTask;
	private List<String> usersInTask;
	private List<String> tags;
	
	public Long getTaskId() {
		return taskId;
	}
	
	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}
	
	public String getTaskTitle() {
		return taskTitle;
	}
	
	public void setTaskTitle(String taskTitle) {
		this.taskTitle = taskTitle;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getUrlRepository() {
		return urlRepository;
	}
	
	public void setUrlRepository(String urlRepository) {
		this.urlRepository = urlRepository;
	}
	
	public Integer getEstimatedTime() {
		return estimatedTime;
	}
	
	public void setEstimatedTime(Integer estimatedTime) {
		this.estimatedTime = estimatedTime;
	}
	
	public String getDeadline() {
		return deadline;
	}
	
	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}
	
	public String getPhaseName() {
		return phaseName;
	}
	
	public void setPhaseName(String phaseName) {
		this.phaseName = phaseName;
	}
	
	public String getWorkflowName() {
		return workflowName;
	}
	
	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}
	
	public String getUserGroupInTask() {
		return userGroupInTask;
	}
	
	public void setUserGroupInTask(String userGroupInTask) {
		this.userGroupInTask = userGroupInTask;
	}
	
	public List<String> getUsersInTask() {
		return usersInTask;
	}
	
	public void setUsersInTask(List<String> usersInTask) {
		this.usersInTask = usersInTask;
	}
	
	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	
}
