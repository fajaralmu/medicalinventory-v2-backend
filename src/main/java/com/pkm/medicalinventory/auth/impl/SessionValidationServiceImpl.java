package com.pkm.medicalinventory.auth.impl;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.pkm.medicalinventory.auth.SessionValidationService;
import com.pkm.medicalinventory.config.security.UserDetailDomain;
import com.pkm.medicalinventory.entity.User;
import com.pkm.medicalinventory.util.HttpRequestUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SessionValidationServiceImpl implements SessionValidationService {

	public boolean validatePrinciple(Object principal) {		 
		if (principal instanceof UsernamePasswordAuthenticationToken) {
			UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) principal;
			if (auth.getPrincipal() instanceof UserDetailDomain == false) {
				log.error("usernamePasswordAuthenticationToken.getPrincipal() is not instance of UserDetailDomain");
				return false;
			}
			//throw new IllegalArgumentException("Principal can not be null!");
			return true;
		}
		log.error("Principal is not instance of UsernamePasswordAuthenticationToken");
		return false;
	}
	public User getLoggedUser() {
		HttpServletRequest request = HttpRequestUtil.getHttpServletReq();
		if (validatePrinciple(request.getUserPrincipal()) == false) {
			return null;
		}
		User user = ((UserDetailDomain) getUserPrincipal()).getUserDetails();
		return user;
	}
	public Object getUserPrincipal() {
		HttpServletRequest request = HttpRequestUtil.getHttpServletReq();
		boolean validated = validatePrinciple(request.getUserPrincipal());
		if (validated) {
			return ((UsernamePasswordAuthenticationToken)request.getUserPrincipal()).getPrincipal();
		}
		return null;
	}

}
