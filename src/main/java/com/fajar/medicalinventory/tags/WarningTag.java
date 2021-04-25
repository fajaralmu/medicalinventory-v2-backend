package com.fajar.medicalinventory.tags;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

import org.springframework.web.servlet.tags.RequestContextAwareTag;

import lombok.Data;

@Data
public class WarningTag  extends RequestContextAwareTag {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1299201183531460093L;
	String jspPage = "../tags/warning.jsp";
	
	private String message;
	@Override
	protected int doStartTagInternal() throws Exception {
		 
		return 0;
		
	}
	@Override
	public void doFinally() {
		JspWriter out = pageContext.getOut();
		try {
			HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
			request.setAttribute("message", getMessage());
			request.getRequestDispatcher(jspPage);
			pageContext.include(jspPage);
		} catch (Exception e) {
			try {
				out.print(e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}


}
