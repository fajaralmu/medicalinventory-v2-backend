package com.fajar.medicalinventory.entity;

import java.beans.Transient;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;

import org.hibernate.annotations.Type;
import org.springframework.beans.BeanUtils;

import com.fajar.medicalinventory.annotation.BaseField;
import com.fajar.medicalinventory.annotation.CustomEntity;
import com.fajar.medicalinventory.annotation.Dto;
import com.fajar.medicalinventory.annotation.FormField;
import com.fajar.medicalinventory.dto.model.BaseModel;
import com.fajar.medicalinventory.entity.setting.EntityUpdateInterceptor;
import com.fajar.medicalinventory.util.EntityUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.extern.slf4j.Slf4j;

@Dto
@Slf4j
@MappedSuperclass
public class BaseEntity<M extends BaseModel> implements Serializable{

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = 5713292970611528372L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@FormField
	@Type(type = "org.hibernate.type.LongType")
	@Column 
	@BaseField
	private Long id;
	
	@Column(name = "created_date")
//	@JsonIgnore
//	@FormField
	private Date createdDate;
	@Column(name = "modified_date")
//	@JsonIgnore
	private Date modifiedDate; 
	@Column(name = "deleted")
	@JsonIgnore
	private Date deleted;
	@javax.persistence.Transient
	private List<String> nulledFields = new ArrayList<>();
	
	public <T > boolean idEquals(T object) {
		Long id = ((BaseEntity) object).getId();
		if (null == getId() || null == id) return false;
		return getId().equals(id);
	}
	
	public List<String> getNulledFields() {
		return nulledFields;
	}

	public void setNulledFields(List<String> nulledFields) {
		this.nulledFields = nulledFields;
	}

	public Date getCreatedDate() { 
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	 public Date isDeleted() {
		return deleted;
	}
	 public void setDeleted(Date deleted) {
		this.deleted = deleted;
	}
	 
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@PrePersist
	private void prePersist() {
		if (this.createdDate == null) {
			this.createdDate = new Date();
		}
		this.modifiedDate = new Date();
	}
 
	public void validateUniqueKeys(List<BaseEntity> entities) {}
	
	@JsonIgnore
	@Transient
	public EntityUpdateInterceptor modelUpdateInterceptor() {
		return new EntityUpdateInterceptor<BaseEntity>() {
			@Override
			public BaseEntity preUpdate(BaseEntity object) {
				return object;
			}
		};
	}
	
	public void validateNullValues () {
		for (int i = 0; i < this.nulledFields.size(); i++) {
			String fieldName = this.nulledFields.get(i);
			try {
				Field field = EntityUtil.getDeclaredField(getClass(), fieldName);
				field.set(this, null);
				log.info("Set {} NULL", field.getName());
			} catch (Exception e) {
			}
		}
	}
	
	public static <T extends BaseEntity> List<Long> getIdList(List<T> list) {
		
		List<Long> idList = new ArrayList<>();
		for (T object : list) {
			idList.add(object.getId());
		}
		return idList ;
	}
	
	public M toModel() {
		try {
			M instance = getEntityNewInstance();
			return copy(instance);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected M getEntityNewInstance() throws Exception {
		CustomEntity customEntity = getClass().getAnnotation(CustomEntity.class);
		Objects.requireNonNull(customEntity);
		Class<? extends BaseModel> entityClass = customEntity.value();
		M instance = (M) entityClass.newInstance();
		return instance;
	}
	
	List<Field> getObjectModelField() {
		List<Field> fields = EntityUtil.getDeclaredFields(getClass());
		List<Field> filtered = new ArrayList<>();
		for (Field field : fields) {
			if (field.getType().getSuperclass() == null) continue;
			if (field.getType().getSuperclass().equals(BaseEntity.class)) {
				filtered.add(field);
			}
		}
		
		return filtered;
	}
	
	void setObjectModel(M e) throws  Exception {
		Class<? extends BaseModel> entityClass = e.getClass();
		Objects.requireNonNull(e);
		List<Field> fields = getObjectModelField();
		for (Field field : fields) {
			Object value = field.get(this);
			if (null == value || false == (value instanceof BaseEntity)) continue;
			String name = field.getName();
			Field entityField = EntityUtil.getDeclaredField(entityClass, name);
			if (null == entityField) continue;
			
			BaseModel finalValue = ((BaseEntity) value).toModel();
			entityField.set(e, finalValue);
		}
	}
	
	protected M copy(M e, String...ignoredProperties) {
		try {
			setObjectModel(e);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		BeanUtils.copyProperties(this, e, ignoredProperties);
		return e;
	}
}
