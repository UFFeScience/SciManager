package com.uff.scimanager.service.task;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.uff.scimanager.domain.Field;
import com.uff.scimanager.domain.TaskHistory;
import com.uff.scimanager.domain.TaskStatus;
import com.uff.scimanager.domain.dto.TasksChartDataDTO;
import com.uff.scimanager.domain.form.TaskChartFormFilter;
import com.uff.scimanager.util.CalendarDateUtils;

@Service
public class TaskCumulativeChartService {

	private static final Logger log = LoggerFactory.getLogger(TaskCumulativeChartService.class);
	
	public TasksChartDataDTO getTaskCumulativeChartByFilter(TaskChartFormFilter taskChartFormFilter, Map<String, Integer> tasksTotalByStatus, List<TaskHistory> taskUpdateHistory, 
														   List<TaskHistory> taskCreatedHistory, List<TaskHistory> taskRemovedHistory) {
		
		log.info("Gerando grafico pelos filtros: initialDate = {}, finalDate = {}, userAgentId = {}, scientificProjectId = {}, workflowId = {}, phaseId = {}",
				taskChartFormFilter.getInitialDate(), taskChartFormFilter.getFinalDate(), taskChartFormFilter.getUserId(), taskChartFormFilter.getScientificProjectId(), 
				taskChartFormFilter.getWorkflowId(), taskChartFormFilter.getPhaseId());
		
		TasksChartDataDTO taskMetricChart = new TasksChartDataDTO();
		
		Integer currentToDoTotal = tasksTotalByStatus.get(TaskStatus.TODO.name());
		Integer currentDoingTotal = tasksTotalByStatus.get(TaskStatus.DOING.name());
		
		tasksTotalByStatus.put(TaskStatus.TODO.name(), currentDoingTotal + tasksTotalByStatus.get(TaskStatus.DONE.name()) + currentToDoTotal);
		tasksTotalByStatus.put(TaskStatus.DOING.name(), tasksTotalByStatus.get(TaskStatus.DONE.name()) + currentDoingTotal);
		
		Calendar finalDateCalendar = CalendarDateUtils.createCalendarFromString(taskChartFormFilter.getFinalDate());
		Calendar initialDateCalendar = CalendarDateUtils.createCalendarFromString(taskChartFormFilter.getInitialDate());
		taskMetricChart.addChartEntryPoint(CalendarDateUtils.formatDateWithoutHours(finalDateCalendar), tasksTotalByStatus.get(TaskStatus.TODO.name()), 
																									    tasksTotalByStatus.get(TaskStatus.DOING.name()), 
																									    tasksTotalByStatus.get(TaskStatus.DONE.name()));
		for (Calendar day = finalDateCalendar; 
			 initialDateCalendar != null && finalDateCalendar != null &&
			 (initialDateCalendar.before(day) || CalendarDateUtils.isSameDay(initialDateCalendar, day));
			 day.add(Calendar.DAY_OF_YEAR, - 1)) {
		
			String currentDayText = CalendarDateUtils.formatDateWithoutHours(day);
			updateTasksInfoByDay(taskUpdateHistory, taskCreatedHistory, taskRemovedHistory, tasksTotalByStatus, day);
			taskMetricChart.addChartEntryPoint(currentDayText, tasksTotalByStatus.get(TaskStatus.TODO.name()), 
															   tasksTotalByStatus.get(TaskStatus.DOING.name()), 
															   tasksTotalByStatus.get(TaskStatus.DONE.name()));
		}
		
		taskMetricChart.reverseDataOrder();
		
		return taskMetricChart;
	}

	private void updateTasksInfoByDay(List<TaskHistory> taskUpdateHistory, List<TaskHistory> taskCreatedHistory,
									  List<TaskHistory> taskRemovedHistory, Map<String, Integer> tasksTotalByStatus, Calendar day) {
		
		day.add(Calendar.DAY_OF_YEAR, + 1);
		
		for (TaskHistory historyByDay : taskUpdateHistory) {
			if (CalendarDateUtils.isSameDay(CalendarDateUtils.getCalendarFromDate(historyByDay.getActionDate()), day)) {
				setTaskUpdates(tasksTotalByStatus, historyByDay);
			}
		}
		
		for (TaskHistory historyByDay : taskCreatedHistory) {
			if (CalendarDateUtils.isSameDay(CalendarDateUtils.getCalendarFromDate(historyByDay.getActionDate()), day)) {
				setTaskCreations(tasksTotalByStatus, historyByDay);
			}
		}
		
		for (TaskHistory historyByDay : taskRemovedHistory) {
			if (CalendarDateUtils.isSameDay(CalendarDateUtils.getCalendarFromDate(historyByDay.getActionDate()), day)) {
				setTaskDeletes(tasksTotalByStatus, historyByDay);
			}
		}
		
		day.add(Calendar.DAY_OF_YEAR, - 1);
	}
	
	private void setTaskDeletes(Map<String, Integer> tasksTotalByStatus, TaskHistory historyByDay) {
		setTaskDeletedDecrements(tasksTotalByStatus, historyByDay);
	}
	
	private void setTaskDeletedDecrements(Map<String, Integer> tasksTotalByStatus, TaskHistory historyByDay) {
		if (historyByDay != null && (TaskStatus.TODO).equals(historyByDay.getCurrentStatus())) {
			tasksTotalByStatus.put(TaskStatus.TODO.name(), tasksTotalByStatus.get(TaskStatus.TODO.name()) + 1);
		}
		
		else if (historyByDay != null && (TaskStatus.DOING).equals(historyByDay.getCurrentStatus())) {
			tasksTotalByStatus.put(TaskStatus.DOING.name(), tasksTotalByStatus.get(TaskStatus.DOING.name()) + 1);
			tasksTotalByStatus.put(TaskStatus.TODO.name(), tasksTotalByStatus.get(TaskStatus.TODO.name()) + 1);
		}
		
		else if (historyByDay != null && (TaskStatus.DONE).equals(historyByDay.getCurrentStatus())) {
			tasksTotalByStatus.put(TaskStatus.DONE.name(), tasksTotalByStatus.get(TaskStatus.DONE.name()) + 1);
			tasksTotalByStatus.put(TaskStatus.DOING.name(), tasksTotalByStatus.get(TaskStatus.DOING.name()) + 1);
			tasksTotalByStatus.put(TaskStatus.TODO.name(), tasksTotalByStatus.get(TaskStatus.TODO.name()) + 1);
		}
	}
	
	private void setTaskCreations(Map<String, Integer> tasksTotalByStatus, TaskHistory historyByDay) {
		setTaskCreateDecrements(tasksTotalByStatus, historyByDay);
	}
	
	private void setTaskCreateDecrements(Map<String, Integer> tasksTotalByStatus, TaskHistory historyByDay) {
		if (historyByDay != null && (TaskStatus.TODO).equals(historyByDay.getCurrentStatus())) {
			if ((tasksTotalByStatus.get(TaskStatus.TODO.name()) - 1) >= 0) {
				tasksTotalByStatus.put(TaskStatus.TODO.name(), tasksTotalByStatus.get(TaskStatus.TODO.name()) - 1);
			}
		}
		
		else if (historyByDay != null && (TaskStatus.DOING).equals(historyByDay.getCurrentStatus())) {
			if ((tasksTotalByStatus.get(TaskStatus.DOING.name()) - 1) >= 0) {
				tasksTotalByStatus.put(TaskStatus.DOING.name(), tasksTotalByStatus.get(TaskStatus.DOING.name()) - 1);
			}
		}
		
		else if (historyByDay != null && (TaskStatus.DONE).equals(historyByDay.getCurrentStatus())) {
			if ((tasksTotalByStatus.get(TaskStatus.DONE.name()) - 1) >= 0) {
				tasksTotalByStatus.put(TaskStatus.DONE.name(), tasksTotalByStatus.get(TaskStatus.DONE.name()) - 1);
			}
		}
	}

	private void setTaskUpdates(Map<String, Integer> tasksTotalByStatus, TaskHistory historyByDay) {
		Field changeSetField = historyByDay != null ? historyByDay.getStatusField() : null;
		setTaskUpdatesDecrements(tasksTotalByStatus, changeSetField);
	}
	
	private void setTaskUpdatesDecrements(Map<String, Integer> tasksTotalByStatus, Field changeSetField) {
		if (changeSetField != null && (TaskStatus.TODO).equals(TaskStatus.valueOf((String) changeSetField.getUpdatedValue()))) {
			if ((tasksTotalByStatus.get(TaskStatus.TODO.name()) - 1) >= 0) {
				tasksTotalByStatus.put(TaskStatus.TODO.name(), tasksTotalByStatus.get(TaskStatus.TODO.name()) - 1);
			}
		}
		
		else if (changeSetField != null && (TaskStatus.DOING).equals(TaskStatus.valueOf((String) changeSetField.getUpdatedValue()))) {
			if ((tasksTotalByStatus.get(TaskStatus.DOING.name()) - 1) >= 0) {
				tasksTotalByStatus.put(TaskStatus.DOING.name(), tasksTotalByStatus.get(TaskStatus.DOING.name()) - 1);
			}
		}
		
		else if (changeSetField != null && (TaskStatus.DONE).equals(TaskStatus.valueOf((String) changeSetField.getUpdatedValue()))) {
			if ((tasksTotalByStatus.get(TaskStatus.DONE.name()) - 1) >= 0) {
				tasksTotalByStatus.put(TaskStatus.DONE.name(), tasksTotalByStatus.get(TaskStatus.DONE.name()) - 1);
			}		
		}
	}
	
}