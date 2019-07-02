package com.uff.scimanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(exclude={org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration.class})
public class SciManagerApplication extends SpringBootServletInitializer {
	
	@Configuration
	@Profile("default")
	@PropertySource("classpath:application.properties")
	static class DefaultEnv {}
	
	@Configuration
	@Profile("prod")
	@PropertySource({"classpath:application.properties", "classpath:application-prod.properties"})
	static class ProdEnv {}
	
    public static void main(String[] args) {
        SpringApplication.run(SciManagerApplication.class, args);
    }
    
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SciManagerApplication.class);
    }

}