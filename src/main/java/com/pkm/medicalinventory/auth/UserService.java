package com.pkm.medicalinventory.auth;

import javax.servlet.http.HttpServletRequest;

import com.pkm.medicalinventory.dto.WebRequest;
import com.pkm.medicalinventory.dto.model.UserModel;

public interface UserService {

	UserModel updateProfile(WebRequest webRequest);

}
