package com.uff.scimanager.service.history;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Throwables;
import com.uff.scimanager.domain.Field;
import com.uff.scimanager.domain.Task;
import com.uff.scimanager.domain.TaskHistory;
import com.uff.scimanager.domain.TaskHistoryType;
import com.uff.scimanager.domain.User;
import com.uff.scimanager.domain.UserGroup;
import com.uff.scimanager.domain.dto.PhaseDTO;
import com.uff.scimanager.domain.dto.TaskDTO;
import com.uff.scimanager.domain.form.TaskChartFormFilter;
import com.uff.scimanager.repository.history.TaskHistoryRepository;
import com.uff.scimanager.util.CalendarDateUtils;

@Service
public class TaskHistoryService {

	private static final Logger log = LoggerFactory.getLogger(TaskHistoryService.class);
	
	private final TaskHistoryRepository taskHistoryRepository;
	
	@Autowired
	public TaskHistoryService(TaskHistoryRepository taskHistoryRepository) {
		this.taskHistoryRepository = taskHistoryRepository;
	}
	
	@Async
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveTaskHistoryCreate(Task task, User user) {
		log.info("Salvando historico de criacao de tarefa");
		
		TaskHistory taskHistory;

		try {
			if (user == null) {
				log.error("Erro ao salvar histórico de criacao de tarefa, user nao encontrado");
				return;
			}
			
			taskHistory = TaskHistory.builder()
								     .buildTaskDTOFields(TaskDTO.buildDTOByEntity(task))
								     .historyType(TaskHistoryType.CREATED)
									 .userAgentId(user.getUserId())
									 .creationDate(task.getCreationDate().getTime())
									 .actionDate(Calendar.getInstance(TimeZone.getTimeZone(CalendarDateUtils.DATE_LOCALE)).getTime())
									 .build();
			
			taskHistoryRepository.save(taskHistory);
			
		} 
		catch (Exception e) {
			log.error("Erro ao salvar historico de criacao de tarefa\n{}", Throwables.getStackTraceAsString(e));
		} 
	}

	@Async
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveTaskHistoryForRemove(Task task, Long userId) {
		log.info("Salvando historico de remocao de tarefa");
		
		TaskHistory taskHistory;
		
		try {
			
			if (userId == null) {
				log.error("Erro ao salvar histórico de criacao de tarefa, user nao encontrado");
				return;
			}
			
			taskHistory = TaskHistory.builder()
									 .buildTaskDTOFields(TaskDTO.buildDTOByEntity(task))
									 .historyType(TaskHistoryType.REMOVED)
									 .userAgentId(userId)
									 .creationDate(task.getCreationDate().getTime())
									 .actionDate(Calendar.getInstance(TimeZone.getTimeZone(CalendarDateUtils.DATE_LOCALE)).getTime())
									 .build();
			
			taskHistoryRepository.save(taskHistory);
			
		} 
		catch (Exception e) {
			log.error("Erro ao salvar historico de remocao de tarefa\n{}", Throwables.getStackTraceAsString(e));
		} 
	}
	
	@Async
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveTaskHistoryForUpdate(Task oldTask, Task updatedTask, User user) {
		if (oldTask == null || updatedTask == null) {
			log.info("Nao e possivel salvar historico de atualizacao pois uma das entidades esta nulas");
			return;
		}
		
		log.info("Salvando historico de criacao de tarefa");
		
		TaskHistory taskHistory;

		try {
			if (user == null) {
				log.error("Erro ao salvar histórico de criacao de tarefa, user nao encontrado");
				return;
			}
			
			taskHistory = TaskHistory.builder()
									 .buildTaskDTOFields(TaskDTO.buildDTOByEntity(oldTask))
									 .historyType(TaskHistoryType.UPDATED)
									 .userAgentId(user.getUserId())
									 .changeSet(mountFieldsChanges(oldTask, updatedTask))
									 .creationDate(oldTask.getCreationDate().getTime())
									 .actionDate(Calendar.getInstance(TimeZone.getTimeZone(CalendarDateUtils.DATE_LOCALE)).getTime())
									 .build();
			
			taskHistoryRepository.save(taskHistory);
			
		} 
		catch (Exception e) {
			log.error("Erro ao salvar historico de criacao de tarefa\n{}", Throwables.getStackTraceAsString(e));
		} 
	}
	
	private List<Field> mountFieldsChanges(Task oldTask, Task updatedTask) {
		List<Field> changeSet = new ArrayList<Field>();
		
		verifyFieldChanges(oldTask.getDeadline(), updatedTask.getDeadline(), "deadline", changeSet);
		verifyFieldChanges(oldTask.getDescription(), updatedTask.getDescription(), "description", changeSet);
		verifyFieldChanges(oldTask.getEstimatedTime(), updatedTask.getEstimatedTime(), "estimatedTime", changeSet);
		verifyFieldChanges(oldTask.getTaskTitle(), updatedTask.getTaskTitle(), "taskTitle", changeSet);
		verifyFieldChanges(oldTask.getUrlRepository(), updatedTask.getUrlRepository(), "urlRepository", changeSet);
		verifyFieldChanges(oldTask.getStatus(), updatedTask.getStatus(), "status", changeSet);
		
		if ((oldTask.getWorkflow() == null && updatedTask.getWorkflow() != null) ||
		   (oldTask.getWorkflow() != null && updatedTask.getWorkflow() == null) ||
		   (oldTask.getWorkflow() != null && updatedTask.getWorkflow() != null &&
		   !oldTask.getWorkflow().getWorkflowId().equals(updatedTask.getWorkflow().getWorkflowId()))) {
			
			changeSet.add(Field.builder().name("workflow")
										 .oldValue(oldTask.getWorkflowSimpleDTO())
										 .updatedValue(updatedTask.getWorkflowSimpleDTO()).build());
		}
		
		if ((oldTask.getPhase() == null && updatedTask.getPhase() != null) ||
		   (oldTask.getPhase() != null && updatedTask.getPhase() == null) ||
		   (oldTask.getPhase() != null && updatedTask.getPhase() != null &&
		   !oldTask.getPhase().getPhaseId().equals(updatedTask.getPhase().getPhaseId()))) {
			
			changeSet.add(Field.builder().name("phase")
										 .oldValue(PhaseDTO.buildDTOByEntity(oldTask.getPhase()))
										 .updatedValue(PhaseDTO.buildDTOByEntity(updatedTask.getPhase())).build());
		}
		
		if (compareGroups(oldTask.getUserGroupInTask(), updatedTask.getUserGroupInTask())) {
			changeSet.add(Field.builder().name("userGroup")
										 .oldValue(oldTask.getUserGroupInTask() != null ? oldTask.getUserGroupInTask().buildUserGroupSimpleDTO() : null)
										 .updatedValue(updatedTask.getUserGroupInTask() != null ? updatedTask.getUserGroupInTask().buildUserGroupSimpleDTO() : null).build());
		}
		
		if (compareUsersInTask(oldTask.getUsersInTask(), updatedTask.getUsersInTask())) {
			changeSet.add(Field.builder().name("usersInTask")
										 .oldValue(oldTask.getUsersInTaskSimpleDTO())
										 .updatedValue(updatedTask.getUsersInTaskSimpleDTO()).build());
		}
		
		return changeSet;
	}

	private void verifyFieldChanges(Object oldValue, Object updatedValue, String fieldName, List<Field> changeSet) {
		if ((oldValue == null && updatedValue != null) || (oldValue != null && updatedValue == null) ||
		   (oldValue != null && updatedValue != null && !oldValue.equals(updatedValue))) {
			
			changeSet.add(Field.builder().name(fieldName)
										 .oldValue(oldValue)
										 .updatedValue(updatedValue).build());
		}
	}
	
	private Boolean compareUsersInTask(Set<User> oldUsersInTask, Set<User> newUsersInTask) {
		if (oldUsersInTask != null && newUsersInTask == null || oldUsersInTask == null && newUsersInTask != null) {
			return true;
		}
		
		if (oldUsersInTask != null && newUsersInTask != null) {
			return verifyRemovedUser(oldUsersInTask, newUsersInTask) || verifyAddedRemovedUser(oldUsersInTask, newUsersInTask);
		}
		
		return false;
	}
	
	public Boolean verifyRemovedUser(Set<User> oldUsersInTask, Set<User> newUsersInTask) {
		for (User oldUser : oldUsersInTask) {
			Boolean hasUser = false;
			
			for (User newUser : newUsersInTask) {
				if (oldUser.getEmail().equals(newUser.getEmail())) {
					hasUser = true;
					break;
				}
			}
			
			if (!hasUser) {
				return true;
			}
		}
		
		return false;
	}
	
	public Boolean verifyAddedRemovedUser(Set<User> oldUsersInTask, Set<User> newUsersInTask) {
		for (User newUser : newUsersInTask) {
			Boolean hasUser = false;
			
			for (User oldUser : oldUsersInTask) {
				if (oldUser.getEmail().equals(newUser.getEmail())) {
					hasUser = true;
					break;
				}
			}
			
			if (!hasUser) {
				return true;
			}
		}
		
		return false;
	}

	private Boolean compareGroups(UserGroup oldUserGroupInTask, UserGroup newUserGroupInTask) {
		if ((oldUserGroupInTask == null && newUserGroupInTask != null || oldUserGroupInTask != null && newUserGroupInTask == null) ||
		   (oldUserGroupInTask != null && newUserGroupInTask != null && 
		   !oldUserGroupInTask.getUserGroupId().equals(newUserGroupInTask.getUserGroupId()))) {
			return true;
		}
		
		return false;
	}
	
	public List<TaskHistory> getTasksHistory(TaskChartFormFilter taskChartFilterDTO, TaskHistoryType taskHistoryType) {
		return taskHistoryRepository.getTasksHistoryByFilter(CalendarDateUtils.createCalendarFromString(taskChartFilterDTO.getInitialDate()), 
															 CalendarDateUtils.createCalendarFromString(taskChartFilterDTO.getFinalDate()), 
															 taskChartFilterDTO, taskHistoryType);
	}

}