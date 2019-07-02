package com.uff.system.notifier.domain;

import java.util.List;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class MessageEmitter {
	
	private Long userId;
	private String channel;
	private SseEmitter sseEmitter;
	
	public MessageEmitter() {}

	public MessageEmitter(MessageEmitterBuilder messageEmitterBuilder) {
		this.userId = messageEmitterBuilder.userId;
		this.channel = messageEmitterBuilder.channel;
		this.sseEmitter = messageEmitterBuilder.sseEmitter;
	}
	
	public static void unsubscribeExistingSse(List<MessageEmitter> emitters, String channel, Long userId) {
		MessageEmitter emitterFound = getSseEmitterActive(emitters, channel, userId);
    	
    	if (emitterFound != null) {
    		emitterFound.getSseEmitter().complete();
    		emitters.remove(emitterFound);
    	}
	}
	
	public static MessageEmitter getSseEmitterActive(List<MessageEmitter> emitters, String channel, Long userId) {
		for (MessageEmitter messageEmitter : emitters) {
        	if (messageEmitter.getChannel().equals(channel) && messageEmitter.getUserId().equals(userId)) {
        		return messageEmitter;
        	}
        }
		
		return null;
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

	public SseEmitter getSseEmitter() {
		return sseEmitter;
	}

	public void setSseEmitter(SseEmitter sseEmitter) {
		this.sseEmitter = sseEmitter;
	}
	
	public static MessageEmitterBuilder builder() {
		return new MessageEmitterBuilder();
	}
	
	public static class MessageEmitterBuilder {
		
		private Long userId;
		private String channel;
		private SseEmitter sseEmitter;
		
		public MessageEmitterBuilder userId(Long userId) {
			this.userId = userId;
			return this;
		}
		
		public MessageEmitterBuilder channel(String channel) {
			this.channel = channel;
			return this;
		}
		
		public MessageEmitterBuilder sseEmitter(SseEmitter sseEmitter) {
			this.sseEmitter = sseEmitter;
			return this;
		}
		
		
		public MessageEmitter build() {
			return new MessageEmitter(this);
		}
	}
	
}