package com.pkm.medicalinventory.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pkm.medicalinventory.dto.model.ApplicationProfileModel;
import com.pkm.medicalinventory.dto.model.BaseModel;
import com.pkm.medicalinventory.dto.model.ConfigurationModel;
import com.pkm.medicalinventory.dto.model.HealthCenterModel;
import com.pkm.medicalinventory.dto.model.TransactionModel;
import com.pkm.medicalinventory.dto.model.UserModel;
import com.pkm.medicalinventory.entity.BaseEntity;
import com.pkm.medicalinventory.entity.setting.EntityProperty;
import com.pkm.medicalinventory.util.CollectionUtil;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class WebResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8345271799535134609L;
	@Builder.Default
	private Date date = new Date();
	private UserModel user; 
	@Builder.Default
	private String code = "00";
	@Builder.Default
	private String message = "success";
	@Builder.Default
	@Setter(value = AccessLevel.NONE)
	private List<BaseModel> entities = new ArrayList<>();
	
	private InventoryData inventoryData;
	private List<InventoryData> inventoriesData;
	private List<?> generalList;
	
	private BaseModel entity; 
	private Filter filter;
	/**
	 * total record
	 */
	private Integer totalData;
	private Integer totalItems;
	private EntityProperty entityProperty;
	
	private Long maxValue;
	private Integer quantity;
	private ApplicationProfileModel applicationProfile;
	private HealthCenterModel masterHealthCenter;
	private TransactionModel transaction;
	private ConfigurationModel configuration;

	private Double percentage;
	private Integer[] transactionYears;
	 
	private String requestId;  
 
	private Boolean loggedIn;
 
	@JsonIgnore
	private Class<? extends BaseEntity> entityClass; 
	
	public WebResponse(String code, String message) {
		this.code = code;
		this.message = message;
		this.date = new Date();
	}

	public <T extends BaseModel> void setEntities(List<T > entities) {
		this.entities = CollectionUtil.convertList(entities);
	}
	 
	public long getId() {
		return Thread.currentThread().getId();
	}
	
	//////////////////// statics ///////////////////
	
	public static WebResponse failedResponse() {
		return new WebResponse("01", "INVALID REQUEST");
	}

	

	public static WebResponse failed() {
		return failed("INVALID REQUEST");
	}

	public static WebResponse failed(Exception e) {
		return failed(e.getMessage());
	}

	public static WebResponse failed(String msg) {
		return new WebResponse("01", msg);
	}

	public static WebResponse success() {
		return new WebResponse("00", "SUCCESS");
	}

	public static WebResponse invalidSession() {
		return new WebResponse("02", "Invalid Session");
	}

	
	
	
}