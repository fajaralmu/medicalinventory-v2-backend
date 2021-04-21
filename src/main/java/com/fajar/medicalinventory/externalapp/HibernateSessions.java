package com.fajar.medicalinventory.externalapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.persistence.Entity;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class HibernateSessions {
	public final static String inputDir = "D:\\Development\\Fajar\\medicalinventory\\src\\"
			+ "main\\java\\com\\fajar\\medicalinventory\\entity\\"; 
	 
	static Session setSession() {
		Session testSession;
		org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();
		configuration.setProperties(additionalPropertiesPostgres());

		List<Class<?>>  managedEntities = getManagedEntities();
		for (Class class1 : managedEntities) {
			configuration.addAnnotatedClass(class1);
		}

		SessionFactory factory = configuration./* setInterceptor(new HibernateInterceptor()). */buildSessionFactory();
		testSession = factory.openSession();
		return testSession;
	}

	static List<Class<?>> getManagedEntities() {
		List<Class<?>> returnClasses = new ArrayList<>();
		List<String> names = TypeScriptModelCreators.getJavaFiles(inputDir);
		List<Class> classes = TypeScriptModelCreators.getJavaClasses("com.fajar.medicalinventory.entity", names);
		for (Class class1 : classes) {
			if (null != class1.getAnnotation(Entity.class)) {
				returnClasses.add(class1);
			}
		}
		return returnClasses;
	}
 

	private static Properties additionalPropertiesPostgresOffline() {

		String dialect = "org.hibernate.dialect.PostgreSQLDialect";
		String ddlAuto = "update";

		Properties properties = new Properties();
		properties.setProperty("hibernate.dialect", dialect);
		properties.setProperty("hibernate.connection.url", "jdbc:postgresql://localhost:5432/universal_commerce");
		properties.setProperty("hibernate.connection.username", "postgres");
		properties.setProperty("hibernate.connection.password", "root");

		properties.setProperty("hibernate.connection.driver_class", org.postgresql.Driver.class.getCanonicalName());
		properties.setProperty("hibernate.current_session_context_class", "thread");
		properties.setProperty("hibernate.show_sql", "true");
		properties.setProperty("hibernate.connection.pool_size", "1");
		properties.setProperty("hbm2ddl.auto", ddlAuto);

		return properties;
	}

	private static Properties additionalPropertiesPostgres() {

		String dialect = "org.hibernate.dialect.PostgreSQLDialect";
		String ddlAuto = "update";

		Properties properties = new Properties();
		properties.setProperty("hibernate.dialect", dialect);
		properties.setProperty("hibernate.connection.url",
				"jdbc:postgresql://localhost:5432/medicalinventory");
		properties.setProperty("hibernate.connection.username", "postgres");
		properties.setProperty("hibernate.connection.password",
				"root");

		properties.setProperty("hibernate.connection.driver_class", org.postgresql.Driver.class.getCanonicalName());
		properties.setProperty("hibernate.current_session_context_class", "thread");
		properties.setProperty("hibernate.show_sql", "true");
		properties.setProperty("hibernate.connection.pool_size", "1");
		properties.setProperty("hbm2ddl.auto", ddlAuto);
		properties.setProperty("hibernate.temp.use_jdbc_metadata_defaults", "false");
		properties.setProperty("hibernate.jdbc.batch_size", "20");
		return properties;
	}

}
