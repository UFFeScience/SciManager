
package com.uff.scimanager.controller.advice;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;
import org.thymeleaf.exceptions.TemplateInputException;

@ControllerAdvice
public class ExceptionHandlerControllerAdvice {
	
	private static Logger log = LoggerFactory.getLogger(ExceptionHandlerControllerAdvice.class);

    @ExceptionHandler({NoHandlerFoundException.class, NoSuchRequestHandlingMethodException.class, EntityNotFoundException.class})
    public String notFound(Exception exception) {
        return "error/page-404";
    }

    @ExceptionHandler({Exception.class, TemplateInputException.class})
    public String exception(HttpServletRequest req, Exception exception) {
        log.error(exception.getMessage(), exception);
        return "error/page-500";
    }
    
    @ExceptionHandler({AccessDeniedException.class})
    public String accessDenied(HttpServletRequest req, Exception exception) {
        log.error(exception.getMessage(), exception);
        return "error/page-403";
    }
    
}