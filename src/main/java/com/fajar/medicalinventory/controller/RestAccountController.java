package com.fajar.medicalinventory.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fajar.medicalinventory.annotation.CustomRequestInfo;
import com.fajar.medicalinventory.dto.WebRequest;
import com.fajar.medicalinventory.dto.WebResponse;
import com.fajar.medicalinventory.entity.User;
import com.fajar.medicalinventory.service.config.DefaultUserService;
import com.fajar.medicalinventory.util.HttpRequestUtil;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@RestController
@RequestMapping("/api/app/account")
@Slf4j
public class RestAccountController extends BaseController {

	@Autowired
	private DefaultUserService userService;
	public RestAccountController() {
		log.info("------------------RestAccountController-----------------");
	}

	
	@PostMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
	public User user(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
		return sessionValidationService.getLoggedUser(httpRequest);
	}
	@PostMapping(value = "/updateprofile", produces = MediaType.APPLICATION_JSON_VALUE)
	@CustomRequestInfo(withRealtimeProgress = true)
	public WebResponse updateProfile(@RequestBody WebRequest webRequest, HttpServletRequest httpRequest) throws IOException {
		log.info("update profile");
		return userService.updateProfile(httpRequest, webRequest);
	}
	@PostMapping(value="/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse logout (HttpServletRequest request, HttpServletResponse response) {
		try {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){   
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
         
		} catch (Exception e) { }
		
		HttpRequestUtil.removeLoginKeyCookie(response);
        return new WebResponse();
    }

}
