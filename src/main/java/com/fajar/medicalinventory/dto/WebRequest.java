package com.fajar.medicalinventory.dto;

import java.io.Serializable;
import java.util.List;

import com.fajar.medicalinventory.annotation.Dto;
import com.fajar.medicalinventory.entity.ApplicationProfile;
import com.fajar.medicalinventory.entity.BaseEntity;
import com.fajar.medicalinventory.entity.Customer;
import com.fajar.medicalinventory.entity.HealthCenter;
import com.fajar.medicalinventory.entity.Product;
import com.fajar.medicalinventory.entity.Supplier;
import com.fajar.medicalinventory.entity.Transaction;
import com.fajar.medicalinventory.entity.Unit;
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
	
	private Customer customer;
	private HealthCenter healthcenter;
	private Product product;
	private Supplier supplier;
	private Unit unit;

	/**
	 * ==========end entity============
	 */

	private Filter filter; 
	
	private User user; 
	private ApplicationProfile profile;  
	private BaseEntity entityObject; 
	private Transaction transaction;
	private List<BaseEntity> orderedEntities; 
	
	private boolean regularTransaction; 
	
	private String imageData; 

}
