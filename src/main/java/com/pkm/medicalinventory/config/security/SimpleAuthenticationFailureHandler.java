package com.pkm.medicalinventory.config.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pkm.medicalinventory.dto.WebResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleAuthenticationFailureHandler implements AuthenticationFailureHandler {
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	private JWTUtils jwtUtils;
	private ObjectMapper objectMapper;
	
	public void setJwtUtils(JWTUtils jwtUtils) {
		this.jwtUtils = jwtUtils;
	}
	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {

		if (SimpleAuthenticationSuccessHandler.isJsonResponse(request)) {
			try {
				sendJsonResponse(request, response, exception);
				return;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		String targetUrl = "/login?error";
		if ( response.isCommitted()) {
			log.debug("Response has already been committed. Unable to redirect to " + targetUrl);
			return;
		}
		
		redirectStrategy.sendRedirect(request, response, targetUrl);
		
	}
	
	private class SS {
		
	}
	private void sendJsonResponse(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws  Exception {
		
		JWTAuthFilter.setCorsHeaders(response);
		
		log.info("Authentication Failed");
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		WebResponse data = WebResponse.builder().date(new Date())
				.message(exception.getMessage()).build();

		response.getOutputStream().println(objectMapper.writeValueAsString(data));
		
	}
}