package com.fajar.medicalinventory.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpRequestUtil {
	
	public static final String PAGE_REQUEST_ID = "requestId";

	public static String getPageRequestId(HttpServletRequest httpServletRequest) {
		if (null == httpServletRequest) {return "";}
		String pageRequest = httpServletRequest.getHeader(PAGE_REQUEST_ID);
		log.trace("Page request id: " + pageRequest);
		return pageRequest;
	}

	public static void removeLoginKeyCookie(HttpServletResponse response) {
		 
		response.setHeader("Set-Cookie", "medical-inventory-login-key=;path=/;expires=Thu, 01 Jan 1970 00:00:00 GMT;");
	} 

	

 
 

}
