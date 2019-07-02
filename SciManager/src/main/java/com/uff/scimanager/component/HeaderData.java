package com.uff.scimanager.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.uff.scimanager.domain.security.CurrentUser;

@Component
public class HeaderData {
	
	private CurrentUser currentUser;
	private String applicationContext;
	
	@Value("${notifer.system.url}")
	private String notifierUrl;
	
	public void setUpData(CurrentUser currentUser, String applicationContext) {
		this.currentUser = currentUser;
		this.applicationContext = applicationContext;
	}
	
	public CurrentUser getCurrentUser() {
		return currentUser;
	}
	
	public void setCurrentUser(CurrentUser currentUser) {
		this.currentUser = currentUser;
	}
	
	public String getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(String applicationContext) {
		this.applicationContext = applicationContext;
	}

	public String getNotifierUrl() {
		return notifierUrl;
	}

	public void setNotifierUrl(String notifierUrl) {
		this.notifierUrl = notifierUrl;
	}
	
}