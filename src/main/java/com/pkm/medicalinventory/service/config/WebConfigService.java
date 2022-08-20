package com.pkm.medicalinventory.service.config;

import static com.pkm.medicalinventory.util.CollectionUtil.emptyArray;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.pkm.medicalinventory.config.LogProxyFactory;
import com.pkm.medicalinventory.entity.BaseEntity;
import com.pkm.medicalinventory.entity.User;
import com.pkm.medicalinventory.repository.AppProfileRepository;
import com.pkm.medicalinventory.util.CollectionUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;

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
  
	private String uploadedImageRealPath;//, uploadedImagePath, reportPath;
	private User defaultSuperAdminUser;

	@PostConstruct
	public void init() { setLoggers(); }

	private void setLoggers() {
		
		String[] beanNames = applicationContext.getBeanDefinitionNames();
		for (String string : beanNames) {
			Object beans = applicationContext.getBean(string);
			if (beans == null || !beans.getClass().getCanonicalName().startsWith("com.pkm")) continue;
			LogProxyFactory.setLoggers(beans);
			
		}
	}
	
}
