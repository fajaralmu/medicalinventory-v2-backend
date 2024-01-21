package com.pkm.medicalinventory.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpRequestUtil {
	
	public static final String PAGE_REQUEST_ID = "requestId";
	
	public static HttpServletRequest getHttpServletReq() {
		return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes())
                .getRequest();
	}

	public static String getPageRequestId() {
		HttpServletRequest request = getHttpServletReq();
		        
		if (null == request) {return "";}
		String pageRequest = request.getHeader(PAGE_REQUEST_ID);
		log.trace("Page request id: " + pageRequest);
		return pageRequest;
	}

	public static void removeLoginKeyCookie(HttpServletResponse response) {
		 
		response.setHeader("Set-Cookie", "medical-inventory-login-key=;path=/;expires=Thu, 01 Jan 1970 00:00:00 GMT;");
	} 

	

 
 

}
