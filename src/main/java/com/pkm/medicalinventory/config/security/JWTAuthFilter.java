package com.pkm.medicalinventory.config.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pkm.medicalinventory.dto.WebResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JWTAuthFilter extends OncePerRequestFilter {

	private static final String PREFIX = "Bearer ";
	private static final String ALLOW_HEADER_VAL = "Content-Type, Accept, X-Requested-With, Authorization, requestid, access-token";
	
	private UserDetailsService userDetailsService;
	
	@Autowired
	private JWTUtils jwtUtils;
	@Autowired
	private ObjectMapper objectMapper;

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {
		
		log.debug("___________JWTAuthFilter____________{}", request.getRequestURI());
		
		if (request.getMethod().toLowerCase().equals("options")) {
			setCorsHeaders(response);
			return;
		}
		try {
			String jwt = parseJwt(request);
			Object userPrincipal = null;
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth != null) {
			    Object principal = auth.getPrincipal();  
			    if (principal instanceof UserDetails) {
			    	userPrincipal = (UserDetails) principal;
			    }
			}
			log.debug("User principal: {}", userPrincipal);
			if (jwt != null && null == userPrincipal) {
				if (jwtUtils.validateJwtToken(jwt)) {

					String username = jwtUtils.getUserNameFromJwtToken(jwt);

					UserDetails userDetails = userDetailsService.loadUserByUsername(username);
					UsernamePasswordAuthenticationToken userAuth = new UsernamePasswordAuthenticationToken(
						userDetails,
						null,
						userDetails.getAuthorities()
					);
					userAuth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					
					log.info("JWT Authenticated..");
					
					SecurityContextHolder.getContext().setAuthentication(userAuth);
					String refreshToken = jwtUtils.generateJwtToken(userAuth);
					
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
		
	}

	public static void setCorsHeaders(HttpServletResponse response) {
		log.info("setCorsHeaders.....");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers", ALLOW_HEADER_VAL );
		response.setStatus(HttpStatus.OK.value());

	}

	private String parseJwt(HttpServletRequest request) {
		String headerAuth = request.getHeader("Authorization");
		log.debug("headerAuth: {}", headerAuth);
		if (
			headerAuth!=null && 
			(
				headerAuth.replaceAll("[ ]", "").toLowerCase().equals("bearernull") ||
				headerAuth.trim().toLowerCase().equals("bearer") ||
				headerAuth.isEmpty()
			)
		)  {
			return null;
		}
		if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(PREFIX)) {
			return headerAuth.substring(PREFIX.length(), headerAuth.length());
		}

		return null;
	}
	public static void main(String[] args) {
		String str = "Bearer null xxx";
		System.out.println(str.replaceAll("[ ]", ""));
	}

	private void sendJsonResponseUnAuthenticated(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		log.info("JWT Authentication Failed");
		
		setCorsHeaders(response);
		
		WebResponse data = new WebResponse("401", "Unauthenticated");
		response.getOutputStream().println(objectMapper.writeValueAsString(data));
		response.setStatus(HttpStatus.UNAUTHORIZED.value()); 

	}

}