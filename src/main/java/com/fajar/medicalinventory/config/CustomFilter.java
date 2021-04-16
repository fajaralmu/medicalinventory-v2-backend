package com.fajar.medicalinventory.config;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.fajar.medicalinventory.config.security.JWTAuthFilter;
import com.fajar.medicalinventory.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomFilter implements javax.servlet.Filter {

	public CustomFilter() {
		log.info("_________________CustomFilter______________");
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		Date startTime = new Date();
		HttpServletRequest req = (HttpServletRequest) request;

		final String randomID = randomID();
		/*
		 * ================== REQUEST ==================
		 */
		log.info("");
		log.info("****************************** BEGIN API " + randomID + " ***************************");
		log.info(uriInfo(req));
		printRequestHeaders(req);
		log.info("********************************************************************");
		log.info("");

		chain.doFilter(request, response);
		HttpServletResponse res = (HttpServletResponse) response;
		if (req.getMethod().toLowerCase().equals("options")) {
			log.info("PRE HANDLE SET CORS HEADER: {}", req.getRequestURI());
			JWTAuthFilter.setCorsHeaders(res);
			res.setHeader("print_id", randomID);
		}
		Date endTime = new Date();

		/*
		 * ================== RESPONSE =================
		 */
		log.info("");
		log.info("***************************** END API " + randomID + " *******************************");
		log.info(uriInfo(req));
		printResponseHeaders(res);
		log.info("Status: " + res.getStatus() + " Duration: " + getDuration(startTime, endTime) + " ms");
		log.info("*********************************************************************");
		log.info("");

	}

	private String randomID() {

		return StringUtil.generateRandomNumber(5);
	}

	private void printResponseHeaders(HttpServletResponse res) {
		Collection<String> headers = res.getHeaderNames();
		for (String header : headers) {
			log.info(header + ": " + res.getHeader(header));
		}
	}

	private void printRequestHeaders(HttpServletRequest req) {
		Enumeration<String> headers = req.getHeaderNames();
		while (headers.hasMoreElements()) {
			String header = headers.nextElement();
			log.info(header + ": " + req.getHeader(header));
		}
	}

	private String uriInfo(HttpServletRequest req) {
		return ("URI: [" + req.getMethod() + "]" + req.getRequestURI());
	}

	private long getDuration(Date startTime, Date endTime) {
		return endTime.getTime() - startTime.getTime();
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	// other methods
}
