package com.fajar.medicalinventory.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.SerializationUtils;

import com.fajar.medicalinventory.entity.User;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.extern.slf4j.Slf4j;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

 @Data
 @Builder
 @AllArgsConstructor
 @Slf4j
 @NoArgsConstructor
public class UserSessionModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3868645032944633878L;
	/**
	 * 
	 */
	//private static final long serialVersionUID = 3868645032944633878L;
	private User user;
	//key using for storing flat file
	private String requestKey;
	//page ID
	private String requestId;
	private String jwt;
	 
	
	public UserSessionModel(User user, String requestToken, String requestId) {
		this.user = SerializationUtils.clone(user);
		this.requestKey=(requestToken);
		this.requestId = requestId;
	}
	
	 
	
	

}
