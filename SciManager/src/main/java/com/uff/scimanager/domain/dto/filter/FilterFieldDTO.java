package com.uff.scimanager.domain.dto.filter;

public class FilterFieldDTO {
	
	private String name;
	private String label;
	private Object value;
	private Object text;
	private Boolean isTextField = Boolean.FALSE;
	
	public FilterFieldDTO() {}
	
	public FilterFieldDTO(FilterFieldDTOBuilder filterFieldDTOBuilder) {
		this.name = filterFieldDTOBuilder.name;
		this.label = filterFieldDTOBuilder.label;
		this.value = filterFieldDTOBuilder.value;
		this.text = filterFieldDTOBuilder.text;
		this.isTextField = filterFieldDTOBuilder.isTextField;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
	public Object getText() {
		return text;
	}

	public void setText(Object text) {
		this.text = text;
	}

	public Boolean getIsTextField() {
		return isTextField;
	}

	public void setIsTextField(Boolean isTextField) {
		this.isTextField = isTextField;
	}

	public static FilterFieldDTOBuilder builder() {
		return new FilterFieldDTOBuilder();
	}
	
	public static class FilterFieldDTOBuilder {
		
		private String name;
		private String label;
		private Object value;
		private Object text;
		private Boolean isTextField = Boolean.FALSE;
		
		public FilterFieldDTOBuilder name(String name) {
			this.name = name;
			return this;
		}
		
		public FilterFieldDTOBuilder text(Object text) {
			this.text = text;
			return this;
		}
		
		public FilterFieldDTOBuilder label(String label) {
			this.label = label;
			return this;
		}
		
		public FilterFieldDTOBuilder value(Object value) {
			this.value = value;
			return this;
		}
		
		public FilterFieldDTOBuilder isTextField(Boolean isTextField) {
			this.isTextField = isTextField;
			return this;
		}
		
		public FilterFieldDTO build() {
			return new FilterFieldDTO(this);
		}
	}
	
}