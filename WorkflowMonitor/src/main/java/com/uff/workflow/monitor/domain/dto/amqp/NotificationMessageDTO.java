package com.uff.workflow.monitor.domain.dto.amqp;

import java.io.Serializable;
import java.util.Date;

import com.uff.workflow.monitor.domain.NotificationType;

public class NotificationMessageDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String messageTitle;
	private String messageBody;
	private String messageLink;
	private Date actionDate;
	private Long userGroupId;
	private Long userSubjectId;
	private Long userAgentId;
	private NotificationType notificationType;
	
	public NotificationMessageDTO() {}

	public NotificationMessageDTO(NotificationMessageDTOBuilder notificationMessageDTOBuilder) {
		this.messageTitle = notificationMessageDTOBuilder.messageTitle;
		this.messageBody = notificationMessageDTOBuilder.messageBody;
		this.userGroupId = notificationMessageDTOBuilder.userGroupId;
		this.messageLink = notificationMessageDTOBuilder.messageLink;
		this.actionDate = notificationMessageDTOBuilder.actionDate;
		this.userGroupId = notificationMessageDTOBuilder.userGroupId;
		this.userSubjectId = notificationMessageDTOBuilder.userSubjectId;
		this.userAgentId = notificationMessageDTOBuilder.userAgentId;
		this.notificationType = notificationMessageDTOBuilder.notificationType;
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

	public Long getUserGroupId() {
		return userGroupId;
	}

	public void setUserGroupId(Long userGroupId) {
		this.userGroupId = userGroupId;
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

	public NotificationType getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(NotificationType notificationType) {
		this.notificationType = notificationType;
	}

	public static NotificationMessageDTOBuilder builder() {
		return new NotificationMessageDTOBuilder();
	}
	
	public static class NotificationMessageDTOBuilder {
		
		private String messageTitle;
		private String messageBody;
		private String messageLink;
		private Date actionDate;
		private Long userGroupId;
		private Long userSubjectId;
		private Long userAgentId;
		private NotificationType notificationType;
		
		public NotificationMessageDTOBuilder messageTitle(String messageTitle) {
			this.messageTitle = messageTitle;
			return this;
		}
		
		public NotificationMessageDTOBuilder messageBody(String messageBody) {
			this.messageBody = messageBody;
			return this;
		}
		
		public NotificationMessageDTOBuilder messageLink(String messageLink) {
			this.messageLink = messageLink;
			return this;
		}
		
		public NotificationMessageDTOBuilder actionDate(Date actionDate) {
			this.actionDate = actionDate;
			return this;
		}
		
		public NotificationMessageDTOBuilder userGroupId(Long userGroupId) {
			this.userGroupId = userGroupId;
			return this;
		}
		
		public NotificationMessageDTOBuilder userSubjectId(Long userSubjectId) {
			this.userSubjectId = userSubjectId;
			return this;
		}
		
		public NotificationMessageDTOBuilder userAgentId(Long userAgentId) {
			this.userAgentId = userAgentId;
			return this;
		}
		
		public NotificationMessageDTOBuilder notificationType(NotificationType notificationType) {
			this.notificationType = notificationType;
			return this;
		}
		
		public NotificationMessageDTO build() {
			return new NotificationMessageDTO(this);
		}
	}
	
}