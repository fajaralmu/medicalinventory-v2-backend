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
@JsonInclude(value = Include.NON_NULL)
public class WebResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8345271799535134609L;
	private Date date = new Date();
	@Setter(value = AccessLevel.NONE)
	private UserModel user; 
	private String code = "00";
	private String message = "success";
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
	
	public WebResponse() {
		//
	}
	
	public WebResponse(String code, String message) {
		this.code = code;
		this.message = message;
		this.date = new Date();
	}
	
	public void setUser(UserModel user) {
		user.setPassword(null);
		this.user = user;
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

	public WebResponse withUser(UserModel m) {
		setUser(m);
		return this;
	}

	public WebResponse withEntityProperty(EntityProperty config) {
		setEntityProperty(config);
		return this;
	}

	public WebResponse withGeneralList(List<Object> result) {
		setGeneralList(result);
		return this;
	}

	public WebResponse withEntity(BaseModel resp) {
		setEntity(resp);
		return this;
	}

	public WebResponse withEntityClass(Class<? extends BaseEntity> entityClass2) {
		setEntityClass(entityClass2);
		return this;
	}

	public WebResponse withEntities(List<BaseModel> models) {
		setEntities(models);
		return this;
	}

	public WebResponse withTotalData(int count) {
		setTotalData(count);
		return this;
	}

	public WebResponse withFilter(Filter filter2) {
		setFilter(filter2);
		return this;
	}

	public WebResponse withConfiguration(ConfigurationModel config) {
		setConfiguration(config);
		return this;
	}

	public WebResponse withAppProfile(ApplicationProfileModel profile) {
		setApplicationProfile(profile);
		return this;
	}

	public WebResponse withRequestId(String requestId2) {
		setRequestId(requestId2);
		return this;
	}

	public WebResponse withPercentage(double progress) {
		setPercentage(progress);
		return this;
	}

	public WebResponse withTransaction(TransactionModel rec) {
		setTransaction(rec);
		return this;
	}
	
	
}
