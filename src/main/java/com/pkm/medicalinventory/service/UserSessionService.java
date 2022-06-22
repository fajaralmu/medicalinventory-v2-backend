package com.pkm.medicalinventory.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pkm.medicalinventory.config.security.JWTUtils;
import com.pkm.medicalinventory.dto.WebResponse;
import com.pkm.medicalinventory.entity.User;
import com.pkm.medicalinventory.service.config.DefaultApplicationProfileService;
import com.pkm.medicalinventory.service.config.DefaultHealthCenterMasterService;
import com.pkm.medicalinventory.util.StringUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserSessionService {

	@Autowired
	private JWTUtils jwtUtils;
	@Autowired
	private SessionValidationService sessionValidationService;
	@Autowired
	private DefaultApplicationProfileService defaultApplicationProfileService;
	@Autowired
	private DefaultHealthCenterMasterService defaultHealthCenterMasterService;
	
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
			response.setUser(loggedUser.toModel());
			response.setLoggedIn(true);
		}
		response.setApplicationProfile(defaultApplicationProfileService.getApplicationProfile().toModel());
		response.setRequestId(randomRequestId());
		response.setMasterHealthCenter(defaultHealthCenterMasterService.getMasterHealthCenter().toModel());
		return response;
	}

	private String randomRequestId() {
		return StringUtil.generateRandomNumber(15);
	}
}
