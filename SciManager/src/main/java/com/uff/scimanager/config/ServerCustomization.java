package com.uff.scimanager.config;

import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
public class ServerCustomization {
	
	@Bean
	public EmbeddedServletContainerCustomizer containerCustomizer() {
	 
	    return new EmbeddedServletContainerCustomizer() {
	        
	    	@Override
	        public void customize(ConfigurableEmbeddedServletContainer container) {

	    		ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/error/404");
	            ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error/500");
	            
	            container.addErrorPages(error404Page, error500Page); 
	        }
	    	
	    };
	}
	
}