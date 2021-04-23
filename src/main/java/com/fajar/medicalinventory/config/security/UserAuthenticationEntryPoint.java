package com.fajar.medicalinventory.config.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex)
			throws IOException, ServletException {
		log.warn("Auth exception {} on {}", ex.getMessage(), request.getRequestURI());
		response.setContentType("application/json");
		response.getOutputStream().print("{\"message\":\""+ex.getMessage()+"\"}");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}
}