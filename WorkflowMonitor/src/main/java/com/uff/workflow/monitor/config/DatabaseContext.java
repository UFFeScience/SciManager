package com.uff.workflow.monitor.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class DatabaseContext {

    @Autowired
    private Environment env;

    @SuppressWarnings("serial")
    @Primary
	@Bean(name = "sciManager") 
    public LocalSessionFactoryBean sessionFactorySciManager() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        
        sessionFactory.setDataSource(dataSourceSciManager());
        sessionFactory.setPackagesToScan(new String[] { "com.uff.workflow.monitor" });
        sessionFactory.setHibernateProperties(new Properties() {
            {
            	setProperty("hibernate.default_schema", env.getProperty("jdbc.scimanager.schema"));
                setProperty("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
                setProperty("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
                setProperty("hibernate.format_sql", env.getProperty("hibernate.format_sql"));
                setProperty("hibernate.generate_statistics", env.getProperty("hibernate.generate_statistics"));
            }
        });

        return sessionFactory;
    }
    
    @Primary
    @Bean(name = "sciManagerDataSource") 
    public DataSource dataSourceSciManager() {
        BasicDataSource dataSource = new BasicDataSource();
        
        dataSource.setDriverClassName(env.getProperty("jdbc.driverClassName"));
        dataSource.setUrl(env.getProperty("jdbc.scimanager.url"));
        dataSource.setUsername(env.getProperty("jdbc.scimanager.username"));
        dataSource.setPassword(env.getProperty("jdbc.scimanager.password"));
        
        return dataSource;
    }
    
    @SuppressWarnings("serial")
	@Bean(name = "sciCumulus") 
    public LocalSessionFactoryBean sessionFactorySciCumulus() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        
        sessionFactory.setDataSource(dataSourceSciCumulus());
        sessionFactory.setPackagesToScan(new String[] { "com.uff.workflow.monitor.domain.swfms.scicumulus" });
        sessionFactory.setHibernateProperties(new Properties() {
            {
            	setProperty("hibernate.default_schema", env.getProperty("jdbc.scicumulus.schema"));
                setProperty("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
                setProperty("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
                setProperty("hibernate.format_sql", env.getProperty("hibernate.format_sql"));
                setProperty("hibernate.generate_statistics", env.getProperty("hibernate.generate_statistics"));
            }
        });

        return sessionFactory;
    }

    @Bean(name = "sciCumulusDataSource") 
    public DataSource dataSourceSciCumulus() {
        BasicDataSource dataSource = new BasicDataSource();
        
        dataSource.setDriverClassName(env.getProperty("jdbc.driverClassName"));
        dataSource.setUrl(env.getProperty("jdbc.scicumulus.url"));
        dataSource.setUsername(env.getProperty("jdbc.scicumulus.username"));
        dataSource.setPassword(env.getProperty("jdbc.scicumulus.password"));
        
        return dataSource;
    }

    @Bean
    @Autowired
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        HibernateTransactionManager txManager = new HibernateTransactionManager();
        txManager.setSessionFactory(sessionFactory);
        
        return txManager;
    }

}