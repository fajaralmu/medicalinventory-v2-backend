package com.pkm.medicalinventory.config.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pkm.medicalinventory.controller.AuthController;
import com.pkm.medicalinventory.dto.WebResponse;
import com.pkm.medicalinventory.entity.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler  {

	public static final String TARGET_URL_ATTRIBUTE = "last_get_url";
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	private JWTUtils jwtUtils;
	private ObjectMapper objectMapper; 
	
	public SimpleAuthenticationSuccessHandler(String defaultTargetUrl) {
		super();
		setDefaultTargetUrl(defaultTargetUrl);
	}
	 
	public void setJwtUtils(JWTUtils jwtUtils) {
		this.jwtUtils = jwtUtils;
	}
	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException {
		log.info("+++++++++++ ONSUCESS AUTH ++++++++++");
		
		handle(request, response, authentication);
		clearAuthenticationAttributes(request);
	}

	@Override
	protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException {

		AuthController.extractRequestHeader(request);
		
		
		if (isJsonResponse(request)) {
			sendJsonResponse(response, authentication);
			return;
		}
		String targetUrl = determineTargetUrl(request, response);
		if ( response.isCommitted()) {
			log.debug("Response has already been committed. Unable to redirect to " + targetUrl);
			return;
		}
		log.info("Redirect to {}", targetUrl);
		redirectStrategy.sendRedirect(request, response, targetUrl);
		 
	}
	
	@Override
	protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
		Object savedTargetUrl = getTargetUrlFromSession(request);
		log.info("getTargetUrlFromSession(request):{}",savedTargetUrl);
		if (null !=savedTargetUrl) {
			
			return savedTargetUrl.toString().trim();
		}
		return super.determineTargetUrl(request, response);
	}
	
	private Object getTargetUrlFromSession(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (null == session) return null;
		return session.getAttribute(TARGET_URL_ATTRIBUTE);
	}

	private void sendJsonResponse(HttpServletResponse response, Authentication authentication) {
		log.info(":::::::::: sendJsonResponse ::::::::::::");
		try {
			String jwt = jwtUtils.generateJwtToken(authentication);
			response.setHeader("access-token", jwt);
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.setHeader("Access-Control-Expose-Headers", "access-token");
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			UserDetailDomain principal = (UserDetailDomain) authentication.getPrincipal();
			User user = principal.getUserDetails();
			response.getWriter().write(objectMapper.writeValueAsString(WebResponse.builder()
//					.token(jwt)
					.user(user.toModel() ).build()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean isJsonResponse(HttpServletRequest httpServletRequest) {
		boolean isJsonResponse = httpServletRequest.getParameter("transport_type")!=null
				&&
				httpServletRequest.getParameter("transport_type").equals("rest");
//		log.info("isJsonResponse: {}", isJsonResponse);
		
		return isJsonResponse;
	}
	
//	@Override
//	protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
////		Map<String, String> roleTargetUrlMap = new HashMap<>();
////		roleTargetUrlMap.put(AuthorityType.ROLE_USER.toString(), "/loginsuccess");
////		roleTargetUrlMap.put(AuthorityType.ROLE_ADMIN.toString(), "/loginsuccess");
////
////		final Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
////		for (final GrantedAuthority grantedAuthority : authorities) {
////			String authorityName = grantedAuthority.getAuthority();
////			if (roleTargetUrlMap.containsKey(authorityName)) {
////				return roleTargetUrlMap.get(authorityName);
////			}
////		}
//
////		throw new IllegalStateException();
//		return super.defaultTargetUrl;
//	}
	 
	 
}