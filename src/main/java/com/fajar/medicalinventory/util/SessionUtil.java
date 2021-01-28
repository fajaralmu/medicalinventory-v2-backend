package com.fajar.medicalinventory.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fajar.medicalinventory.dto.WebResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SessionUtil {
	public static final String PAGE_REQUEST = "page_req_id";
	public static final String ATTR_USER = "user";
	public static final String PAGE_REQUEST_ID = "requestId";
	public static final String JSESSSIONID = "JSESSIONID";
	public static final String HEADER_LOGIN_KEY = "loginKey";
	public static final String ACCESS_CONTROL_EXPOSE_HEADER = "Access-Control-Expose-Headers";
	public static final String ATTR_REQUEST_URI = "requestURI";
	public static final String ATTR_REGISTERED_REQUEST_ID = "registered_request_id";
	public static final String PAGE_CODE = "page-code";
	public static final String HEADER_REQUEST_TOKEN = "requestToken";

	public static String getPageRequestId(HttpServletRequest httpServletRequest) {
		if (null == httpServletRequest) {return "";}
		String pageRequest = httpServletRequest.getHeader(PAGE_REQUEST_ID);
		log.info("Page request id: " + pageRequest);
		return pageRequest;
	} 

	public static void setLoginKeyHeader(HttpServletResponse servletResponse, String loginKey) {

		servletResponse.addHeader(HEADER_LOGIN_KEY, loginKey);
	}

	public static void setAccessControlExposeHeader(HttpServletResponse httpResponse) {

		httpResponse.addHeader(ACCESS_CONTROL_EXPOSE_HEADER, "*");
	}
 

	public static void setSessionRequestUri(HttpServletRequest request) {

		request.getSession().setAttribute(ATTR_REQUEST_URI, request.getRequestURI());
		log.info("REQUESTED URI: " + request.getRequestURI());
	} 

	public static void removeSessionUserAndInvalidate(HttpServletRequest request) {

		request.getSession(false).removeAttribute(ATTR_USER);
		request.getSession(false).invalidate();

	}

	public static String getSessionRequestUri(HttpServletRequest httpRequest) {
		try {
			return httpRequest.getSession(false).getAttribute(ATTR_REQUEST_URI).toString();
		} catch (Exception e) {
			return null;
		}
	}

	public static void setSessionRegisteredRequestId(HttpServletRequest httpRequest, WebResponse requestIdResponse) {

		httpRequest.getSession(false).setAttribute(ATTR_REGISTERED_REQUEST_ID, requestIdResponse.getMessage());

	}
 
	public static String getRequestToken(HttpServletRequest httpRequest) {
		try {
			return httpRequest.getHeader(HEADER_REQUEST_TOKEN).toString();
		} catch (Exception e) {
			return null;
		}
	}

	public static String getSessionRegisteredRequest(HttpServletRequest request) {

		try {
			return request.getSession().getAttribute(ATTR_REGISTERED_REQUEST_ID).toString();
		} catch (Exception e) {

		}
		return null;
	}

 
 

}
