package com.fajar.medicalinventory.config.security;

import java.io.IOException;

import javax.management.RuntimeErrorException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.access.DefaultWebInvocationPrivilegeEvaluator;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FormLoginFilter extends OncePerRequestFilter {

	@Autowired
	private ApplicationContext appContext;

	private String defaultPath;
	private String loginPath;

	public void setdefaultPath(String defaultPath) {
		this.defaultPath = defaultPath;
	}

	public void setLoginPath(String loginPath) {
		this.loginPath = loginPath;
	}

	private DefaultWebInvocationPrivilegeEvaluator webInvocationPrivilegeEvaluator = null;

	private DefaultWebInvocationPrivilegeEvaluator getWebInvocationPrivilegeEvaluator() {
		if (null != webInvocationPrivilegeEvaluator) {
			return webInvocationPrivilegeEvaluator;
		}
		String[] beanNames = appContext.getBeanNamesForType(DefaultWebInvocationPrivilegeEvaluator.class);
		if (null == beanNames || beanNames.length < 2) {
			throw new RuntimeException("NO BEAN FOR DefaultWebInvocationPrivilegeEvaluator found");
		}
		/**
		 * beanNames[0] = APIs, beanNames[1] = webPages see <http ....> in security.xml
		 */
		webInvocationPrivilegeEvaluator = (DefaultWebInvocationPrivilegeEvaluator) appContext.getBean(beanNames[1]);
		return webInvocationPrivilegeEvaluator;
	}

	private boolean isAllowedForWebPages(String path) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		boolean allowed = getWebInvocationPrivilegeEvaluator().isAllowed(path, auth);
		log.info("allowed for {}: {}", path, allowed);
		return allowed;
	}

	public void setUrlSession(HttpServletRequest request) {
		HttpSession session = request.getSession(false);

		if (session == null) {
			return;
		}
		session.setAttribute(SimpleAuthenticationSuccessHandler.TARGET_URL_ATTRIBUTE, getPath(request));
	}

	private void removeUrlSession(HttpServletRequest request) {
		HttpSession session = request.getSession(false);

		if (session == null) {
			return;
		}
		session.removeAttribute(SimpleAuthenticationSuccessHandler.TARGET_URL_ATTRIBUTE);

	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		setSavedPath(request);
		
		if (!isLoginPath(request)) {
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
			response.setHeader("location", request.getContextPath() + defaultPath);
			return;
		}
		filterChain.doFilter(request, response);

	}

	/**
	 * save path to session before redirecting to login page in order to redirect to it again after login
	 * @param request
	 */
	private void setSavedPath(HttpServletRequest request) {
		
		String path = getPath(request);
		boolean isLoginPath = isLoginPath(request);
		boolean allowed = isAllowedForWebPages(path);
		if (!allowed) {
			setUrlSession(request);
			log.info("SET TARGET_URL_ATTRIBUTE: {}", request.getRequestURI());
		} else if (!isLoginPath) {
			removeUrlSession(request);
		}
	}

	private boolean isLoginPath(HttpServletRequest request) {
		return request.getRequestURI().trim().equals(request.getContextPath() + loginPath);
	}

	private String getPath(HttpServletRequest request) {
		return request.getRequestURI().substring(request.getContextPath().length());
	}

}