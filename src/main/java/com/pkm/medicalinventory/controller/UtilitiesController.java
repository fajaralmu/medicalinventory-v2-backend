package com.pkm.medicalinventory.controller;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.NestedServletException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pkm.medicalinventory.config.security.JWTAuthFilter;
import com.pkm.medicalinventory.dto.WebResponse;
import com.pkm.medicalinventory.util.ErrorUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author fajar
 *
 */
@Slf4j
@Controller
@RequestMapping("web")
public class UtilitiesController {  
	private ObjectMapper objectMapper;
	
	public UtilitiesController() {
		log.info("-----------------UtilitiesController------------------");
		objectMapper = new ObjectMapper();
	}


	@RequestMapping(value = "/app-error", method = {RequestMethod.POST, RequestMethod.GET}) 
	public ModelAndView renderErrorPage(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
		ModelAndView errorPage = new ModelAndView("error/errorPage");

		int httpErrorCode = getErrorCode(httpRequest);

		if (200 == httpErrorCode) {
			httpResponse.sendRedirect(httpRequest.getContextPath()+"/index");
			return null;
		}

		JWTAuthFilter.setCorsHeaders(httpResponse);
		errorPage.addObject("errorCode", httpErrorCode);
		errorPage.addObject("errorMessage", getErrorMessage(httpRequest ));
		httpResponse.setStatus(httpErrorCode);
		printHttpRequestAttrs(httpRequest);
		return errorPage;
	}
	
	@RequestMapping(value = "/error-not-found", produces = MediaType.APPLICATION_JSON_VALUE, method = {RequestMethod.POST, RequestMethod.GET}) 
	public void errorNotFound(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
		 
		int httpErrorCode = getErrorCode(httpRequest);

		if (200 == httpErrorCode) {
			httpResponse.sendRedirect(httpRequest.getContextPath()+"/index");
			return;
		}
		JWTAuthFilter.setCorsHeaders(httpResponse);
		
		WebResponse payload = WebResponse.failed(getErrorMessage(httpRequest));
		payload.setCode("404");
		httpResponse.setStatus(404);

		String jsonString = objectMapper.writeValueAsString(payload); 
		
		httpResponse.getWriter().write(jsonString);
		printHttpRequestAttrs(httpRequest);
		
	}
	private String getErrorMessage(HttpServletRequest httpRequest) {
		try {
			Object exception = httpRequest.getAttribute("javax.servlet.error.exception");
			log.error("======= !! HANDLING exception: {}", exception);
			if (exception != null && exception instanceof NestedServletException) {
				NestedServletException nestedServletException = (NestedServletException) exception;
				return ErrorUtil.getRootCaouseMessage(nestedServletException);
			}
			
			return String.valueOf(exception);
		} catch (Exception e) {
			return "Error occured";
		}
	}
	 

	@RequestMapping(value = "/error-general", produces = MediaType.APPLICATION_JSON_VALUE, method = {RequestMethod.POST, RequestMethod.GET}) 
	public void errorGeneral(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
		 
		int httpErrorCode = getErrorCode(httpRequest);

		if (200 == httpErrorCode) {
			httpResponse.sendRedirect(httpRequest.getContextPath()+"/index");
			return;
		}
		JWTAuthFilter.setCorsHeaders(httpResponse);

		String message 		= getErrorMessage(httpRequest );
		WebResponse payload = WebResponse.failed(String.valueOf(message));
		payload.setCode("400");
		httpResponse.setStatus(400);

		String jsonString = objectMapper.writeValueAsString(payload); 
		httpResponse.getWriter().write(jsonString);
		
		printHttpRequestAttrs(httpRequest);
		
	}

	private void printHttpRequestAttrs(HttpServletRequest httpRequest) {
		Enumeration<String> attrNames = httpRequest.getAttributeNames();
		log.debug("========= error request http attrs ========");
		int number = 1;
		while (attrNames.hasMoreElements()) {
			String attrName = attrNames.nextElement();
			Object attributeValue = httpRequest.getAttribute(attrName);
			log.debug(number + ". " + attrName + " : " + attributeValue + " || TYPE: "
					+ (attributeValue == null ? "" : attributeValue.getClass()));
			number++;
		}
		log.debug("===== ** end ** ====");
	}

	private int getErrorCode(HttpServletRequest httpRequest) {
		if (null == httpRequest.getAttribute("javax.servlet.error.status_code")) {
			return 200;
		}
		try {
			Integer status_code = (Integer) httpRequest.getAttribute("javax.servlet.error.status_code");
			log.debug("status_code:{}", status_code);
			return status_code;
		} catch (Exception e) {

			return 500;
		}
	}
}
