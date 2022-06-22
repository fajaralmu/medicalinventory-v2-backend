package com.pkm.medicalinventory.dto;

import java.io.Serializable;
import java.util.List;

import com.pkm.medicalinventory.dto.model.ApplicationProfileModel;
import com.pkm.medicalinventory.dto.model.BaseModel;
import com.pkm.medicalinventory.dto.model.ConfigurationModel;
import com.pkm.medicalinventory.dto.model.CustomerModel;
import com.pkm.medicalinventory.dto.model.HealthCenterModel;
import com.pkm.medicalinventory.dto.model.ProductFlowModel;
import com.pkm.medicalinventory.dto.model.ProductModel;
import com.pkm.medicalinventory.dto.model.SupplierModel;
import com.pkm.medicalinventory.dto.model.TransactionModel;
import com.pkm.medicalinventory.dto.model.UnitModel;
import com.pkm.medicalinventory.dto.model.UserModel;
import com.pkm.medicalinventory.entity.ApplicationProfile;
import com.pkm.medicalinventory.entity.BaseEntity;
import com.pkm.medicalinventory.entity.Configuration;
import com.pkm.medicalinventory.entity.Product;
import com.pkm.medicalinventory.entity.ProductFlow;
import com.pkm.medicalinventory.entity.Supplier;
import com.pkm.medicalinventory.entity.Transaction;
import com.pkm.medicalinventory.entity.Unit;
import com.pkm.medicalinventory.entity.User;

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
