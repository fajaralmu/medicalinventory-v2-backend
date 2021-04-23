package com.fajar.medicalinventory.config.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FormLoginFilter extends OncePerRequestFilter {

	
	private String defaultPath;
	private String loginPath;
	public void setdefaultPath(String defaultPath) {
		this.defaultPath = defaultPath;
	}
	public void setLoginPath(String loginPath) {
		this.loginPath = loginPath;
	}
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if (!request.getRequestURI().trim().equals(request.getContextPath()+loginPath)) {
			filterChain.doFilter(request, response);
			return;
		}
		log.info("FILTER PRE LOGIN : {}", request.getRequestURI());
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Object userPrincipal = null;
		if (auth != null) {
		    Object principal = auth.getPrincipal();  
		    if (principal instanceof UserDetails) {
		    	userPrincipal = (UserDetails) principal;
		    }
		}
		log.info("userPrincipal: {}", userPrincipal);
		if (null != userPrincipal) {
			response.setStatus(HttpStatus.FOUND.value());
			response.setHeader("location", request.getContextPath()+defaultPath);
			return;
		}
		filterChain.doFilter(request, response);
		
	}

	 

}