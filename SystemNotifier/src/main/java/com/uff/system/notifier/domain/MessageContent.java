package com.uff.system.notifier.domain;

public class MessageContent {
	
	private Long userId;
	private String channel;
	private Boolean reload;
	
	public MessageContent() {}

	public MessageContent(MessageContentBuilder messageContentBuilder) {
		this.userId = messageContentBuilder.userId;
		this.channel = messageContentBuilder.channel;
		this.reload = messageContentBuilder.reload;
	}
	
	public Long getUserId() {
		return userId;
	}
	
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public String getChannel() {
		return channel;
	}
	
	public void setChannel(String channel) {
		this.channel = channel;
	}

	public Boolean getReload() {
		return reload;
	}

	public void setReload(Boolean reload) {
		this.reload = reload;
	}
	
	public static MessageContentBuilder builder() {
		return new MessageContentBuilder();
	}
	
	public static class MessageContentBuilder {
		
		private Long userId;
		private String channel;
		private Boolean reload;
		
		public MessageContentBuilder userId(Long userId) {
			this.userId = userId;
			return this;
		}
		
		public MessageContentBuilder channel(String channel) {
			this.channel = channel;
			return this;
		}
		
		public MessageContentBuilder reload(Boolean reload) {
			this.reload = reload;
			return this;
		}
		
		
		public MessageContent build() {
			return new MessageContent(this);
		}
	}
	
}