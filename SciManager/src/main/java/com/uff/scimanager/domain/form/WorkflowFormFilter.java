package com.uff.scimanager.domain.form;

public class WorkflowFormFilter {
	
	private Integer pageNumber = 1;
	private String queryString;
	private Long scientificExperimentId;
	private Long scientificProjectId;
	private String responsibleGroupName;
	private Boolean myOwn;
	private Long userId;
	
	public WorkflowFormFilter() {}
	
	public WorkflowFormFilter(WorkflowFormFilterBuilder workflowFormFilterBuilder) {
		this.pageNumber = workflowFormFilterBuilder.pageNumber;
		this.queryString = workflowFormFilterBuilder.queryString;
		this.scientificExperimentId = workflowFormFilterBuilder.scientificExperimentId;
		this.scientificProjectId = workflowFormFilterBuilder.scientificProjectId;
		this.responsibleGroupName = workflowFormFilterBuilder.responsibleGroupName;
		this.myOwn = workflowFormFilterBuilder.myOwn;
		this.userId = workflowFormFilterBuilder.userId;
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
	
	public Long getScientificExperimentId() {
		return scientificExperimentId;
	}

	public void setScientificExperimentId(Long scientificExperimentId) {
		this.scientificExperimentId = scientificExperimentId;
	}
	
	public Long getScientificProjectId() {
		return scientificProjectId;
	}

	public void setScientificProjectId(Long scientificProjectId) {
		this.scientificProjectId = scientificProjectId;
	}

	public String getResponsibleGroupName() {
		return responsibleGroupName;
	}
	
	public void setResponsibleGroupName(String responsibleGroupName) {
		this.responsibleGroupName = responsibleGroupName;
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
	
	public static WorkflowFormFilterBuilder builder() {
		return new WorkflowFormFilterBuilder();
	}
	
	public static class WorkflowFormFilterBuilder {
		
		private Integer pageNumber = 1;
		private String queryString;
		private Long scientificExperimentId;
		private Long scientificProjectId;
		private String responsibleGroupName;
		private Boolean myOwn;
		private Long userId;
		
		public WorkflowFormFilterBuilder pageNumber(Integer pageNumber) {
			this.pageNumber = pageNumber;
			return this;
		}
		
		public WorkflowFormFilterBuilder queryString(String queryString) {
			this.queryString = queryString;
			return this;
		}
		
		public WorkflowFormFilterBuilder scientificExperimentId(Long scientificExperimentId) {
			this.scientificExperimentId = scientificExperimentId;
			return this;
		}
		
		public WorkflowFormFilterBuilder scientificProjectId(Long scientificProjectId) {
			this.scientificProjectId = scientificProjectId;
			return this;
		}
		
		public WorkflowFormFilterBuilder responsibleGroupName(String responsibleGroupName) {
			this.responsibleGroupName = responsibleGroupName;
			return this;
		}
		
		public WorkflowFormFilterBuilder userId(Long userId) {
			this.userId = userId;
			return this;
		}
		
		public WorkflowFormFilterBuilder myOwn(Boolean myOwn) {
			this.myOwn = myOwn;
			return this;
		}
		
		public WorkflowFormFilter build() {
			return new WorkflowFormFilter(this);
		}
	}
	
}	
