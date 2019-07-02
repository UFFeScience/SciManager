package com.uff.scimanager.domain;

public class Field {
	
	private String name;
	private Object oldValue;
	private Object updatedValue;
	
	public Field() {}
	
	public Field(FieldBuilder fieldBuilder) {
		this.name = fieldBuilder.name;
		this.oldValue = fieldBuilder.oldValue;
		this.updatedValue = fieldBuilder.updatedValue;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Object getOldValue() {
		return oldValue;
	}
	
	public void setOldValue(Object oldValue) {
		this.oldValue = oldValue;
	}
	
	public Object getUpdatedValue() {
		return updatedValue;
	}
	
	public void setUpdatedValue(Object updatedValue) {
		this.updatedValue = updatedValue;
	}
	
	public static FieldBuilder builder() {
		return new FieldBuilder();
	}
	
	public static class FieldBuilder {
		
		private String name;
		private Object oldValue;
		private Object updatedValue;
		
		public FieldBuilder name(String name) {
			this.name = name;
			return this;
		}
		
		public FieldBuilder oldValue(Object oldValue) {
			this.oldValue = oldValue;
			return this;
		}
		
		public FieldBuilder updatedValue(Object updatedValue) {
			this.updatedValue = updatedValue;
			return this;
		}
		
		public Field build() {
			return new Field(this);
		}
	}
	
}