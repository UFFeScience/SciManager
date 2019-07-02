package com.uff.scimanager.domain.dto.filter;

import java.util.ArrayList;
import java.util.List;

public class FilterDTO {
	
	private List<FilterFieldDTO> filterFields;
	private Boolean hasTextFilter = Boolean.FALSE;
	
	public FilterDTO() {}
	
	public FilterDTO(FilterDTOBuilder filterDTOBuilder) {
		this.filterFields = filterDTOBuilder.filterFields;
		this.hasTextFilter = filterDTOBuilder.hasTextFilter;
	}
	
	public FilterDTO addFilterField(FilterFieldDTO filterField) {
		if (filterField != null && filterField.getValue() != null) {
			getFilterFields().add(filterField);
			
			if (filterField.getIsTextField()) {
				hasTextFilter = Boolean.TRUE;
			}
		}
		
		return this;
	}
	
	public Object getFieldValue(String fieldName) {
		for (FilterFieldDTO filterField : getFilterFields()) {
			if (filterField.getName().equals(fieldName)) {
				return filterField.getValue();
			}
		}
		
		return null;
	}
	
	public List<FilterFieldDTO> getFilterFields() {
		if (filterFields == null) {
			filterFields = new ArrayList<FilterFieldDTO>();
		}
		
		return filterFields;
	}

	public void setFilterFields(List<FilterFieldDTO> filterFields) {
		this.filterFields = filterFields;
	}

	public Boolean getHasTextFilter() {
		return hasTextFilter;
	}

	public void setHasTextFilter(Boolean hasTextFilter) {
		this.hasTextFilter = hasTextFilter;
	}

	public static FilterDTOBuilder builder() {
		return new FilterDTOBuilder();
	}
	
	public static class FilterDTOBuilder {
		
		private List<FilterFieldDTO> filterFields;
		private Boolean hasTextFilter = Boolean.FALSE;
		
		public FilterDTOBuilder filterFields(List<FilterFieldDTO> filterFields) {
			this.filterFields = filterFields;
			return this;
		}
		
		public FilterDTOBuilder hasTextFilter(Boolean hasTextFilter) {
			this.hasTextFilter = hasTextFilter;
			return this;
		}

		public FilterDTO build() {
			return new FilterDTO(this);
		}
	}
	
}