package com.uff.scimanager.service.task;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uff.scimanager.amqp.NotificationMessageSender;
import com.uff.scimanager.dao.task.TaskDao;
import com.uff.scimanager.domain.NotificationType;
import com.uff.scimanager.domain.ScientificProject;
import com.uff.scimanager.domain.Tag;
import com.uff.scimanager.domain.Task;
import com.uff.scimanager.domain.TaskStatus;
import com.uff.scimanager.domain.User;
import com.uff.scimanager.domain.UserGroup;
import com.uff.scimanager.domain.dto.TaskDTO;
import com.uff.scimanager.domain.dto.TasksChartDTO;
import com.uff.scimanager.domain.dto.amqp.NotificationMessageDTO;
import com.uff.scimanager.domain.form.TaskChartFormFilter;
import com.uff.scimanager.domain.form.TaskForm;
import com.uff.scimanager.domain.form.TaskFormFilter;
import com.uff.scimanager.domain.form.TaskFormUpdate;
import com.uff.scimanager.domain.security.CurrentUser;
import com.uff.scimanager.service.history.TaskHistoryService;
import com.uff.scimanager.service.scientific.project.ScientificProjectService;
import com.uff.scimanager.service.tag.TagService;
import com.uff.scimanager.service.user.UserService;
import com.uff.scimanager.util.CalendarDateUtils;

@Service
public class TaskService {

	private static final Logger log = LoggerFactory.getLogger(TaskService.class);
	
	private final TaskDao taskDao;
	private final TaskHistoryService taskHistoryService;
	private final NotificationMessageSender notificationMessageSender;
	
	@Autowired
	private TaskChartService taskChartService;
	
	@Autowired
	private ScientificProjectService scientificProjectService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private TagService tagService;
	
	@Autowired
	public TaskService(TaskDao taskDao, TaskHistoryService taskHistoryService, NotificationMessageSender notificationMessageSender) {
		this.taskDao = taskDao;
		this.taskHistoryService = taskHistoryService;
		this.notificationMessageSender = notificationMessageSender;
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Task getTaskById(Long taskId) {
		log.info("Buscando task de id {}", taskId);
		
		Task task = taskDao.findById(taskId);
		task.getWorkflow().getResponsibleGroup().getGroupUsers().size();
		
		return task;
	}
    
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public List<TaskDTO> getAllTasks(Long scientificProjectId, TaskFormFilter taskFormFilter) {
		
		log.info("Buscando lista de tarefas pelo filtro: scientificProjectId = {} ,pageNumber = {}, queryString = {}, taskStatus = {}, phaseName = {}, workflowName = {}", 
				 scientificProjectId, taskFormFilter.getPageNumber(), taskFormFilter.getQueryString(), taskFormFilter.getTaskStatus(), taskFormFilter.getWorkflowName());
		
		List<Task> tasks = taskDao.findAllPaginated(scientificProjectId, taskFormFilter);
		
		if (tasks != null && !tasks.isEmpty()) {
			
			for (Task task : tasks) {
				task.getUsersInTask().size();
			}
		}
		
		return TaskDTO.convertEntityListToDTOList(tasks);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public List<TaskDTO> getAllProjectDashboardTasks(Long scientificProjectId, TaskFormFilter taskFormFilter) {
		log.info("Buscando lista de tarefas pelo filtro: scientificProjectId = {}, pageNumber = {}", scientificProjectId, taskFormFilter.getPageNumber());
		
		List<Task> tasks = taskDao.findAllRelevantPaginated(scientificProjectId, taskFormFilter);
		
		if (tasks != null && !tasks.isEmpty()) {
			
			for (Task task : tasks) {
				task.getUsersInTask().size();
			}
		}
		
		return TaskDTO.convertEntityListToDTOList(tasks);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public TaskDTO editTask(TaskFormUpdate taskFormUpdate, Long userId) {
		Task task = taskDao.findById(taskFormUpdate.getTaskId());
		
		if (task == null) {
			log.info("Não foi possível encontrar tarefa com id {}", taskFormUpdate.getTaskId());
			return null;
		}
		
		Task oldTask = task.cloneTask();

		task.setTaskTitle(taskFormUpdate.getTaskTitle());
		task.setDescription((taskFormUpdate.getDescription() != null && !"".equals(taskFormUpdate.getDescription())) ? taskFormUpdate.getDescription() : null);
		task.setUrlRepository((taskFormUpdate.getUrlRepository() != null && !"".equals(taskFormUpdate.getUrlRepository())) ? taskFormUpdate.getUrlRepository() : null);
		task.setEstimatedTime(taskFormUpdate.getEstimatedTime());
		task.setDeadline(CalendarDateUtils.createCalendarFromString(taskFormUpdate.getDeadline()));
		task.setPhase(task.getScientificProject().getPhaseByPhaseName(taskFormUpdate.getPhaseName()));
		
		if (taskFormUpdate.getWorkflowName() != null && !"".equals(taskFormUpdate.getWorkflowName())) {
			task.setWorkflow(task.getScientificProject().getWorkflowByWorkflowName(taskFormUpdate.getWorkflowName()));
		}
		
		setResponsibleForTask(taskFormUpdate.getUserGroupInTask(), taskFormUpdate.getUsersInTask(), task);
		setTaskTags(taskFormUpdate.getTags(), task);
		
		User userAgent = userService.getUserEntityById(userId);
		task = taskDao.saveOrUpdateTask(task);
		
		taskHistoryService.saveTaskHistoryForUpdate(oldTask, task, userAgent);
		
		String messageBody = ("O usuário <b>").concat(userAgent.getUsername()).concat("</b> editou uma tarefa associada a você: <b>").concat(task.getTaskTitle()).concat("</b>");
		String messageTitle = ("Uma tarefa associada a você foi editada - ").concat(task.getTaskTitle().substring(0, task.getTaskTitle().length() > 10 ? 10 : task.getTaskTitle().length())
																			.concat(task.getTaskTitle().length() > 10 ? "..." : ""));
		String messageLink = ("/task-board/" + task.getScientificProject().getScientificProjectId()).concat("?myOwn=true");
		
		handleNotification(task, userAgent.getUserId(), messageBody, messageTitle, messageLink);
		
		return TaskDTO.buildDTOByEntity(taskDao.saveOrUpdateTask(task));
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public TaskDTO createTask(TaskForm taskForm) {
		Task task = Task.buildTaskFromTaskForm(taskForm);

		task.setStatus(TaskStatus.TODO);
		task.setCreationDate(Calendar.getInstance(TimeZone.getTimeZone(CalendarDateUtils.DATE_LOCALE)));
		
		User userCreator = userService.getUserEntityById(taskForm.getUserCreatorId());
		
		if (userCreator == null) {
			return null;
		}
		task.setUserCreator(userCreator);
		
		ScientificProject scientificProject = scientificProjectService.getScientificProjectEntityAndChildrenById(taskForm.getScientificProjectId());
		if (scientificProject == null) {
			return null;
		}
		
		task.setScientificProject(scientificProject);
		task.setPhase(scientificProject.getPhaseByPhaseName(taskForm.getPhaseName()));
		
		if (taskForm.getWorkflowName() != null && !"".equals(taskForm.getWorkflowName())) { 
			task.setWorkflow(scientificProject.getWorkflowByWorkflowName(taskForm.getWorkflowName()));
		}
		
		setResponsibleForTask(taskForm.getUserGroupInTask(), taskForm.getUsersInTask(), task);
		setTaskTags(taskForm.getTags(), task);
		
		Task createdTask = taskDao.saveOrUpdateTask(task);
		taskHistoryService.saveTaskHistoryCreate(createdTask, userCreator);
		
		String messageBody = ("O usuário <b>").concat(userCreator.getUsername()).concat("</b> associou você a tarefa <b>").concat(task.getTaskTitle()).concat("</b>");
		String messageTitle = ("Você foi associado a uma tarefa - ").concat(task.getTaskTitle().substring(0, task.getTaskTitle().length() > 10 ? 10 : task.getTaskTitle().length())
																	.concat(task.getTaskTitle().length() > 10 ? "..." : ""));
		String messageLink = ("/task-board/" + taskForm.getScientificProjectId()).concat("?myOwn=true");
		
		handleNotification(createdTask, userCreator.getUserId(), messageBody, messageTitle, messageLink);
		
		return TaskDTO.buildDTOByEntity(createdTask);
	}

	private void setTaskTags(List<String> tags, Task task) {
		task.setTags(null);
		
		if (tags != null && !tags.isEmpty()) {
			createTagsForTask(tags, task);
		}
	}

	private void setResponsibleForTask(String userGroupInTask, List<String> usersInTask, Task task) {
		if (userGroupInTask != null && !"".equals(userGroupInTask)) {
			task.setUserGroupInTask(task.getWorkflow().getResponsibleGroup());
			task.setUsersInTask(null);
		}
		else if (usersInTask != null && !usersInTask.isEmpty()) {
			setResponsibleUsers(usersInTask, task);
		}
		else {
			task.setUsersInTask(null);
			task.setUserGroupInTask(null);
		}
	}

	private void setResponsibleUsers(List<String> usersInTask, Task task) {
		if (task.getWorkflow() != null) {
			task.setUsersInTask(task.getWorkflow().getResponsibleGroup().getUsersByEmails(usersInTask));
		}
		else {
			List<User> users = userService.getUsersEntitiesByEmails(usersInTask);
			
			if (users != null) {
				task.setUsersInTask(new HashSet<User>(users));
			}
			else {
				task.setUsersInTask(null);
			}
		}
		task.setUserGroupInTask(null);
	}

	private void createTagsForTask(List<String> tags, Task task) {
		for (String tagName : tags) {
			Tag tagFromDB = tagService.getTagEntityByTagName(tagName);
			
			if (tagFromDB != null) {
				task.getTags().add(tagFromDB);
			}
			else {
				Tag newTag = tagService.createFromNameTag(tagName);
				
				if (newTag != null) {
					task.getTags().add(newTag);
				}
				else {
					log.error("Erro ao criar tag com nome {} para a tarefa", tagName);
				}
			}
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public TaskDTO editTaskStatus(Long taskId, String taskStatus, Long userId) {
		TaskStatus newStatus = TaskStatus.getStatusFromString(taskStatus);
		
		if (newStatus == null) {
			log.info("Status {} está inválido ou null", taskStatus);
			return null;
		}
		
		Task task = taskDao.findById(taskId);
		
		if (task == null) {
			log.info("Não foi possível encontrar tarefa com id {}", taskId);
			return null;
		}
		
		User userAgent = userService.getUserEntityById(userId);
		Task oldTask = task.cloneTask();
		
		task.setStatus(newStatus);
		task = taskDao.saveOrUpdateTask(task);

		taskHistoryService.saveTaskHistoryForUpdate(oldTask, task, userAgent);
		
		String messageBody = ("O usuário <b>").concat(userAgent.getUsername()).concat("</b> alterou o status para <i>")
											  .concat(taskStatus).concat("</i> da tarefa: <b>").concat(task.getTaskTitle())
											  .concat("</b>");
		String messageTitle = ("Uma tarefa associada a você teve seu status alterado para ").concat(taskStatus).concat(" - ")
											  .concat(task.getTaskTitle().substring(0, task.getTaskTitle().length() > 10 ? 10 : task.getTaskTitle().length())
											  .concat(task.getTaskTitle().length() > 10 ? "..." : ""));
		
		String messageLink = ("/task-board/" + task.getScientificProject().getScientificProjectId()).concat("?myOwn=true");
		
		handleNotification(task, userAgent.getUserId(), messageBody, messageTitle, messageLink);
		
		return TaskDTO.buildDTOByEntity(task);
	}
	
	public TasksChartDTO getTaskChart(TaskChartFormFilter taskChartFormFilter) {
		log.info("Iniciando construcao de graficos");
		return taskChartService.getTaskChartsByFilter(taskChartFormFilter);
	}

    public Integer countAllTasks(Long scientificProjectId, TaskFormFilter taskFormFilter) {
    	
    	log.info("Contando lista de tarefas pelo filtro: scientificProjectId = {} , queryString = {}, taskStatus = {}, phaseName = {}, workflowName = {}", 
				 scientificProjectId, taskFormFilter.getQueryString(), taskFormFilter.getTaskStatus(), taskFormFilter.getPhaseName(), taskFormFilter.getWorkflowName());
    	
        return taskDao.getAllTasksCount(scientificProjectId, taskFormFilter);
    }
    
    public Integer countAllLateTasks(Long scientificProjectId, TaskFormFilter taskFormFilter) {
    	log.info("Contando lista de tarefas atrasadas pelo filtro: scientificProjectId = {} , queryString = {}, taskStatus = {}, phaseName = {}, workflowName = {}", 
				 scientificProjectId, taskFormFilter.getQueryString(), taskFormFilter.getTaskStatus(), taskFormFilter.getPhaseName(), taskFormFilter.getWorkflowName());

		return taskDao.getAllTasksLateCount(scientificProjectId, taskFormFilter);
	}
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
	public List<TaskDTO> getAllTasksOpenForUser(Long userId) {
		log.info("Recuperando todas as tasks abertas para o usuário de id {}", userId);
		return TaskDTO.convertEntityListToDTOList(taskDao.getAllTasksOpenForUser(userId));
	}
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
	public List<Task> getAllTasksEntityOfUserOrCreatedByUser(Long userId) {
		log.info("Recuperando todas as tasks para o usuário de id {}", userId);
		
		List<Task> tasks =  taskDao.getAllTasksOfUserOrCreatedByUser(userId);
		
		if (tasks != null && !tasks.isEmpty()) {
			
			for (Task task : tasks) {
				task.getUsersInTask().size();
			}
		}
		
		return tasks;
	}
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
	public List<Task> getAllTasksEntityOfUserGroup(Long userGroupId) {
		log.info("Recuperando todas as tasks para o grupo de usuários de id {}", userGroupId);
		
		List<Task> tasks =  taskDao.getAllTasksOfUserGroup(userGroupId);
		
		if (tasks != null && !tasks.isEmpty()) {
			
			for (Task task : tasks) {
				task.getUsersInTask().size();
			}
		}
		
		return tasks;
	}
    
	public List<Task> getAllTasksOfTag(Long tagId) {
		log.info("Recuperando todas as tasks paraa tag de id {}", tagId);
		
		List<Task> tasks =  taskDao.getAllTasksWithTag(tagId);
		
		if (tasks != null && !tasks.isEmpty()) {
			
			for (Task task : tasks) {
				task.getTags().size();
			}
		}
		
		return tasks;
	}

	public Integer getAllTasksOpenForUserCount(Long userId) {
		log.info("Recuperando todas as tarefas abertas para o usuário de id {}", userId);
		return taskDao.countTasksOpenForUserCount(userId);
	}
	
	public Integer countAllTasksByPhaseId(Long phaseId) {
		log.info("Recuperando contagem de todas as tarefas por phaseId {}", phaseId);
		return taskDao.countTasksByPhaseId(phaseId);
	}
	
	public Integer countAllTasksByWorkflowId(Long workflowId) {
		log.info("Recuperando contagem de todas as tarefas por workflowId {}", workflowId);
		return taskDao.countTasksByWorkflowId(workflowId);
	}
	

	public Integer countAllTasksByWorkflowIdAndGroup(Long workflowId, UserGroup userGroup) {
		log.info("Recuperando contagem de todas as tarefas por workflowId {} e userGroupId", workflowId, userGroup.getUserGroupId());
		return taskDao.countTasksByWorkflowIdAndGroup(workflowId, userGroup);
	}
	
	@Transactional
	public void removeTask(Long taskId, CurrentUser currentUser) {
		Task taskToBeRemoved = taskDao.findById(taskId);
		
		taskDao.delete(taskToBeRemoved);
		taskHistoryService.saveTaskHistoryForRemove(taskToBeRemoved, currentUser.getUserId());
		
		String messageBody = ("O usuário <b>").concat(currentUser.getUsername()).concat("</b> removeu a tarefa: <b>").concat(taskToBeRemoved.getTaskTitle()).concat("</b>");
		String messageTitle = ("Uma tarefa associada a você foi removida - ").concat(taskToBeRemoved.getTaskTitle().substring(0, taskToBeRemoved.getTaskTitle().length() > 10 ? 10 : taskToBeRemoved.getTaskTitle().length())
																			 .concat(taskToBeRemoved.getTaskTitle().length() > 10 ? "..." : ""));
		String messageLink = ("/task-board/" + taskToBeRemoved.getScientificProject().getScientificProjectId()).concat("?myOwn=true");
		
		handleNotification(taskToBeRemoved, currentUser.getUserId(), messageBody, messageTitle, messageLink);
	}
	
	private void handleNotification(Task task, Long userAgentId, String messageBody, String messageTitle, String messageLink) {
		
		if (task.getUserGroupInTask() != null && !"".equals(task.getUserGroupInTask().getGroupName())) {
			notificationMessageSender.sendMessage(NotificationMessageDTO.builder()
																		.actionDate(new Date())
																		.messageBody(messageBody)
																		.messageTitle(messageTitle)
																		.messageLink(messageLink)
																		.userGroupId(task.getWorkflow().getResponsibleGroup().getUserGroupId())
																		.userAgentId(userAgentId)
																		.notificationType(NotificationType.MESSAGE)
																		.build());
		}
		else if (task.getUsersInTask() != null && !task.getUsersInTask().isEmpty()) {

			for (User userInTask : task.getUsersInTask()) {
				notificationMessageSender.sendMessage(NotificationMessageDTO.builder()
																			.actionDate(new Date())
																			.messageBody(messageBody)
																			.messageTitle(messageTitle)
																			.messageLink(messageLink)
																			.userSubjectId(userInTask.getUserId())
																			.userAgentId(userAgentId)
																			.notificationType(NotificationType.MESSAGE)
																			.build());
			}
		}
	}
	
	@Transactional
	public void deleteByScientificProjectId(Long scientificProjectId) {
		log.info("Removendo tarefas por scientificProjectId {}", scientificProjectId);
		taskDao.deleteByScientificProjectId(scientificProjectId);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void disassociateUserFromTask(Task task, User user) {
		log.info("Disassociando usuario de id {} da tarefa de id {}", user.getUserId(), task.getTaskId());
		
		Boolean hasToUpdate = false;
		
		if (task.getUserCreator() != null && task.getUserCreator().getEmail().equals(user.getEmail())) {
			task.setUserCreator(null);
			hasToUpdate = true;
		}
		
		if (task.getUsersInTask() != null && !task.getUsersInTask().isEmpty()) {
			hasToUpdate = task.removeUserInTaskByEmail(user.getEmail());
		}
		
		if (hasToUpdate) {
			updateTask(task);
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void disassociateTagFromTask(Task task, Long tagId, Long userId) {
		log.info("Disassociando tag de id {} da tarefa de id {}", tagId, task.getTaskId());
		
		if (task.removeTag(tagId)) {
			User userAgent = userService.getUserEntityById(userId);
			Task updatedTask = task.cloneTask();
			
			updatedTask.setUserGroupInTask(null);
			updateTask(updatedTask);
			
			taskHistoryService.saveTaskHistoryForUpdate(task, updatedTask, userAgent);
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void disassociateUserGroupFromTask(Task task, Long userGroupId, Long userId) {
		log.info("Disassociando grupo de id {} da tarefa de id {}", userGroupId, task.getTaskId());
		if (task.getUserGroupInTask() != null && task.getUserGroupInTask().getUserGroupId().equals(userGroupId)) {
			User userAgent = userService.getUserEntityById(userId);
			Task updatedTask = task.cloneTask();
			
			updatedTask.setUserGroupInTask(null);
			updateTask(updatedTask);
			
			taskHistoryService.saveTaskHistoryForUpdate(task, updatedTask, userAgent);
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateTask(Task task) {
		log.info("Atualizando tarefa");
		taskDao.mergeTask(task);
	}
	
	public Integer countAllTasksCount(Long scientificProjectId, TaskFormFilter taskFormFilter) {
		log.info("Iniciando contagem de tarefas para grafico de performance");
		return taskDao.getAllTasksCount(scientificProjectId, taskFormFilter);
	}
	
	public Integer getPageSize() {
		return taskDao.getPaginationParameterConfig().getMaxResultsPerPage();
	}

}