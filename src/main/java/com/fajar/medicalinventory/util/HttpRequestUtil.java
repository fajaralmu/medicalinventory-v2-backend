package com.fajar.medicalinventory.util;

import javax.servlet.http.HttpServletRequest;

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

	

 
 

}
