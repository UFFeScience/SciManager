package com.uff.system.notification.processor.domain;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.uff.system.notification.processor.domain.dto.amqp.NotificationMessageDTO;

@Document(collection = "notificationMessage")
public class NotificationMessage {
	
	@Id
	private String notificationMessageId;
	private String messageTitle;
	private String messageBody;
	private String messageLink;
	private Date actionDate;
	private Date visualizedDate;
	private Boolean visualized = Boolean.FALSE;
	private Long userSubjectId;
	private Long userAgentId;
	
	public NotificationMessage() {}

	public NotificationMessage(NotificationMessageBuilder notificationMessageBuilder) {
		this.notificationMessageId = notificationMessageBuilder.notificationMessageId;
		this.messageTitle = notificationMessageBuilder.messageTitle;
		this.messageBody = notificationMessageBuilder.messageBody;
		this.messageLink = notificationMessageBuilder.messageLink;
		this.actionDate = notificationMessageBuilder.actionDate;
		this.visualizedDate = notificationMessageBuilder.visualizedDate;
		this.visualized = notificationMessageBuilder.visualized;
		this.userSubjectId = notificationMessageBuilder.userSubjectId;
		this.userAgentId = notificationMessageBuilder.userAgentId;
	}
	
	public static NotificationMessage buildByDTO(NotificationMessageDTO notificationMessageDTO) {
		if (notificationMessageDTO == null) {
			return null;
		}
		
		return NotificationMessage.builder()
								  .messageTitle(notificationMessageDTO.getMessageTitle())
								  .messageBody(notificationMessageDTO.getMessageBody())
								  .messageLink(notificationMessageDTO.getMessageLink())
								  .actionDate(notificationMessageDTO.getActionDate())
								  .userSubjectId(notificationMessageDTO.getUserSubjectId())
								  .userAgentId(notificationMessageDTO.getUserAgentId()).build();
	}
	
	public String getNotificationMessageId() {
		return notificationMessageId;
	}

	public void setNotificationMessageId(String notificationMessageId) {
		this.notificationMessageId = notificationMessageId;
	}

	public String getMessageTitle() {
		return messageTitle;
	}

	public void setMessageTitle(String messageTitle) {
		this.messageTitle = messageTitle;
	}

	public String getMessageBody() {
		return messageBody;
	}

	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}

	public String getMessageLink() {
		return messageLink;
	}

	public void setMessageLink(String messageLink) {
		this.messageLink = messageLink;
	}

	public Date getActionDate() {
		return actionDate;
	}

	public void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
	}

	public Date getVisualizedDate() {
		return visualizedDate;
	}

	public void setVisualizedDate(Date visualizedDate) {
		this.visualizedDate = visualizedDate;
	}

	public Boolean getVisualized() {
		return visualized;
	}

	public void setVisualized(Boolean visualized) {
		this.visualized = visualized;
	}

	public Long getUserSubjectId() {
		return userSubjectId;
	}

	public void setUserSubjectId(Long userSubjectId) {
		this.userSubjectId = userSubjectId;
	}
	
	public Long getUserAgentId() {
		return userAgentId;
	}

	public void setUserAgentId(Long userAgentId) {
		this.userAgentId = userAgentId;
	}

	public static NotificationMessageBuilder builder() {
		return new NotificationMessageBuilder();
	}
	
	public static class NotificationMessageBuilder {
		
		private String notificationMessageId;
		private String messageTitle;
		private String messageBody;
		private String messageLink;
		private Date actionDate;
		private Date visualizedDate;
		private Boolean visualized = Boolean.FALSE;
		private Long userSubjectId;
		private Long userAgentId;
		
		public NotificationMessageBuilder notificationMessageId(String notificationMessageId) {
			this.notificationMessageId = notificationMessageId;
			return this;
		}
		
		public NotificationMessageBuilder messageTitle(String messageTitle) {
			this.messageTitle = messageTitle;
			return this;
		}
		
		public NotificationMessageBuilder messageBody(String messageBody) {
			this.messageBody = messageBody;
			return this;
		}
		
		public NotificationMessageBuilder messageLink(String messageLink) {
			this.messageLink = messageLink;
			return this;
		}
		
		public NotificationMessageBuilder actionDate(Date actionDate) {
			this.actionDate = actionDate;
			return this;
		}
		
		public NotificationMessageBuilder visualizedDate(Date visualizedDate) {
			this.visualizedDate = visualizedDate;
			return this;
		}
		
		public NotificationMessageBuilder visualized(Boolean visualized) {
			this.visualized = visualized;
			return this;
		}
		
		public NotificationMessageBuilder userSubjectId(Long userSubjectId) {
			this.userSubjectId = userSubjectId;
			return this;
		}
		
		public NotificationMessageBuilder userAgentId(Long userAgentId) {
			this.userAgentId = userAgentId;
			return this;
		}
		
		public NotificationMessage build() {
			return new NotificationMessage(this);
		}
	}
	
}