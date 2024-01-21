package com.pkm.medicalinventory.controller.api;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pkm.medicalinventory.annotation.CustomRequestInfo;
import com.pkm.medicalinventory.auth.UserService;
import com.pkm.medicalinventory.controller.BaseController;
import com.pkm.medicalinventory.dto.WebRequest;
import com.pkm.medicalinventory.dto.WebResponse;
import com.pkm.medicalinventory.dto.model.UserModel;
import com.pkm.medicalinventory.entity.User;
import com.pkm.medicalinventory.util.HttpRequestUtil;

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

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@RestController
@RequestMapping("/api/app/account")
@Slf4j
public class AccountController extends BaseController {

	@Autowired
	private UserService userService;
	public AccountController() {
		log.info("------------------RestAccountController-----------------");
	}

	
	@PostMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
	public User user() throws IOException {
		return sessionValidationService.getLoggedUser();
	}
	@PostMapping(
		value = "/updateprofile",
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@CustomRequestInfo(withRealtimeProgress = true)
	public WebResponse updateProfile(@RequestBody WebRequest webRequest) {
		log.info("update profile");
		UserModel user = userService.updateProfile(webRequest);
		return new WebResponse().withUser(user);
	}
	@PostMapping(
		value="/logout",
		produces = MediaType.APPLICATION_JSON_VALUE
	)
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
