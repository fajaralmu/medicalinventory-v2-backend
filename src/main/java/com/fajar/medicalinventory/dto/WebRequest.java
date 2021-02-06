package com.fajar.medicalinventory.dto;

import java.io.Serializable;
import java.util.List;

import com.fajar.medicalinventory.dto.model.ApplicationProfileModel;
import com.fajar.medicalinventory.dto.model.BaseModel;
import com.fajar.medicalinventory.dto.model.ConfigurationModel;
import com.fajar.medicalinventory.dto.model.CustomerModel;
import com.fajar.medicalinventory.dto.model.HealthCenterModel;
import com.fajar.medicalinventory.dto.model.ProductFlowModel;
import com.fajar.medicalinventory.dto.model.ProductModel;
import com.fajar.medicalinventory.dto.model.SupplierModel;
import com.fajar.medicalinventory.dto.model.TransactionModel;
import com.fajar.medicalinventory.dto.model.UnitModel;
import com.fajar.medicalinventory.dto.model.UserModel;
import com.fajar.medicalinventory.entity.ApplicationProfile;
import com.fajar.medicalinventory.entity.BaseEntity;
import com.fajar.medicalinventory.entity.Configuration;
import com.fajar.medicalinventory.entity.Product;
import com.fajar.medicalinventory.entity.ProductFlow;
import com.fajar.medicalinventory.entity.Supplier;
import com.fajar.medicalinventory.entity.Transaction;
import com.fajar.medicalinventory.entity.Unit;
import com.fajar.medicalinventory.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
	
	private CustomerModel customer;
	private HealthCenterModel healthcenter;
	private ProductModel product;
	private SupplierModel supplier;
	private UnitModel unit;
	private ProductFlowModel productflow;

	/**
	 * ==========end entity============
	 */

	private Filter filter; 
	
	private UserModel user; 
	private ApplicationProfileModel profile; 
	private ConfigurationModel inventoryConfiguration;
	private BaseModel entityObject; 
	private TransactionModel transaction;
	private List<BaseModel > orderedEntities; 
	
	private boolean regularTransaction; 
	
	private String imageData; 

}
