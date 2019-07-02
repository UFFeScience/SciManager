package com.uff.system.notification.processor.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class DatabaseContext {

    @Autowired
    private Environment env;
    
    @Bean
    @SuppressWarnings("serial")
    public LocalSessionFactoryBean sessionFactorySciManager() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan(new String[] { "com.uff.system.notification.processor" });
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
    
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        
        dataSource.setDriverClassName(env.getProperty("jdbc.driverClassName"));
        dataSource.setUrl(env.getProperty("jdbc.scimanager.url"));
        dataSource.setUsername(env.getProperty("jdbc.scimanager.username"));
        dataSource.setPassword(env.getProperty("jdbc.scimanager.password"));
        
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