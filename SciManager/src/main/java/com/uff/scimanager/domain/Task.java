package com.uff.scimanager.domain;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.uff.scimanager.domain.dto.UserDTO;
import com.uff.scimanager.domain.dto.WorkflowDTO;
import com.uff.scimanager.domain.form.TaskForm;
import com.uff.scimanager.util.CalendarDateUtils;

@Entity
@Table(name = "TAREFA")
public class Task {
	
	@Id
	@SequenceGenerator(name = "tarefa_id", sequenceName = "tarefa_id")
	@GeneratedValue(generator = "tarefa_id", strategy = GenerationType.AUTO)
	@Column(name = "ID_TAREFA", nullable = false)
	private Long taskId;
	
	@Column(name = "NM_TITULO_TAREFA")
	private String taskTitle;
	
	@Column(name = "NM_DESCRICAO")
	private String description;
	
	@Column(name = "NM_URL_REPOSITORIO")
	private String urlRepository;
	
	@Column(name = "DT_DATA_CRIACAO")
	private Calendar creationDate;
	
	@Column(name = "DT_DATA_LIMITE")
	private Calendar deadline;
	
	@Column(name = "NR_TEMPO_ESTIMADO")
	private Integer estimatedTime;
	
	@Column(name = "NM_STATUS")
	@Enumerated(EnumType.STRING)
	private TaskStatus status;
	
	@ManyToOne
	@JoinColumn(name = "ID_WORKFLOW", referencedColumnName = "ID_WORKFLOW", nullable = true)
	@Cascade(CascadeType.SAVE_UPDATE)
	private Workflow workflow;
	
	@ManyToOne
	@JoinColumn(name = "ID_PROJETO_CIENTIFICO", referencedColumnName = "ID_PROJETO_CIENTIFICO", nullable = true)
	@Cascade(CascadeType.SAVE_UPDATE)
	private ScientificProject scientificProject;
	
	@ManyToMany
    @JoinTable(name = "TAG_TAREFA", joinColumns =
    {@JoinColumn(name = "ID_TAREFA")}, inverseJoinColumns =
   	{@JoinColumn(name = "ID_TAG")})
	@Fetch(FetchMode.SUBSELECT)
	private Set<Tag> tags;
	
	@ManyToOne
	@JoinColumn(name = "ID_USUARIO", referencedColumnName = "ID_USUARIO", nullable = true)
	@Cascade(CascadeType.SAVE_UPDATE)
	private User userCreator;
	
	@ManyToMany
    @JoinTable(name = "USUARIO_TAREFA", joinColumns =
    {@JoinColumn(name = "ID_TAREFA")}, inverseJoinColumns =
   	{@JoinColumn(name = "ID_USUARIO")})
	@Fetch(FetchMode.SUBSELECT)
	private Set<User> usersInTask;
	
	@ManyToOne
	@JoinColumn(name = "ID_FASE", referencedColumnName = "ID_FASE")
	@Cascade(CascadeType.SAVE_UPDATE)
	private Phase phase;
	
	@ManyToOne
	@JoinColumn(name = "ID_GRUPO", referencedColumnName = "ID_GRUPO", nullable = true)
	@Cascade(CascadeType.SAVE_UPDATE)
	private UserGroup userGroupInTask;
	
	public Task() {}
	
	public Task(TaskBuilder taskBuilder) {
		this.taskId = taskBuilder.taskId;
		this.taskTitle = taskBuilder.taskTitle;
		this.description = taskBuilder.description;
		this.urlRepository = taskBuilder.urlRepository;
		this.creationDate = taskBuilder.creationDate;
		this.deadline = taskBuilder.deadline;
		this.estimatedTime = taskBuilder.estimatedTime;
		this.status = taskBuilder.status;
		this.workflow = taskBuilder.workflow;
		this.scientificProject = taskBuilder.scientificProject;
		this.tags = taskBuilder.tags;
		this.userCreator = taskBuilder.userCreator;
		this.usersInTask = taskBuilder.usersInTask;
		this.phase = taskBuilder.phase;
		this.userGroupInTask = taskBuilder.userGroupInTask;
	}
	
	public static Task buildEmptyTask() {
		return new Task();
	}
	
	public static Task buildTaskFromTaskForm(TaskForm taskForm) {
		if (taskForm != null) {
			return Task.builder()
					   .taskTitle(taskForm.getTaskTitle())
					   .description(taskForm.getDescription())
					   .urlRepository(taskForm.getUrlRepository())
					   .estimatedTime(taskForm.getEstimatedTime())
					   .deadline(CalendarDateUtils.createCalendarFromString(taskForm.getDeadline())).build();
		}
		
		return Task.buildEmptyTask();
	}

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

	public Calendar getDeadline() {
		return deadline;
	}

	public void setDeadline(Calendar deadline) {
		this.deadline = deadline;
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

	public Workflow getWorkflow() {
		return workflow;
	}

	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}
	
	public ScientificProject getScientificProject() {
		return scientificProject;
	}

	public void setScientificProject(ScientificProject scientificProject) {
		this.scientificProject = scientificProject;
	}

	public Set<Tag> getTags() {
		if (tags == null) {
			tags = new HashSet<Tag>();
		}
		
		return tags;
	}

	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}

	public User getUserCreator() {
		return userCreator;
	}

	public void setUserCreator(User userCreator) {
		this.userCreator = userCreator;
	}

	public UserGroup getUserGroupInTask() {
		return userGroupInTask;
	}

	public void setUserGroupInTask(UserGroup userGroup) {
		this.userGroupInTask = userGroup;
	}
	
	public Phase getPhase() {
		return phase;
	}

	public void setPhase(Phase phase) {
		this.phase = phase;
	}
	
	public Set<User> getUsersInTask() {
		if (usersInTask == null) {
			usersInTask = new HashSet<User>();
		}
		
		return usersInTask;
	}
	
	public List<UserDTO> getUsersInTaskSimpleDTO() {
		List<UserDTO> users = new ArrayList<UserDTO>();
		Integer index = 0;

		for (Iterator<User> i = getUsersInTask().iterator(); i.hasNext();) {
			User userInTask = i.next();
			users.add(index, userInTask.buildUserSimpleDTO());
			index++;
		}
		
		return users;
	}
	
	public List<String> getUserInTaskEmails() {
		List<String> userEmails = new ArrayList<String>();
		Integer index = 0;

		for (Iterator<User> i = getUsersInTask().iterator(); i.hasNext();) {
			User userInTask = i.next();
			userEmails.add(index, userInTask.getEmail());
			index++;
		}
		
		return userEmails;
	}
	
	public WorkflowDTO getWorkflowSimpleDTO() {
		if (workflow != null) {
			return workflow.buildWorkflowSimpleDTO();
		}
		
		return null;
	}

	public void setUsersInTask(Set<User> usersInTask) {
		this.usersInTask = usersInTask;
	}
	
	public Calendar getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Calendar creationDate) {
		this.creationDate = creationDate;
	}

	public Boolean removeUserInTaskByEmail(String email) {
		for (Iterator<User> i = getUsersInTask().iterator(); i.hasNext();) {
			User userInTask = i.next();
		    
		    if (userInTask.getEmail().equals(email)) {
				getUsersInTask().remove(userInTask);
				return Boolean.TRUE;
			}
		}
		
		return Boolean.FALSE;
	}
	
	public boolean removeTag(Long tagId) {
		for (Iterator<Tag> i = getTags().iterator(); i.hasNext();) {
		    Tag tag = i.next();
		    if (tag.getTagId().equals(tagId)) {
		    	getTags().remove(tag);
		        return Boolean.TRUE;
		    }
		}
		
		return Boolean.FALSE;
	}
	
	public Task cloneTask() {
		Task clonedTask = new Task();
		
		clonedTask.setCreationDate(getCreationDate());
		clonedTask.setDeadline(getDeadline());
		clonedTask.setDescription(getDescription());
		clonedTask.setEstimatedTime(getEstimatedTime());
		clonedTask.setScientificProject(getScientificProject());
		clonedTask.setPhase(getPhase());
		clonedTask.setStatus(getStatus());
		clonedTask.setTags(getTags());
		clonedTask.setTaskId(getTaskId());
		clonedTask.setTaskTitle(getTaskTitle());
		clonedTask.setUrlRepository(getUrlRepository());
		clonedTask.setUserCreator(getUserCreator());
		clonedTask.setUserGroupInTask(getUserGroupInTask());
		clonedTask.setUsersInTask(getUsersInTask());
		clonedTask.setWorkflow(getWorkflow());
		
		return clonedTask;
	}
	
	public static TaskBuilder builder() {
		return new TaskBuilder();
	}
	
	public static class TaskBuilder {
		
		private Long taskId;
		private String taskTitle;
		private String description;
		private String urlRepository;
		private Calendar creationDate;
		private Calendar deadline;
		private Integer estimatedTime;
		private TaskStatus status;
		private Workflow workflow;
		private ScientificProject scientificProject;
		private Set<Tag> tags;
		private User userCreator;
		private Set<User> usersInTask;
		private Phase phase;
		private UserGroup userGroupInTask;
		
		public TaskBuilder taskId(Long taskId) {
			this.taskId = taskId;
			return this;
		}
		
		public TaskBuilder taskTitle(String taskTitle) {
			this.taskTitle = taskTitle;
			return this;
		}
		
		public TaskBuilder description(String description) {
			this.description = description;
			return this;
		}
		
		public TaskBuilder urlRepository(String urlRepository) {
			this.urlRepository = urlRepository;
			return this;
		}
		
		public TaskBuilder creationDate(Calendar creationDate) {
			this.creationDate = creationDate;
			return this;
		}
		
		public TaskBuilder deadline(Calendar deadline) {
			this.deadline = deadline;
			return this;
		}

		public TaskBuilder estimatedTime(Integer estimatedTime) {
			this.estimatedTime = estimatedTime;
			return this;
		}
		
		public TaskBuilder status(TaskStatus status) {
			this.status = status;
			return this;
		}
		
		public TaskBuilder workflow(Workflow workflow) {
			this.workflow = workflow;
			return this;
		}
		
		public TaskBuilder scientificProject(ScientificProject scientificProject) {
			this.scientificProject = scientificProject;
			return this;
		}
		
		public TaskBuilder tags(Set<Tag> tags) {
			this.tags = tags;
			return this;
		}
		
		public TaskBuilder userCreator(User userCreator) {
			this.userCreator = userCreator;
			return this;
		}
		
		public TaskBuilder usersInTask(Set<User> usersInTask) {
			this.usersInTask = usersInTask;
			return this;
		}
		
		public TaskBuilder phase(Phase phase) {
			this.phase = phase;
			return this;
		}
		
		public TaskBuilder userGroupInTask(UserGroup userGroupInTask) {
			this.userGroupInTask = userGroupInTask;
			return this;
		}
		
		public Task build() {
			return new Task(this);
		}
	}

}