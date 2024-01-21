package com.pkm.medicalinventory.config;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.pkm.medicalinventory.entity.User;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * this class is autowired via XML
 * 
 * @author Republic Of Gamers
 *
 */

@Data
@Slf4j
public class WebConfigService {

	@Autowired
	private ApplicationContext applicationContext;

	private String uploadedImageRealPath;// , uploadedImagePath, reportPath;
	private User defaultSuperAdminUser;

	@PostConstruct
	public void init() {
		setLoggers();
	}

	private void setLoggers() {

		String[] beanNames = applicationContext.getBeanDefinitionNames();
		Thread t = new Thread(() -> {
			for (String string : beanNames) {
				Object beans = applicationContext.getBean(string);
				if (beans == null || !beans.getClass().getCanonicalName().startsWith("com.pkm"))
					continue;
				LogProxyFactory.setLoggers(beans);

			}
		});
		t.start();
	}

}
