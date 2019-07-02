package com.uff.scimanager.domain.form;

public class TaskFormFilter {
	
	private Integer pageNumber = 1;
	private String taskStatus;
	private String phaseName;
	private Long phaseId;
	private String workflowName;
	private Long workflowId;
	private Long userId;
	private String queryString;
	private String initialDate;
	private String finalDate;
	private Boolean myOwn;
	
	public TaskFormFilter() {}
	
	public TaskFormFilter(TaskFilterDataDTOBuilder taskFilterDataDTOBuilder) {
		this.pageNumber = taskFilterDataDTOBuilder.pageNumber;
		this.taskStatus = taskFilterDataDTOBuilder.taskStatus;
		this.phaseName = taskFilterDataDTOBuilder.phaseName;
		this.phaseId = taskFilterDataDTOBuilder.phaseId;
		this.workflowName = taskFilterDataDTOBuilder.workflowName;
		this.workflowId = taskFilterDataDTOBuilder.workflowId;
		this.userId = taskFilterDataDTOBuilder.userId;
		this.queryString = taskFilterDataDTOBuilder.queryString;
		this.initialDate = taskFilterDataDTOBuilder.initialDate;
		this.finalDate = taskFilterDataDTOBuilder.finalDate;
		this.myOwn = taskFilterDataDTOBuilder.myOwn;
	}
	
	public Integer getPageNumber() {
		return pageNumber;
	}
	
	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}
	
	public Integer getPage() {
		return pageNumber;
	}
	
	public void setPage(Integer page) {
		this.pageNumber = page;
	}
	
	public String getTaskStatus() {
		return taskStatus;
	}
	
	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}
	
	public String getPhaseName() {
		return phaseName;
	}
	
	public void setPhaseName(String phaseName) {
		this.phaseName = phaseName;
	}
	
	public String getWorkflowName() {
		return workflowName;
	}
	
	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}
	
	public String getQueryString() {
		return queryString;
	}
	
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}
	
	public String getSearch() {
		return queryString;
	}
	
	public void setSearch(String search) {
		this.queryString = search;
	}
	
	public String getInitialDate() {
		return initialDate;
	}
	
	public void setInitialDate(String initialDate) {
		this.initialDate = initialDate;
	}
	
	public String getFinalDate() {
		return finalDate;
	}
	
	public void setFinalDate(String finalDate) {
		this.finalDate = finalDate;
	}
	
	public Boolean getMyOwn() {
		return myOwn;
	}

	public void setMyOwn(Boolean myOwn) {
		this.myOwn = myOwn;
	}

	public Long getPhaseId() {
		return phaseId;
	}

	public void setPhaseId(Long phaseId) {
		this.phaseId = phaseId;
	}

	public Long getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(Long workflowId) {
		this.workflowId = workflowId;
	}
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public static TaskFilterDataDTOBuilder builder() {
		return new TaskFilterDataDTOBuilder();
	}
	
	public static class TaskFilterDataDTOBuilder {
		
		private Integer pageNumber = 1;
		private String taskStatus;
		private String phaseName;
		private Long phaseId;
		private String workflowName;
		private Long workflowId;
		private Long userId;
		private String queryString;
		private String initialDate;
		private String finalDate;
		private Boolean myOwn;
		
		public TaskFilterDataDTOBuilder pageNumber(Integer pageNumber) {
			this.pageNumber = pageNumber;
			return this;
		}
		
		public TaskFilterDataDTOBuilder taskStatus(String taskStatus) {
			this.taskStatus = taskStatus;
			return this;
		}
		
		public TaskFilterDataDTOBuilder phaseName(String phaseName) {
			this.phaseName = phaseName;
			return this;
		}
		
		public TaskFilterDataDTOBuilder phaseId(Long phaseId) {
			this.phaseId = phaseId;
			return this;
		}
		
		public TaskFilterDataDTOBuilder userId(Long userId) {
			this.userId = userId;
			return this;
		}
		
		public TaskFilterDataDTOBuilder workflowName(String workflowName) {
			this.workflowName = workflowName;
			return this;
		}
		
		public TaskFilterDataDTOBuilder workflowId(Long workflowId) {
			this.workflowId = workflowId;
			return this;
		}
		
		public TaskFilterDataDTOBuilder queryString(String queryString) {
			this.queryString = queryString;
			return this;
		}
		
		public TaskFilterDataDTOBuilder initialDate(String initialDate) {
			this.initialDate = initialDate;
			return this;
		}
		
		public TaskFilterDataDTOBuilder finalDate(String finalDate) {
			this.finalDate = finalDate;
			return this;
		}
		
		public TaskFilterDataDTOBuilder myOwn(Boolean myOwn) {
			this.myOwn = myOwn;
			return this;
		}
		
		public TaskFormFilter build() {
			return new TaskFormFilter(this);
		}
	}
	
}
