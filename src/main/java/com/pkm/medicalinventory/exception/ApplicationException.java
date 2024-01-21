package com.pkm.medicalinventory.exception;

import com.pkm.medicalinventory.util.ErrorUtil;

public class ApplicationException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7610558300205998680L;

	public ApplicationException(Exception ex) {
		super(ErrorUtil.getRootCaouseMessage(ex));
	}
	public ApplicationException(String msg) {
		super(msg);
	}

	public static ApplicationException fromMessage(String msg) {
		return new ApplicationException(msg);
	}
  
}
