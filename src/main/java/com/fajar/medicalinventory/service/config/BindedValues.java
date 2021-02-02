package com.fajar.medicalinventory.service.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Component
@Data
@Slf4j
public class BindedValues {

	@Value("${app.admin.pass}")
	private String adminPass; 
	@Value("${app.header.label}")
	protected String applicationHeaderLabel;
	@Value("${app.header.description}")
	protected String applicationDescription;
	@Value("${app.footer.label}")
	protected String applicationFooterLabel; 

	@Value("${app.config.masterHealthCenter.code}")
	private String masterHealthCenterCode;
	@Value("${app.config.masterHealthCenter.name}")
	private String masterHealthCenterName;
	@Value("${app.config.code}")
	private String configCode;
	
	@Autowired
	ApplicationContext applicationContext;

	@PostConstruct
	public void init() {
		printBeans();
	}
	public void printBeans() {
//		List<String> list = (Arrays.asList(applicationContext.getBeanDefinitionNames()));
//		for (String string : list) {
//			log.info("bean: {} ", string);
//			
//		} 
		
		
		
	}
	
	public static void main(String[] args) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		
		System.out.println(encoder.encode("123"));
	}

}
