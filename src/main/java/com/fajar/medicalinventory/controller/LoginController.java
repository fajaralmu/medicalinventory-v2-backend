package com.fajar.medicalinventory.controller;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.fajar.medicalinventory.annotation.CustomRequestInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author fajar
 *
 */
@Slf4j
@Controller
@SessionAttributes({ "currentUser" })
public class LoginController extends BaseController {

	public LoginController() {
		log.info("LoginController");
	}

	@RequestMapping(value = { "/login.html" }, method = RequestMethod.GET)
	@CustomRequestInfo(pageUrl = "pages/login", title = "Login", stylePaths = "login")
	public String loginPage(@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout, Model model, HttpServletRequest httpServletRequest) {
		String errorMessge = null;
		if (null != sessionValidationService.getLoggedUser(httpServletRequest)) {
			return "redirect:/member/dashboard";
		}
		if (error != null) {
			errorMessge = "Username or Password is incorrect !!";
		}
		if (logout != null) {
			errorMessge = "You have been successfully logged out !!";
		}
		model.addAttribute("errorMessge", errorMessge);
		return basePage;
	}
	
	@RequestMapping(value="/logout", method = RequestMethod.GET)
    public String logoutPage (HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){   
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login.html?logout=true";
    }
	@RequestMapping(value="/loginsuccess" )
	public String loginsuccess (HttpServletRequest request, HttpServletResponse response) throws IllegalAccessException {
		extractRequestHeader(request);
		extractResponseHeader(response);
		return "redirect:/member/dashboard";
	}
	
	public static Map<String, String> extractResponseHeader(HttpServletResponse httpResponse) {
		log.info("==========extractResponseHeader {}===========");
		Map<String, String> headers = new HashMap<>();
		Collection<String> headerNames = httpResponse.getHeaderNames();
		if (headerNames != null) {
			for(String name:headerNames) {				
				String value = httpResponse.getHeader(name);
				headers.put(name, value);
				log.info("{}:{}", name, value);
			}
		}
		log.info("===============");
		return headers;
	}
	public static Map<String, String> extractRequestHeader(HttpServletRequest httpRequest) {
		log.info("==========extractRequestHeader {}===========", httpRequest.getRequestURI());
		log.info("method: {}", httpRequest.getMethod());
		log.info("__header__");
		Map<String, String> headers = new HashMap<>();
		Enumeration<String> headerNames = httpRequest.getHeaderNames();
		if (headerNames != null) {
			while (headerNames.hasMoreElements()) {
				String name = headerNames.nextElement();
				String value = httpRequest.getHeader(name);
				 
				headers.put(name, value);
				log.info("#HEADER {}:{}", name, value);
			}
		}
		
		log.info("__parameters__");
		Enumeration<String> parameterNames = httpRequest.getParameterNames();
		while(parameterNames.hasMoreElements()) {
			try {
				String name = parameterNames.nextElement();
				log.info("#PARAM {}:{}", name, httpRequest.getParameter(name));
			}catch (Exception e) { }
		}
		
		log.info("===============");
		return headers;
	}
}
