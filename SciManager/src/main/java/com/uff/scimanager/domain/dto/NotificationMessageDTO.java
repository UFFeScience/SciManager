package com.uff.scimanager.domain.dto;

import java.util.ArrayList;
import java.util.List;

import com.uff.scimanager.domain.NotificationMessage;
import com.uff.scimanager.util.CalendarDateUtils;

public class NotificationMessageDTO {
	
	private String notificationMessageId;
	private String messageTitle;
	private String messageBody;
	private String messageLink;
	private String actionDate;
	private Long userAgentId;
	private Boolean visualized = Boolean.FALSE;
	
	public NotificationMessageDTO() {}

	public NotificationMessageDTO(NotificationMessageBuilder notificationMessageBuilder) {
		this.notificationMessageId = notificationMessageBuilder.notificationMessageId;
		this.messageTitle = notificationMessageBuilder.messageTitle;
		this.messageBody = notificationMessageBuilder.messageBody;
		this.messageLink = notificationMessageBuilder.messageLink;
		this.actionDate = notificationMessageBuilder.actionDate;
		this.userAgentId = notificationMessageBuilder.userAgentId;
		this.visualized = notificationMessageBuilder.visualized;
	}
	
	private static NotificationMessageDTO buildEmptyNotificationMessageDTO() {
		return new NotificationMessageDTO();
	}
	
	public static NotificationMessageDTO buildByEntity(NotificationMessage notificationMessage) {
		if (notificationMessage == null) {
			return buildEmptyNotificationMessageDTO();
		}
		
		return NotificationMessageDTO.builder()
								    .notificationMessageId(notificationMessage.getNotificationMessageId())
								    .messageTitle(notificationMessage.getMessageTitle())
								    .messageBody(notificationMessage.getMessageBody())
								    .messageLink(notificationMessage.getMessageLink())
								    .actionDate(CalendarDateUtils.formatDate(notificationMessage.getActionDate()))
								    .userAgentId(notificationMessage.getUserAgentId())
								    .visualized(notificationMessage.getVisualized()).build();
	}
	
	public static List<NotificationMessageDTO> convertEntityListToDTOList(List<NotificationMessage> notificationMessages) {
		List<NotificationMessageDTO> notificationMessagesDTO = new ArrayList<NotificationMessageDTO>();
		
		if (notificationMessages != null) {
			for (NotificationMessage notificationMessage : notificationMessages) {
				notificationMessagesDTO.add(NotificationMessageDTO.buildByEntity(notificationMessage));
			}
		}
		
		return notificationMessagesDTO;
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

	public String getActionDate() {
		return actionDate;
	}

	public void setActionDate(String actionDate) {
		this.actionDate = actionDate;
	}
	
	public Long getUserAgentId() {
		return userAgentId;
	}

	public void setUserAgentId(Long userAgentId) {
		this.userAgentId = userAgentId;
	}

	public Boolean getVisualized() {
		return visualized;
	}

	public void setVisualized(Boolean visualized) {
		this.visualized = visualized;
	}

	public static NotificationMessageBuilder builder() {
		return new NotificationMessageBuilder();
	}
	
	public static class NotificationMessageBuilder {
		
		private String notificationMessageId;
		private String messageTitle;
		private String messageBody;
		private String messageLink;
		private String actionDate;
		private Long userAgentId;
		private Boolean visualized = Boolean.FALSE;
		
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
		
		public NotificationMessageBuilder actionDate(String actionDate) {
			this.actionDate = actionDate;
			return this;
		}
		
		public NotificationMessageBuilder userAgentId(Long userAgentId) {
			this.userAgentId = userAgentId;
			return this;
		}
		
		public NotificationMessageBuilder visualized(Boolean visualized) {
			this.visualized = visualized;
			return this;
		}
		
		public NotificationMessageDTO build() {
			return new NotificationMessageDTO(this);
		}
	}
	
}