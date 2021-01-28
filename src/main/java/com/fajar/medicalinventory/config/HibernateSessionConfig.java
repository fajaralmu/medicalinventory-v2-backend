package com.fajar.medicalinventory.config;

import java.lang.reflect.Type;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.fajar.medicalinventory.service.config.WebConfigService;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class HibernateSessionConfig {
	private static SessionFactory factory;

	@Autowired
	private DriverManagerDataSource driverManagerDataSource;
	@Autowired
	private EntityManagerFactory entityManagerFactoryBean;
//	@Autowired
	private WebConfigService webConfigService;
	@Autowired
	private ApplicationContext applicationContext;

	@Bean
	public SessionFactory generateSession() {
		webConfigService = (WebConfigService) applicationContext.getBean("webAppConfig");
		try {
			org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();

			configuration.setProperties(additionalProperties());

			addAnnotatedClass(configuration);

			factory = configuration./* setInterceptor(new HibernateInterceptor()). */buildSessionFactory();
			log.info("Session Factory has been initialized");
			return factory;
		} catch (Exception ex) {

			System.err.println("Failed to create sessionFactory object." + ex);
			throw new ExceptionInInitializerError(ex);
		}

	}

	private void addAnnotatedClass(org.hibernate.cfg.Configuration configuration) {

		List<Type> entities = webConfigService.getEntityClassess();
		for (Type type : entities) {
			log.info("@@@@ addAnnotatedClass: {}", type);
			configuration.addAnnotatedClass((Class) type);
		}

	}

	private Properties additionalProperties() {

		String dialect = entityManagerFactoryBean.getProperties().get("hibernate.dialect").toString();
		String ddlAuto = entityManagerFactoryBean.getProperties().get("hibernate.hbm2ddl.auto").toString();
		String use_jdbc_metadata_defaults = entityManagerFactoryBean.getProperties().get("hibernate.temp.use_jdbc_metadata_defaults").toString();
		Class<? extends Driver> driverClass = com.mysql.jdbc.Driver.class;
		try {
			String connectionUrl =(driverManagerDataSource.getConnection().getMetaData().getURL());
			log.info("CONNECTION URL: {}", connectionUrl);
			driverClass = DriverManager.getDriver(connectionUrl).getClass();
			log.info("DRIVER CLASSNAME: {}", driverClass);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		printProps(entityManagerFactoryBean.getProperties(), "entityManagerFactoryBean");
//		printProps(driverManagerDataSource.getConnectionProperties(), "driverManagerDataSource");
		
		Properties properties = new Properties();
		properties.setProperty("hibernate.dialect", dialect);
		properties.setProperty("hibernate.connection.url", driverManagerDataSource.getUrl());
		properties.setProperty("hibernate.connection.username", driverManagerDataSource.getUsername());
		properties.setProperty("hibernate.connection.password", driverManagerDataSource.getPassword());

		properties.setProperty("hibernate.connection.driver_class", driverClass.getCanonicalName());
		properties.setProperty("hibernate.current_session_context_class", "thread");
		properties.setProperty("hibernate.show_sql", "true");
		properties.setProperty("hibernate.connection.pool_size", "1");
		properties.setProperty("hibernate.temp.use_jdbc_metadata_defaults",use_jdbc_metadata_defaults);
		properties.setProperty("hbm2ddl.auto", ddlAuto);
		return properties;
	}

	private void printProps(Map props, String name) {
		if (null != props) {
			for (Object key : props.keySet()) {
				log.info("{} PROPERTY: {}-> {}", name, key, props.get(key));
			}
		} else {
			log.info("00 PROPS IS NULL");
		}
	}

	@Bean
	public Session hibernateSession(SessionFactory sessionFactory) {

		return sessionFactory.openSession();
	}

}
