package com.pkm.medicalinventory.config;

import java.lang.reflect.Type;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class DatabaseSessionConfig { 

	@Autowired
	@Qualifier("dataSource_main")
	private DriverManagerDataSource dataSource;
	@Autowired
	@Qualifier("entityManagerFactory_main")
	private EntityManagerFactory factory;
	
	@Autowired
	@Qualifier("dataSource_replica")
	private DriverManagerDataSource dataSource_replica;
	@Autowired
	@Qualifier("entityManagerFactory_replica")
	private EntityManagerFactory factory_replica;
	
	@Autowired
	private EntityRegistration entityReg;

	@Bean(name = "sessionFactory_main")
	public SessionFactory getSessionFactory() {
		try {
			org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();

			configuration.setProperties(additionalProperties(factory, dataSource));

			addAnnotatedClass(configuration);

			SessionFactory factory = configuration.
					/* setInterceptor(new HibernateInterceptor()). */
					buildSessionFactory();
			
			log.info("Session Factory has been initialized");
			return factory;
		} catch (Exception ex) {

			System.err.println("Failed to create sessionFactory object." + ex);
			throw new ExceptionInInitializerError(ex);
		}

	}
//	@Bean(name = "sessionFactory_replica")
//	public SessionFactory getSessionFactory_replica() {
//		try {
//			org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();
//			
//			configuration.setProperties(additionalProperties(entityManagerFactoryBean_replica, driverManagerDataSource_replica));
//			
//			addAnnotatedClass(configuration);
//			
//			SessionFactory factory = configuration.
//					/* setInterceptor(new HibernateInterceptor()). */
//					buildSessionFactory();
//			
//			log.info("Session Factory has been initialized");
//			return factory;
//		} catch (Exception ex) {
//			
//			System.err.println("Failed to create sessionFactory object." + ex);
//			throw new ExceptionInInitializerError(ex);
//		}
//		
//	}

	private void addAnnotatedClass(org.hibernate.cfg.Configuration configuration) {
		List<Type> entities = entityReg.getEntityClassess();
		for (Type type : entities) {
			log.info("@@@@ addAnnotatedClass: {}", type);
			configuration.addAnnotatedClass((Class) type);
		}

	}

	private static Properties additionalProperties(EntityManagerFactory fac, DriverManagerDataSource source) {

		String dialect = fac.getProperties().get("hibernate.dialect").toString();
		String showSql = fac.getProperties().get("hibernate.show_sql").toString();
		String ddlAuto = fac.getProperties().get("hibernate.hbm2ddl.auto").toString();
		String use_jdbc_metadata_defaults = fac.getProperties().get("hibernate.temp.use_jdbc_metadata_defaults").toString();
		Class<? extends Driver> driverClass = org.postgresql.Driver.class;// com.mysql.jdbc.Driver.class;
		try {
			String connectionUrl =(source.getConnection().getMetaData().getURL());
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
		properties.setProperty("hibernate.connection.url", source.getUrl());
		properties.setProperty("hibernate.connection.username", source.getUsername());
		properties.setProperty("hibernate.connection.password", source.getPassword());

		properties.setProperty("hibernate.connection.driver_class", driverClass.getCanonicalName());
		properties.setProperty("hibernate.current_session_context_class", "thread");
		properties.setProperty("hibernate.show_sql", showSql);
//		properties.setProperty("hibernate.connection.pool_size", "1");
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
