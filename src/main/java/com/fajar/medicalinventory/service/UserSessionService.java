package com.fajar.medicalinventory.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fajar.medicalinventory.config.security.JWTUtils;
import com.fajar.medicalinventory.dto.WebResponse;
import com.fajar.medicalinventory.entity.User;
import com.fajar.medicalinventory.service.config.DefaultApplicationProfileService;
import com.fajar.medicalinventory.util.StringUtil;

@Service
public class UserSessionService {

	@Autowired
	private JWTUtils jwtUtils;
	@Autowired
	private SessionValidationService sessionValidationService;
	@Autowired
	private DefaultApplicationProfileService defaultApplicationProfileService;
	
	public String generateJwt() {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String jwt = jwtUtils.generateJwtToken(authentication);
			return jwt;
		} catch (Exception e) {
			return null;
		}
		
	}

	public WebResponse generateRequestId(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		User loggedUser = sessionValidationService.getLoggedUser(httpRequest);
		WebResponse response = new WebResponse();
		if (null != loggedUser) {
			response.setUser(loggedUser);
			response.setLoggedIn(true);
		}
		response.setApplicationProfile(defaultApplicationProfileService.getApplicationProfile());
		response.setRequestId(randomRequestId());
		return response;
	}

	private String randomRequestId() {
		return StringUtil.generateRandomNumber(15);
	}
}
