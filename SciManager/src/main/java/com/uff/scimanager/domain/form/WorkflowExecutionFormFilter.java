package com.uff.scimanager.domain.form;

public class WorkflowExecutionFormFilter {
	
	private Integer pageNumber = 1;
	private String workflowName;
	private Long workflowId;
	private String execTag;
	private String queryString;
	private String initialDate;
	private String finalDate;
	private String executionStatus;
	private Boolean myOwn;
	private Long userId;
	
	public WorkflowExecutionFormFilter() {}
	
	public WorkflowExecutionFormFilter(WorkflowExecutionFormFilterBuilder workflowExecutionFormFilterBuilder) {
		this.pageNumber = workflowExecutionFormFilterBuilder.pageNumber;
		this.workflowId = workflowExecutionFormFilterBuilder.workflowId;
		this.workflowName = workflowExecutionFormFilterBuilder.workflowName;
		this.execTag = workflowExecutionFormFilterBuilder.execTag;
		this.queryString = workflowExecutionFormFilterBuilder.queryString;
		this.initialDate = workflowExecutionFormFilterBuilder.initialDate;
		this.finalDate = workflowExecutionFormFilterBuilder.finalDate;
		this.executionStatus = workflowExecutionFormFilterBuilder.executionStatus;
		this.myOwn = workflowExecutionFormFilterBuilder.myOwn;
		this.userId = workflowExecutionFormFilterBuilder.userId;
	}
	
	public Integer getPageNumber() {
		return pageNumber;
	}
	
	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}
	
	public Long getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(Long workflowId) {
		this.workflowId = workflowId;
	}
	
	public String getWorkflowName() {
		return workflowName;
	}

	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}

	public Integer getPage() {
		return pageNumber;
	}
	
	public void setPage(Integer page) {
		this.pageNumber = page;
	}
	
	public String getExecTag() {
		return execTag;
	}

	public void setExecTag(String execTag) {
		this.execTag = execTag;
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
	
	public String getExecutionStatus() {
		return executionStatus;
	}
	
	public void setExecutionStatus(String executionStatus) {
		this.executionStatus = executionStatus;
	}
	
	public Boolean getMyOwn() {
		return myOwn;
	}

	public void setMyOwn(Boolean myOwn) {
		this.myOwn = myOwn;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public static WorkflowExecutionFormFilterBuilder builder() {
		return new WorkflowExecutionFormFilterBuilder();
	}
	
	public static class WorkflowExecutionFormFilterBuilder {
		
		private Integer pageNumber = 1;
		private String workflowName;
		private Long workflowId;
		private String execTag;
		private String queryString;
		private String initialDate;
		private String finalDate;
		private String executionStatus;
		private Boolean myOwn;
		private Long userId;
		
		public WorkflowExecutionFormFilterBuilder pageNumber(Integer pageNumber) {
			this.pageNumber = pageNumber;
			return this;
		}
		
		public WorkflowExecutionFormFilterBuilder workflowName(String workflowName) {
			this.workflowName = workflowName;
			return this;
		}
		
		public WorkflowExecutionFormFilterBuilder workflowId(Long workflowId) {
			this.workflowId = workflowId;
			return this;
		}
		
		public WorkflowExecutionFormFilterBuilder executionStatus(String executionStatus) {
			this.executionStatus = executionStatus;
			return this;
		}
		
		public WorkflowExecutionFormFilterBuilder userId(Long userId) {
			this.userId = userId;
			return this;
		}
		
		public WorkflowExecutionFormFilterBuilder execTag(String execTag) {
			this.execTag = execTag;
			return this;
		}
		
		public WorkflowExecutionFormFilterBuilder queryString(String queryString) {
			this.queryString = queryString;
			return this;
		}
		
		public WorkflowExecutionFormFilterBuilder initialDate(String initialDate) {
			this.initialDate = initialDate;
			return this;
		}
		
		public WorkflowExecutionFormFilterBuilder finalDate(String finalDate) {
			this.finalDate = finalDate;
			return this;
		}
		
		public WorkflowExecutionFormFilterBuilder myOwn(Boolean myOwn) {
			this.myOwn = myOwn;
			return this;
		}
		
		public WorkflowExecutionFormFilter build() {
			return new WorkflowExecutionFormFilter(this);
		}
	}
	
}