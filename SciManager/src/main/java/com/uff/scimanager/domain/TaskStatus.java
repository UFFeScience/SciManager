package com.uff.scimanager.domain;

public enum TaskStatus {
	
	TODO("A fazer"), DOING("Em progresso"), DONE("Concluído");
	
	private final String statusName;

	TaskStatus(String statusName) {
		this.statusName = statusName;
	}
	
	public String getStatusName() {
		return statusName;
	}

	public static TaskStatus getStatusFromString(String taskStatus) {
		for (TaskStatus status : TaskStatus.values()) {
			if (status.name().equals(taskStatus) || status.getStatusName().equals(taskStatus)) {
				return status;
			}
		}
		
		return null;
	}
	
}