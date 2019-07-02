package com.uff.workflow.monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class WorkflowMonitorApplication extends SpringBootServletInitializer {
	
	@Configuration
	@Profile("default")
	@PropertySource("classpath:application.properties")
	static class DefaultEnv {}
	
	@Configuration
	@Profile("prod")
	@PropertySource({"classpath:application.properties", "classpath:application-prod.properties"})
	static class ProdEnv {}

    public static void main(String[] args) {
        SpringApplication.run(WorkflowMonitorApplication.class, args);
    }
    
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(WorkflowMonitorApplication.class);
    }

}