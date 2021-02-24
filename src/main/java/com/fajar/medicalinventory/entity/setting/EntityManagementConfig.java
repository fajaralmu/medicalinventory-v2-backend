package com.fajar.medicalinventory.entity.setting;

import java.io.Serializable;

import com.fajar.medicalinventory.annotation.CustomEntity;
import com.fajar.medicalinventory.annotation.Dto;
import com.fajar.medicalinventory.dto.model.BaseModel;
import com.fajar.medicalinventory.entity.BaseEntity;
import com.fajar.medicalinventory.exception.ApplicationException;
import com.fajar.medicalinventory.service.entity.BaseEntityUpdateService;
import com.fajar.medicalinventory.util.EntityUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder.Default;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EntityManagementConfig implements Serializable {
	private static final long serialVersionUID = -3980738115751592524L;
	private long id;
	@JsonIgnore
	private Class<? extends BaseEntity> entityClass;
	@JsonIgnore
	private BaseEntityUpdateService entityUpdateService;
	@JsonIgnore
	private String fieldName;
	private boolean disabled;
	private String iconClassName;
	@JsonIgnore
	@Default
	private Class<? extends BaseModel> modelClass = BaseModel.class;
	
	

	public EntityManagementConfig(String fieldName, Class<? extends BaseEntity> entityClass,
			BaseEntityUpdateService service, EntityUpdateInterceptor updateInterceptor) {
		this.entityClass = entityClass;
		this.entityUpdateService = service;
		if (null == fieldName) {
			fieldName = "entity";
		}
		this.fieldName = fieldName;
//		this.updateInterceptor = updateInterceptor;
		init();
	}
	
	public EntityManagementConfig setIconClassName(String iconClassName) {
		this.iconClassName = iconClassName;
		return this;
	}

	private void init() {
		CustomEntity customEntity = entityClass.getAnnotation(CustomEntity.class);
		if (null == customEntity) {
			throw new ApplicationException("NOT Custom Entity: "+ entityClass) ;
		}
		modelClass = BaseEntity.getModelClass(entityClass);
		
		Dto dtoAnnotation = modelClass.getAnnotation(Dto.class);
		if (null == dtoAnnotation) {
			throw new ApplicationException("NOT Custom Entity: "+ modelClass) ;
		}
		disabled = dtoAnnotation.editable() == false;
	}

	public String getLabel() {
		Dto dtoAnnotation = modelClass.getAnnotation(Dto.class);
		if (null == dtoAnnotation) {
			throw new ApplicationException("NOT Custom Entity: "+ modelClass) ;
		}
		String label = dtoAnnotation.value().equals("") ? entityClass.getSimpleName() : dtoAnnotation.value();
		return label;
	}

	public String getEntityName() {
		return entityClass.getSimpleName().toLowerCase();
	}

}
