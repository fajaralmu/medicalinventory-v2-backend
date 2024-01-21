package com.pkm.medicalinventory.config.security;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SpringSecurityInitializer extends AbstractSecurityWebApplicationInitializer {
    //no code needed
	public SpringSecurityInitializer() {
		log.info("*******************8SpringSecurityInitializer******************");
	}
}