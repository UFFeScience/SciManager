package com.uff.scimanager.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uff.scimanager.component.ResponseMessage;

public class ResponseMessageUtil {
	
	private static final Logger log = LoggerFactory.getLogger(ResponseMessageUtil.class);
	
	public static String formatErrorMessage(BindingResult bindingResult) {
		StringBuilder errorMessage = new StringBuilder();
		
		for (Object object : bindingResult.getAllErrors()) {
		    if (object instanceof FieldError) {
		        FieldError fieldError = (FieldError) object;
		        
		        errorMessage.append(fieldError.getDefaultMessage() + "\n");
		        continue;
		    }

		    if (object instanceof ObjectError) {
		        ObjectError objectError = (ObjectError) object;
		        
		        errorMessage.append(objectError.getDefaultMessage() + "\n");
		        continue;
		    }
		}
		
		return errorMessage.toString();
	}
	
	public static void handleRedirectMessage(RedirectAttributes redirectAttributes, Object entity, String succesMessage, String errorMessage) {
		if (entity == null) {
			log.info("Redirecionando mensagem de erro");
        	redirectAttributes.addFlashAttribute(ResponseMessage.ERROR_MESSAGE_LABEL, ResponseMessage.buildErrorMessage(errorMessage));
        	return;
        }
		
		log.info("Redirecionando mensagem de sucesso");
		redirectAttributes.addFlashAttribute(ResponseMessage.SUCCESS_MESSAGE_LABEL, ResponseMessage.buildSuccessMessage(succesMessage));
	}
	
	public static ResponseMessage handleResponseMessage(Object entity, String succesMessage, String errorMessage) {
		if (entity == null) {
			log.error("Erro inesperado ao processar a ação");
        	return ResponseMessage.buildErrorMessage(errorMessage);
        }
		
		log.info("Ação realizada com sucesso");
		return ResponseMessage.buildSuccessMessage(succesMessage);
	}
	
}