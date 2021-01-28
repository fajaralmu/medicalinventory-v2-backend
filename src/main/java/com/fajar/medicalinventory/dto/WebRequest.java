package com.fajar.medicalinventory.dto;

import java.io.Serializable;
import java.util.List;

import com.fajar.medicalinventory.annotation.Dto;
import com.fajar.medicalinventory.entity.ApplicationProfile;
import com.fajar.medicalinventory.entity.BaseEntity;
import com.fajar.medicalinventory.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 110411933791444017L;


	
	/**
	 * ENTITY CRUD use lowerCase!!!
	 */

	private String entity;
	private User user; 
	private ApplicationProfile profile;  
	

	/**
	 * ==========end entity============
	 */

	private Filter filter; 
	
	private BaseEntity entityObject;
	private AttachmentInfo attachmentInfo; 
	private List<BaseEntity> orderedEntities; 
	
	private boolean regularTransaction;
	
	private String imageData; 

}
