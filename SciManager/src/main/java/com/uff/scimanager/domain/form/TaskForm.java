package com.uff.scimanager.domain.form;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

public class TaskForm {
	
	@NotNull(message = "Projeto referente não pode ser vazio.")
	private Long scientificProjectId;
	
	@NotEmpty(message = "Título da tarefa não pode ser vazio.")
	private String taskTitle;
	
	@NotNull(message = "Usuário criador da tarefa não pode ser vazio.")
	private Long userCreatorId;
	
	@NotEmpty(message = "Fase referente não pode ser vazia.")
	private String phaseName;
	
	private String workflowName;
	
	private String deadline;
	private Integer estimatedTime;
	private List<String> tags;
	private List<String> usersInTask;
	private String userGroupInTask;
	private String description;
	private String urlRepository;
	
	public TaskForm() {}
	
	public static TaskForm buildEmptyTaskForm() {
		return new TaskForm();
	}

	public Long getScientificProjectId() {
		return scientificProjectId;
	}

	public void setScientificProjectId(Long scientificProjectId) {
		this.scientificProjectId = scientificProjectId;
	}

	public String getWorkflowName() {
		return workflowName;
	}

	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}

	public String getPhaseName() {
		return phaseName;
	}

	public void setPhaseName(String phaseName) {
		this.phaseName = phaseName;
	}

	public String getTaskTitle() {
		return taskTitle;
	}

	public void setTaskTitle(String taskTitle) {
		this.taskTitle = taskTitle;
	}

	public String getDescription() {
		if ("".equals(description)) {
			return null;
		}
		
		return description;
	}
	
	public String getUrlRepository() {
		if ("".equals(urlRepository)) {
			return null;
		}
		
		return urlRepository;
	}

	public void setUrlRepository(String urlRepository) {
		this.urlRepository = urlRepository;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDeadline() {
		return deadline;
	}

	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}

	public Integer getEstimatedTime() {
		return estimatedTime;
	}

	public void setEstimatedTime(Integer estimatedTime) {
		this.estimatedTime = estimatedTime;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public Long getUserCreatorId() {
		return userCreatorId;
	}

	public void setUserCreatorId(Long userCreatorId) {
		this.userCreatorId = userCreatorId;
	}

	public List<String> getUsersInTask() {
		return usersInTask;
	}

	public void setUsersInTask(List<String> usersInTask) {
		this.usersInTask = usersInTask;
	}

	public String getUserGroupInTask() {
		return userGroupInTask;
	}

	public void setUserGroupInTask(String userGroupInTask) {
		this.userGroupInTask = userGroupInTask;
	}
	
}	