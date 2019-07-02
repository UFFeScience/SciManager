package com.uff.system.notifier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@EnableAutoConfiguration
@SpringBootApplication
public class SystemNotifierApplication extends SpringBootServletInitializer {
	
	@Configuration
	@Profile("default")
	@PropertySource("classpath:application.properties")
	static class DefaultEnv {}
	
	@Configuration
	@Profile("prod")
	@PropertySource({"classpath:application.properties", "classpath:application-prod.properties"})
	static class ProdEnv {}

    public static void main(String[] args) {
        SpringApplication.run(SystemNotifierApplication.class, args);
    }
    
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SystemNotifierApplication.class);
    }

}