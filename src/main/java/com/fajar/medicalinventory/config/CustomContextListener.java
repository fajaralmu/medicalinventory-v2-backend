package com.fajar.medicalinventory.config;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		log.info("---------- context initialized ------------");
		
	}

	public final void contextDestroyed(ServletContextEvent sce) {
		log.info("---------- context destroyed ------------");
		WebSocketConfig.stopThreadPoolTaskScheduler();
	    // ... First close any background tasks which may be using the DB ...
	    // ... Then close any DB connection pools ...
		
	    // Now deregister JDBC drivers in this context's ClassLoader:
	    // Get the webapp's ClassLoader
	    ClassLoader cl = Thread.currentThread().getContextClassLoader();
	    // Loop through all drivers
	    Enumeration<Driver> drivers = DriverManager.getDrivers();
	    while (drivers.hasMoreElements()) {
	        Driver driver = drivers.nextElement();
	        if (driver.getClass().getClassLoader() == cl) {
	            // This driver was registered by the webapp's ClassLoader, so deregister it:
	            try {
	                log.info("Deregistering JDBC driver {}", driver);
	                DriverManager.deregisterDriver(driver);
	            } catch (SQLException ex) {
	                log.error("Error deregistering JDBC driver {}", driver, ex);
	            }
	        } else {
	            // driver was not registered by the webapp's ClassLoader and may be in use elsewhere
	            log.trace("Not deregistering JDBC driver {} as it does not belong to this webapp's ClassLoader", driver);
	        }
	   
	    }
	}
}
