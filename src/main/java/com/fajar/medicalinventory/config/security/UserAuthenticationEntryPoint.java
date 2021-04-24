package com.fajar.medicalinventory.config.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

	public UserAuthenticationEntryPoint(String loginFormUrl) {
		super(loginFormUrl); 
		// TODO Auto-generated constructor stub
	}

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex)
			throws IOException, ServletException {
		
		log.info("Auth exception {} on {}", ex.getMessage(), request.getRequestURI());
		if (request.getMethod().toLowerCase().equals(RequestMethod.POST.toString().toLowerCase())) {
			printApiResponse(response, ex);
		} else {
			response.setStatus(HttpStatus.FOUND.value());
			response.setHeader("location", request.getContextPath()+"/login");
		}
	}

	private void printApiResponse(HttpServletResponse response, AuthenticationException ex) throws IOException {
		 
		response.setContentType("application/json");
		response.getOutputStream().print("{\"code\":\"-1\",\"message\":\""+ex.getMessage()+"\"}");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}
}