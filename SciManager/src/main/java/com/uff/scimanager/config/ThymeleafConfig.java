package com.uff.scimanager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import com.uff.scimanager.util.EncrypterUtils;

@Configuration
@ConditionalOnClass({SpringTemplateEngine.class})
@EnableConfigurationProperties({ThymeleafProperties.class})
@AutoConfigureAfter({WebMvcAutoConfiguration.class})
public class ThymeleafConfig implements ApplicationContextAware {
	
	@Autowired
	private ApplicationContext applicationContext;
	
	public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Autowired
    private ThymeleafProperties properties;
    
    private ITemplateResolver templateResolver() {
    	SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        
    	resolver.setApplicationContext(applicationContext);
        resolver.setPrefix(this.properties.getPrefix());
        resolver.setSuffix(this.properties.getSuffix());
        resolver.setTemplateMode(this.properties.getMode());
        resolver.setCacheable(this.properties.isCache());
        
        return resolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine() {
    	SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templateResolver());
        return engine;
    }

    @Bean
    public ViewResolver viewResolver() {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        
        resolver.setOrder(2147483642);
        resolver.setTemplateEngine(templateEngine());
        resolver.setCharacterEncoding(EncrypterUtils.DEFAULT_ENCODING);
        
        return resolver;
    }

}