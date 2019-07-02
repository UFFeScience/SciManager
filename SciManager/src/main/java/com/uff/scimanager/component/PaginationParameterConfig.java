package com.uff.scimanager.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PaginationParameterConfig {

	@Value("${pagination.default.order}")
	private String order;
	
	@Value("${pagination.first.result}")
	private Integer firstResult;
	
	@Value("${pagination.size}")
	private Integer maxResultsPerPage;
	
	@Value("${pagination.tasks.size}")
	private Integer maxTasksResultsPerPage;
	
	@Value("${pagination.filter.size}")
	private Integer maxFilterResults;
	
	@Value("${pagination.execution.metadata.size}")
	private Integer maxMetadataResultsPerPage;
	
	public enum QueryOrder {
		asc, desc
	}
	
	public Integer calculateActualPageFirstResultIndex(Integer pageNumber) {
		return (pageNumber * maxResultsPerPage) - maxResultsPerPage;
	}
	
	public static Integer calculateFirstResultIndex(Integer pageNumber, Integer pageSize) {
		return (pageNumber * pageSize) - pageSize;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public Integer getFirstResult() {
		return firstResult;
	}

	public void setFirstResult(Integer firstResult) {
		this.firstResult = firstResult;
	}

	public Integer getMaxResultsPerPage() {
		return maxResultsPerPage;
	}

	public void setMaxResultsPerPage(Integer maxResultsPerPage) {
		this.maxResultsPerPage = maxResultsPerPage;
	}

	public Integer getMaxTasksResultsPerPage() {
		return maxTasksResultsPerPage;
	}

	public void setMaxTasksResultsPerPage(Integer maxTasksResultsPerPage) {
		this.maxTasksResultsPerPage = maxTasksResultsPerPage;
	}
	
	public Integer getMaxMetadataResultsPerPage() {
		return maxMetadataResultsPerPage;
	}

	public void setMaxMetadataResultsPerPage(Integer maxMetadataResultsPerPage) {
		this.maxMetadataResultsPerPage = maxMetadataResultsPerPage;
	}

	public Integer getMaxFilterResults() {
		return maxFilterResults;
	}

	public void setMaxFilterResults(Integer maxFilterResults) {
		this.maxFilterResults = maxFilterResults;
	}
	
}