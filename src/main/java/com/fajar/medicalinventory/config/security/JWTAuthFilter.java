package com.fajar.medicalinventory.config.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fajar.medicalinventory.dto.WebResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JWTAuthFilter extends OncePerRequestFilter {

	private static final String PREFIX = "Bearer ";
	private UserDetailsService userDetailsService;
	@Autowired
	private JWTUtils jwtUtils;
	@Autowired
	private ObjectMapper objectMapper;

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		log.debug("___________JWTAuthFilter____________{}", request.getRequestURI());
		if (request.getMethod().toLowerCase().equals("options")) {
			setCorsHeaders(response);
			return;
		}
		try {
			String jwt = parseJwt(request);
			if (jwt != null) {
				if (jwtUtils.validateJwtToken(jwt)) {

					String username = jwtUtils.getUserNameFromJwtToken(jwt);

					UserDetails userDetails = userDetailsService.loadUserByUsername(username);
					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					log.info("JWT Authenticated..");
					SecurityContextHolder.getContext().setAuthentication(authentication);
					String refreshToken = jwtUtils.generateJwtToken(authentication);
					response.setHeader("access-token", refreshToken);
					response.setHeader("Access-Control-Expose-Headers", "access-token");
				} else {
					log.info("Failed validating JWT");
//					log.info("jwt is null");
					sendJsonResponseUnAuthenticated(request, response);
					return;
				}
			}
		} catch (Exception e) {
			log.error("Cannot set user authentication: {}", e);
		}

		filterChain.doFilter(request, response);
		if (request.getMethod().toLowerCase().equals("options")) {
			response.setStatus(HttpStatus.OK.value());
		}
	}

	public static void setCorsHeaders(HttpServletResponse response) {
		log.info("setCorsHeaders.....");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers",
				"Content-Type, Accept, X-Requested-With, Authorization, requestid, access-token");
//		response.setStatus(HttpStatus.OK.value());

	}

	private String parseJwt(HttpServletRequest request) {
		String headerAuth = request.getHeader("Authorization");
		log.debug("headerAuth: {}", headerAuth);
		if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(PREFIX)) {
			return headerAuth.substring(PREFIX.length(), headerAuth.length());
		}

		return null;
	}

	private void sendJsonResponseUnAuthenticated(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		log.info("JWT Authentication Failed");
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		WebResponse data = WebResponse.builder().date(new Date()).message("Unauthenticated").build();

		response.getOutputStream().println(objectMapper.writeValueAsString(data));

	}

}