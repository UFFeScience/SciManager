package com.uff.scimanager.component;

import java.util.ArrayList;
import java.util.List;

import com.uff.scimanager.domain.dto.filter.FilterFieldDTO;
import com.uff.scimanager.domain.dto.filter.FilterDTO;

public class PaginationInfo {
	
	private Integer pageSize;
	private Integer totalEntities;
	private Integer actualPageNumber;
	private Integer firstNumberPage;
	private Integer lastNumberPage;
	private Integer previousNumberPage;
	private Integer nextNumberPage;
	private String baseUrlLink;
	private List<Integer> pageNumbers;
	
	public PaginationInfo(PaginationInfoBuilder paginationInfoBuilder) {
		firstNumberPage = 1;
		
		this.totalEntities = paginationInfoBuilder.totalEntities;
		this.actualPageNumber = paginationInfoBuilder.actualPageNumber;
		this.pageSize = paginationInfoBuilder.pageSize;
		this.lastNumberPage = calculateLastNumberPage();
		this.baseUrlLink = paginationInfoBuilder.baseUrlLink;
		
		if (lastNumberPage != null && actualPageNumber != null && totalEntities != null) {
			buildPageNumbers();
			calculateNextAndPreviousNumbers();
		}
	}

	private void calculateNextAndPreviousNumbers() {
		calculateNextNumber();
		calculatePreviousNumber();
	}

	private void calculatePreviousNumber() {
		if ((actualPageNumber - 1) <= 0) {
			previousNumberPage = firstNumberPage;
			return;
		}
		
		previousNumberPage = actualPageNumber - 1;
	}

	private void calculateNextNumber() {
		if ((actualPageNumber + 1) > lastNumberPage) {
			nextNumberPage = lastNumberPage;
			return;
		}
		
		nextNumberPage = actualPageNumber + 1;
	}

	private Integer calculateLastNumberPage() {
		if (totalEntities == null || pageSize == null) {
			return 0;
		}
		
		Double plainDivision = Math.ceil(Double.valueOf(totalEntities) / pageSize);
		return plainDivision.equals(Double.valueOf(0)) ? 1 :plainDivision.intValue();
	}

	private void buildPageNumbers() {
		pageNumbers = new ArrayList<Integer>();
		
		Integer firstPaginationNumber = getFirstPaginationNumber();
		Integer lastPaginationNumber = getLastPaginationNumber();
		
		for (Integer i = firstPaginationNumber; i <= lastPaginationNumber; i++) {
			pageNumbers.add(i);
		}
	}
	
	private Integer getFirstPaginationNumber() {
		if ((actualPageNumber - 3) <= 0) {
			return firstNumberPage;
		}
		
		return (actualPageNumber - 3);
	}
	
	private Integer getLastPaginationNumber() {
		Integer auxiliarNumber = (actualPageNumber + 3);
		
		if (auxiliarNumber > lastNumberPage) {
			return lastNumberPage;
		}

		return auxiliarNumber;
	}
	
	public String buildParametersFromFilter(FilterDTO filter) {
		if (filter != null) {
			for (FilterFieldDTO field : filter.getFilterFields()) {
				addUrlParameter(field.getName(), field.getValue());
			}
		}
		
		return baseUrlLink;
	}
	
	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getTotalEntities() {
		return totalEntities;
	}

	public void setTotalEntities(Integer totalEntities) {
		this.totalEntities = totalEntities;
	}

	public Integer getActualPageNumber() {
		return actualPageNumber;
	}

	public void setActualPageNumber(Integer actualPageNumber) {
		this.actualPageNumber = actualPageNumber;
	}

	public String getBaseUrlLink() {
		return baseUrlLink;
	}

	public void setBaseUrlLink(String baseUrlLink) {
		this.baseUrlLink = baseUrlLink;
	}

	public List<Integer> getPageNumbers() {
		return pageNumbers;
	}

	public void setPageNumbers(List<Integer> pageNumbers) {
		this.pageNumbers = pageNumbers;
	}

	public Integer getLastNumberPage() {
		return lastNumberPage;
	}

	public void setLastNumberPage(Integer lastNumberPage) {
		this.lastNumberPage = lastNumberPage;
	}

	public Integer getFirstNumberPage() {
		return firstNumberPage;
	}

	public void setFirstNumberPage(Integer firstNumberPage) {
		this.firstNumberPage = firstNumberPage;
	}

	public Integer getPreviousNumberPage() {
		return previousNumberPage;
	}

	public void setPreviousNumberPage(Integer previousNumberPage) {
		this.previousNumberPage = previousNumberPage;
	}

	public Integer getNextNumberPage() {
		return nextNumberPage;
	}

	public void setNextNumberPage(Integer nextNumberPage) {
		this.nextNumberPage = nextNumberPage;
	}

	public void addUrlParameter(String parameterName, Object parameterValue) {
		if (parameterValue != null) {
			String parametervalueString = String.valueOf(parameterValue);
			
			if (!baseUrlLink.contains("?")) {
				baseUrlLink = baseUrlLink.concat("?" + parameterName + "=" + parametervalueString);
				return;
			}
			
			baseUrlLink = baseUrlLink.concat("&" + parameterName + "=" + parametervalueString);
		}
	}
	
	public static PaginationInfoBuilder builder() {
		return new PaginationInfoBuilder();
	}
	
	public static class PaginationInfoBuilder {
		
		private Integer pageSize;
		private Integer totalEntities;
		private Integer actualPageNumber;
		private String baseUrlLink;
		
		public PaginationInfoBuilder pageSize(Integer pageSize) {
			this.pageSize = pageSize;
			return this;
		}
		
		public PaginationInfoBuilder totalEntities(Integer totalEntities) {
			this.totalEntities = totalEntities;
			return this;
		}
		
		public PaginationInfoBuilder actualPageNumber(Integer actualPageNumber) {
			this.actualPageNumber = actualPageNumber;
			return this;
		}
		
		public PaginationInfoBuilder baseUrlLink(String baseUrlLink) {
			this.baseUrlLink = baseUrlLink;
			return this;
		}
		
		public PaginationInfo build() {
			return new PaginationInfo(this);
		}
	}

}