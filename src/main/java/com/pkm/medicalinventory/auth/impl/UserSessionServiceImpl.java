package com.pkm.medicalinventory.auth.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.pkm.medicalinventory.auth.UserSessionService;
import com.pkm.medicalinventory.config.ApplicationProfileService;
import com.pkm.medicalinventory.config.security.JWTUtils;
import com.pkm.medicalinventory.dto.WebResponse;
import com.pkm.medicalinventory.entity.User;
import com.pkm.medicalinventory.inventory.WarehouseService;
import com.pkm.medicalinventory.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserSessionServiceImpl implements UserSessionService {

	@Autowired
	private JWTUtils jwtUtils;
	@Autowired
	private com.pkm.medicalinventory.auth.SessionValidationService sessionValidationService;
	@Autowired
	private ApplicationProfileService appProfileService;
	@Autowired
	private WarehouseService warehouseService;
	
	public String generateJwt() {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String jwt = jwtUtils.generateJwtToken(authentication);
			return jwt;
		} catch (Exception e) {
			return null;
		}
		
	}

	public WebResponse generateRequestId() {
		User loggedUser = sessionValidationService.getLoggedUser();
		WebResponse response = new WebResponse();
		if (null != loggedUser) {
			response.setUser(loggedUser.toModel());
			response.setLoggedIn(true);
		}
		response.setApplicationProfile(appProfileService.getApplicationProfile().toModel());
		response.setRequestId(randomRequestId());
		response.setMasterHealthCenter(warehouseService.getMasterHealthCenter().toModel());
		return response;
	}

	private String randomRequestId() {
		return StringUtil.generateRandomNumber(15);
	}
}
