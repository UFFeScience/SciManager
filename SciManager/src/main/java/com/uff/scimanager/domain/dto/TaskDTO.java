package com.uff.scimanager.domain.dto;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import com.uff.scimanager.domain.Tag;
import com.uff.scimanager.domain.Task;
import com.uff.scimanager.domain.TaskStatus;
import com.uff.scimanager.util.CalendarDateUtils;

public class TaskDTO {
	
	private Long taskId;
	private ScientificProjectDTO scientificProject;
	private WorkflowDTO workflow;
	private PhaseDTO phase;
	private String taskTitle;
	private String description;
	private String urlRepository;
	private String creationDate;
	private String deadline;
	private Integer estimatedTime;
	private TaskStatus status;
	private List<TagDTO> tags;
	private UserDTO userCreator;
	private List<UserDTO> usersInTask;
	private UserGroupDTO userGroupInTask;
	private Boolean late = Boolean.FALSE;
	
	public TaskDTO() {}

	public TaskDTO(TaskDTOBuilder taskDTOBuilder) {
		this.taskId = taskDTOBuilder.taskId;
		this.scientificProject = taskDTOBuilder.scientificProject;
		this.workflow = taskDTOBuilder.workflow;
		this.phase = taskDTOBuilder.phase;
		this.taskTitle = taskDTOBuilder.taskTitle;
		this.description = taskDTOBuilder.description;
		this.urlRepository = taskDTOBuilder.urlRepository;
		this.creationDate = taskDTOBuilder.creationDate;
		this.deadline = taskDTOBuilder.deadline;
		this.estimatedTime = taskDTOBuilder.estimatedTime;
		this.status = taskDTOBuilder.status;
		this.tags = taskDTOBuilder.tags;
		this.userCreator = taskDTOBuilder.userCreator;
		this.usersInTask = taskDTOBuilder.usersInTask;
		this.userGroupInTask = taskDTOBuilder.userGroupInTask;
	}
	
	public static TaskDTO buildEmptyTaskDTO() {
		return new TaskDTO();
	}
	
	public static TaskDTO buildDTOByEntity(Task task) {
		if (task == null) {
			return buildEmptyTaskDTO();
		}
		
		TaskDTO taskDTO = TaskDTO.builder()
						      	 .taskId(task.getTaskId())
						      	 .scientificProject(ScientificProjectDTO.buildDTOByEntity(task.getScientificProject()))
						      	 .workflow(WorkflowDTO.buildDTOByEntity(task.getWorkflow()))
						      	 .phase(PhaseDTO.buildDTOByEntity(task.getPhase()))
						      	 .taskTitle(task.getTaskTitle())
						      	 .description(task.getDescription())
						      	 .urlRepository(task.getUrlRepository())
						      	 .creationDate(CalendarDateUtils.formatDateWithoutHours(task.getCreationDate()))
						      	 .deadline(CalendarDateUtils.formatDateWithoutHours(task.getDeadline()))
						      	 .estimatedTime(task.getEstimatedTime())
						      	 .status(task.getStatus())
						      	 .tags(TagDTO.convertEntityListToDTOList(new ArrayList<Tag>(task.getTags())))
						      	 .userCreator(UserDTO.buildDTOByEntity(task.getUserCreator()))
						      	 .usersInTask(UserDTO.convertEntityListToDTOList(task.getUsersInTask()))
						      	 .userGroupInTask(UserGroupDTO.buildDTOWithChildrenByEntity(task.getUserGroupInTask())).build();
		
		taskDTO.calculateDeadline();
		
		return taskDTO;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public ScientificProjectDTO getScientificProject() {
		return scientificProject;
	}

	public void setScientificProject(ScientificProjectDTO scientificProject) {
		this.scientificProject = scientificProject;
	}

	public WorkflowDTO getWorkflow() {
		return workflow;
	}

	public void setWorkflow(WorkflowDTO workflow) {
		this.workflow = workflow;
	}

	public PhaseDTO getPhase() {
		return phase;
	}

	public void setPhase(PhaseDTO phase) {
		this.phase = phase;
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

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public String getDeadline() {
		return deadline;
	}

	public void setDeadline(String deadline) {
		this.deadline = deadline;
		calculateDeadline();
	}

	public void calculateDeadline() {
		if (deadline == null || "".equals(deadline)) {
			setLate(Boolean.FALSE);
			return;
		}
		
		Calendar currentDay = Calendar.getInstance(TimeZone.getTimeZone(CalendarDateUtils.DATE_LOCALE));
		Calendar deadlineDay = CalendarDateUtils.createCalendarFromString(deadline);

		setLate(currentDay.compareTo(deadlineDay) > 0 ? Boolean.TRUE : Boolean.FALSE);
	}

	public Integer getEstimatedTime() {
		return estimatedTime;
	}

	public void setEstimatedTime(Integer estimatedTime) {
		this.estimatedTime = estimatedTime;
	}

	public TaskStatus getStatus() {
		return status;
	}

	public void setStatus(TaskStatus status) {
		this.status = status;
	}

	public List<TagDTO> getTags() {
		if (tags == null) {
			tags = new ArrayList<TagDTO>();
		}
		
		return tags;
	}

	public void setTags(List<TagDTO> tags) {
		this.tags = tags;
	}

	public UserDTO getUserCreator() {
		return userCreator;
	}

	public void setUserCreator(UserDTO userCreator) {
		this.userCreator = userCreator;
	}

	public List<UserDTO> getUsersInTask() {
		if (usersInTask == null) {
			usersInTask = new ArrayList<UserDTO>();
		}
		
		return usersInTask;
	}

	public void setUsersInTask(List<UserDTO> usersInTask) {
		this.usersInTask = usersInTask;
	}

	public UserGroupDTO getUserGroupInTask() {
		return userGroupInTask;
	}

	public void setUserGroupInTask(UserGroupDTO userGroupInTask) {
		this.userGroupInTask = userGroupInTask;
	}
	
	public Boolean getLate() {
		return late;
	}

	public void setLate(Boolean late) {
		this.late = late;
	}

	public static List<TaskDTO> convertEntityListToDTOList(List<Task> tasks) {
		List<TaskDTO> taskDTOs = new ArrayList<TaskDTO>();
		
		if (tasks != null) {
			for (Task task : tasks){
				taskDTOs.add(TaskDTO.buildDTOByEntity(task));
			}
		}
		
		return taskDTOs;
	}
	
	public static TaskDTOBuilder builder() {
		return new TaskDTOBuilder();
	}
	
	public static class TaskDTOBuilder {
		
		private Long taskId;
		private ScientificProjectDTO scientificProject;
		private WorkflowDTO workflow;
		private PhaseDTO phase;
		private String taskTitle;
		private String description;
		private String urlRepository;
		private String creationDate;
		private String deadline;
		private Integer estimatedTime;
		private TaskStatus status;
		private List<TagDTO> tags;
		private UserDTO userCreator;
		private List<UserDTO> usersInTask;
		private UserGroupDTO userGroupInTask;
		
		public TaskDTOBuilder taskId(Long taskId) {
			this.taskId = taskId;
			return this;
		}
		
		public TaskDTOBuilder scientificProject(ScientificProjectDTO scientificProject) {
			this.scientificProject = scientificProject;
			return this;
		}
		
		public TaskDTOBuilder workflow(WorkflowDTO workflow) {
			this.workflow = workflow;
			return this;
		}
		
		public TaskDTOBuilder phase(PhaseDTO phase) {
			this.phase = phase;
			return this;
		}
		
		public TaskDTOBuilder taskTitle(String taskTitle) {
			this.taskTitle = taskTitle;
			return this;
		}
		
		public TaskDTOBuilder urlRepository(String urlRepository) {
			this.urlRepository = urlRepository;
			return this;
		}
		
		public TaskDTOBuilder description(String description) {
			this.description = description;
			return this;
		}
		
		public TaskDTOBuilder creationDate(String creationDate) {
			this.creationDate = creationDate;
			return this;
		}
		
		public TaskDTOBuilder deadline(String deadline) {
			this.deadline = deadline;
			return this;
		}
		
		public TaskDTOBuilder estimatedTime(Integer estimatedTime) {
			this.estimatedTime = estimatedTime;
			return this;
		}
		
		public TaskDTOBuilder status(TaskStatus status) {
			this.status = status;
			return this;
		}
		
		public TaskDTOBuilder tags(List<TagDTO> tags) {
			this.tags = tags;
			return this;
		}
		
		public TaskDTOBuilder userCreator(UserDTO userCreator) {
			this.userCreator = userCreator;
			return this;
		}
		
		public TaskDTOBuilder usersInTask(List<UserDTO> usersInTask) {
			this.usersInTask = usersInTask;
			return this;
		}
		
		public TaskDTOBuilder userGroupInTask(UserGroupDTO userGroupInTask) {
			this.userGroupInTask = userGroupInTask;
			return this;
		}
		
		public TaskDTO build() {
			return new TaskDTO(this);
		}
	}

}	