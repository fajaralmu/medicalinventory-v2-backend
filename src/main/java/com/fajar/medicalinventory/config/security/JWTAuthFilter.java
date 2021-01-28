package com.fajar.medicalinventory.config.security;

import java.io.IOException;

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

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JWTAuthFilter extends OncePerRequestFilter {

	private static final String PREFIX = "Bearer ";
	private UserDetailsService userDetailsService;
	@Autowired
	private JWTUtils jwtUtils;
	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		log.info("___________JWTAuthFilter____________{}", request.getRequestURI());
		try {
			String jwt = parseJwt(request);
			if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
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
//				log.info("jwt is null");
			}
		} catch (Exception e) {
			log.error("Cannot set user authentication: {}", e);
		}
		if (request.getMethod().toLowerCase().equals("options")) {
			setCorsHeaders(response);
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
		response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With, Authorization, requestid, access-token");
//		response.setStatus(HttpStatus.OK.value());
		
	}
	private String parseJwt(HttpServletRequest request) {
		String headerAuth = request.getHeader("Authorization");

		if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(PREFIX)) {
			return headerAuth.substring(PREFIX.length(), headerAuth.length());
		}

		return null;
	}

}
