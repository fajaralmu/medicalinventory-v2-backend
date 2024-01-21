package com.pkm.medicalinventory.auth;

import javax.servlet.http.HttpServletRequest;

import com.pkm.medicalinventory.entity.User;

public interface SessionValidationService {
	User getLoggedUser();
	Object getUserPrincipal();
	boolean validatePrinciple(Object principal);
}
