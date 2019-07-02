package com.uff.scimanager.component;

public class ResponseMessage {
	
	public static final String ERROR_MESSAGE_LABEL = "errorMessage";
	public static final String SUCCESS_MESSAGE_LABEL = "successMessage";
	
	private Integer statusCode;
	private String type;
	private String text;
	
	public ResponseMessage() {}
	
	public ResponseMessage(ResponseMessageBuilder responseMessageBuilder) {
		this.type = responseMessageBuilder.type;
		this.text = responseMessageBuilder.text;
		this.statusCode = responseMessageBuilder.statusCode;
	}
	
	public static ResponseMessageBuilder builder() {
		return new ResponseMessageBuilder();
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public Integer getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

	public static ResponseMessage buildSuccessMessage(String successMessage) {
		if ("".equals(successMessage) || successMessage == null) {
			return null;
		}
		
		return ResponseMessage.builder()
							  .type(SUCCESS_MESSAGE_LABEL)
							  .text(successMessage).build();
	}
	
	public static ResponseMessage buildErrorMessage(String errorMessage) {
		if ("".equals(errorMessage) || errorMessage == null) {
			return null;
		}
		
		return ResponseMessage.builder()
							  .type(ERROR_MESSAGE_LABEL)
						      .text(errorMessage).build();
	}
	
	public static class ResponseMessageBuilder {
		
		private String type;
		private String text;
		private Integer statusCode;
		
		public ResponseMessageBuilder type(String type) {
			this.type = type;
			return this;
		}
		
		public ResponseMessageBuilder text(String text) {
			this.text = text;
			return this;
		}
		
		public ResponseMessageBuilder statusCode(Integer statusCode) {
			this.statusCode = statusCode;
			return this;
		}
		
		public ResponseMessage build() {
			return new ResponseMessage(this);
		}
	}

}