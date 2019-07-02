package com.uff.scimanager.service.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uff.scimanager.domain.TaskHistory;
import com.uff.scimanager.domain.TaskHistoryType;
import com.uff.scimanager.domain.TaskStatus;
import com.uff.scimanager.domain.dto.TasksChartDTO;
import com.uff.scimanager.domain.form.TaskChartFormFilter;
import com.uff.scimanager.domain.form.TaskFormFilter;
import com.uff.scimanager.service.history.TaskHistoryService;

@Service
public class TaskChartService {

	private static final Logger log = LoggerFactory.getLogger(TaskChartService.class);
	
	private final TaskHistoryService taskHistoryService;
	private final TaskDailyUpdateChartService taskDailyUpdateChartService;
	private final TaskCumulativeChartService taskCumulativeChartService;
	
	@Autowired
	private TaskService taskService;

	@Autowired
	public TaskChartService(TaskHistoryService taskHistoryService, TaskDailyUpdateChartService taskDailyUpdateChartService,
							TaskCumulativeChartService taskCumulativeChartService) {
		
		this.taskHistoryService = taskHistoryService;
		this.taskDailyUpdateChartService = taskDailyUpdateChartService;
		this.taskCumulativeChartService = taskCumulativeChartService;
	}
	
	public TasksChartDTO getTaskChartsByFilter(TaskChartFormFilter taskChartFormFilter) {
		
		log.info("Gerando grafico pelos filtros: initialDate = {}, finalDate = {}, userAgentId = {}, scientificProjectId = {}, workflowId = {}, phaseId = {}",
				taskChartFormFilter.getInitialDate(), taskChartFormFilter.getFinalDate(), taskChartFormFilter.getUserId(), taskChartFormFilter.getScientificProjectId(), 
				taskChartFormFilter.getWorkflowId(), taskChartFormFilter.getPhaseId());
		
		List<TaskHistory> taskUpdateHistory = taskHistoryService.getTasksHistory(taskChartFormFilter, TaskHistoryType.UPDATED);
		log.info("Total de {} acoes de update encontradas para o projeto {}", taskUpdateHistory != null ? taskUpdateHistory.size() : 0, taskChartFormFilter.getScientificProjectId());
		
		List<TaskHistory> taskCreatedHistory = taskHistoryService.getTasksHistory(taskChartFormFilter, TaskHistoryType.CREATED);
		log.info("Total de {} acoes de criacao encontradas para o projeto {}", taskUpdateHistory != null ? taskUpdateHistory.size() : 0, taskChartFormFilter.getScientificProjectId());
		
		List<TaskHistory> taskRemovedHistory = taskHistoryService.getTasksHistory(taskChartFormFilter, TaskHistoryType.REMOVED);
		log.info("Total de {} acoes de remocao encontradas para o projeto {}", taskUpdateHistory != null ? taskUpdateHistory.size() : 0, taskChartFormFilter.getScientificProjectId());
		
		Map<String, Integer> tasksTotalByStatus = new HashMap<String, Integer>();
		TaskFormFilter taskFilterDTO = TaskFormFilter.builder()
												   .taskStatus(TaskStatus.DONE.name())
												   .phaseId(taskChartFormFilter.getPhaseId())
												   .workflowId(taskChartFormFilter.getWorkflowId())
												   .userId(taskChartFormFilter.getUserId()).build();
		
		tasksTotalByStatus.put(TaskStatus.DONE.name(), taskService.countAllTasksCount(taskChartFormFilter.getScientificProjectId(), taskFilterDTO));
		taskFilterDTO.setTaskStatus(TaskStatus.DOING.name());
		tasksTotalByStatus.put(TaskStatus.DOING.name(), taskService.countAllTasksCount(taskChartFormFilter.getScientificProjectId(), taskFilterDTO));
		taskFilterDTO.setTaskStatus(TaskStatus.TODO.name());
		tasksTotalByStatus.put(TaskStatus.TODO.name(), taskService.countAllTasksCount(taskChartFormFilter.getScientificProjectId(), taskFilterDTO));
		
		
		TasksChartDTO tasksChartDTO = TasksChartDTO.builder()
								                .tasksDailyUpdateChart(taskDailyUpdateChartService.getTaskDailyUpdateChartByFilter(taskChartFormFilter, new HashMap<String, Integer>(tasksTotalByStatus),
								                																				   taskUpdateHistory, taskCreatedHistory, taskRemovedHistory))
								                .tasksCumulativeChart(taskCumulativeChartService.getTaskCumulativeChartByFilter(taskChartFormFilter, new HashMap<String, Integer>(tasksTotalByStatus),
																															    taskUpdateHistory, taskCreatedHistory, taskRemovedHistory))
								                .build();
		return tasksChartDTO;
	}

}