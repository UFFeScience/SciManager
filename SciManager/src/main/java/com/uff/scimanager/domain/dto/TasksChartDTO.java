package com.uff.scimanager.domain.dto;

public class TasksChartDTO {
	
	private TasksChartDataDTO tasksCumulativeChart;
	private TasksChartDataDTO tasksDailyUpdateChart;
	
	public TasksChartDTO() {}
	
	public TasksChartDTO(TasksChartDTOBuilder tasksChartDTOBuilder) {
		this.tasksCumulativeChart = tasksChartDTOBuilder.tasksCumulativeChart;
		this.tasksDailyUpdateChart = tasksChartDTOBuilder.tasksDailyUpdateChart;
	}
	
	public TasksChartDataDTO getTasksCumulativeChart() {
		return tasksCumulativeChart;
	}

	public void setTasksCumulativeChart(TasksChartDataDTO tasksCumulativeChart) {
		this.tasksCumulativeChart = tasksCumulativeChart;
	}

	public TasksChartDataDTO getTasksDailyUpdateChart() {
		return tasksDailyUpdateChart;
	}

	public void setTasksDailyUpdateChart(TasksChartDataDTO tasksDailyUpdateChart) {
		this.tasksDailyUpdateChart = tasksDailyUpdateChart;
	}

	public static TasksChartDTOBuilder builder() {
		return new TasksChartDTOBuilder();
	}
	
	public static class TasksChartDTOBuilder {
		
		private TasksChartDataDTO tasksCumulativeChart;
		private TasksChartDataDTO tasksDailyUpdateChart;
		
		public TasksChartDTOBuilder tasksCumulativeChart(TasksChartDataDTO tasksCumulativeChart) {
			this.tasksCumulativeChart = tasksCumulativeChart;
			return this;
		}
		
		public TasksChartDTOBuilder tasksDailyUpdateChart(TasksChartDataDTO tasksDailyUpdateChart) {
			this.tasksDailyUpdateChart = tasksDailyUpdateChart;
			return this;
		}
		
		public TasksChartDTO build() {
			return new TasksChartDTO(this);
		}
	}

}