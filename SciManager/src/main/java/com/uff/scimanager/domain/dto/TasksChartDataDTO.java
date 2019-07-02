package com.uff.scimanager.domain.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TasksChartDataDTO {
	
	private List<String> chartDates;
	private List<Integer> tasksTodoByDay;
	private List<Integer> tasksDoingByDay;
	private List<Integer> tasksDoneByDay;
	
	public TasksChartDataDTO() {}
	
	public TasksChartDataDTO(TasksChartDataDTOBuilder tasksMetricChartDTOBuilder) {
		this.chartDates = tasksMetricChartDTOBuilder.chartDates;
		this.tasksTodoByDay = tasksMetricChartDTOBuilder.tasksTodoByDay;
		this.tasksDoingByDay = tasksMetricChartDTOBuilder.tasksDoingByDay;
		this.tasksDoneByDay = tasksMetricChartDTOBuilder.tasksDoneByDay;
	}
	
	public void addChartEntryPoint(String finalDate, Integer totalTodoTasks, Integer totalDoingTasks, Integer totalDoneTasks) {
		getChartDates().add(finalDate);
		getTasksTodoByDay().add(totalTodoTasks);
		getTaskDoingsByDay().add(totalDoingTasks);
		getTasksDoneByDay().add(totalDoneTasks);
	}
	
	public List<String> getChartDates() {
		if (chartDates == null) {
			chartDates = new ArrayList<String>();
		}
		
		return chartDates;
	}
	
	public void setChartDates(List<String> chartDates) {
		this.chartDates = chartDates;
	}
	
	public List<Integer> getTasksTodoByDay() {
		if (tasksTodoByDay == null) {
			tasksTodoByDay = new ArrayList<Integer>();;
		}
		
		return tasksTodoByDay;
	}
	
	public void setTasksTodoByDay(List<Integer> tasksTodoByDay) {
		this.tasksTodoByDay = tasksTodoByDay;
	}
	
	public List<Integer> getTaskDoingsByDay() {
		if (tasksDoingByDay == null) {
			tasksDoingByDay = new ArrayList<Integer>();
		}
		
		return tasksDoingByDay;
	}
	
	public void setTaskDoingsByDay(List<Integer> tasksDoingByDay) {
		this.tasksDoingByDay = tasksDoingByDay;
	}
	
	public List<Integer> getTasksDoneByDay() {
		if (tasksDoneByDay == null) {
			tasksDoneByDay = new ArrayList<Integer>();;
		}
		
		return tasksDoneByDay;
	}
	
	public void setTasksDoneByDay(List<Integer> tasksDoneByDay) {
		this.tasksDoneByDay = tasksDoneByDay;
	}
	
	public void reverseDataOrder() {
		Collections.reverse(getChartDates());
		Collections.reverse(getTasksTodoByDay());
		Collections.reverse(getTaskDoingsByDay());
		Collections.reverse(getTasksDoneByDay());
	}
	
	public static TasksChartDataDTOBuilder builder() {
		return new TasksChartDataDTOBuilder();
	}
	
	public static class TasksChartDataDTOBuilder {
		
		private List<String> chartDates;
		private List<Integer> tasksTodoByDay;
		private List<Integer> tasksDoingByDay;
		private List<Integer> tasksDoneByDay;
		
		public TasksChartDataDTOBuilder chartDates(List<String> chartDates) {
			this.chartDates = chartDates;
			return this;
		}
		
		public TasksChartDataDTOBuilder tasksTodoByDay(List<Integer> tasksTodoByDay) {
			this.tasksTodoByDay = tasksTodoByDay;
			return this;
		}
		
		public TasksChartDataDTOBuilder tasksDoingByDay(List<Integer> tasksDoingByDay) {
			this.tasksDoingByDay = tasksDoingByDay;
			return this;
		}
		
		public TasksChartDataDTOBuilder tasksDoneByDay(List<Integer> tasksDoneByDay) {
			this.tasksDoneByDay = tasksDoneByDay;
			return this;
		}
		
		public TasksChartDataDTO build() {
			return new TasksChartDataDTO(this);
		}
	}

}