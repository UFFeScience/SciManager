package com.uff.scimanager.controller.advice;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.uff.scimanager.component.HeaderData;
import com.uff.scimanager.domain.security.CurrentUser;

@ControllerAdvice
public class HeaderControllerAdvice {
	
	private static final Logger log = LoggerFactory.getLogger(HeaderControllerAdvice.class);
	
	private final HeaderData headerData;
	
    @Autowired
	public HeaderControllerAdvice(HeaderData headerData) {
		this.headerData = headerData;
	}

    @ModelAttribute
    public void getHeaderAttributes(Model model, Authentication authentication, HttpServletRequest request) {
    	log.info("Recuperando o usuario logado no sistema");
    	
    	if (authentication == null) {
    		log.info("Usuário não logado no sistema.");
    		return;
    	}
    	
    	CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
    	headerData.setUpData(currentUser, request.getContextPath());
    	
    	model.addAttribute("headerData", headerData);
    }
    
}