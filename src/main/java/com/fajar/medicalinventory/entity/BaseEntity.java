package com.fajar.medicalinventory.entity;

import java.beans.Transient;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;

import org.apache.commons.lang3.SerializationUtils;
import org.hibernate.annotations.Type;
import org.springframework.beans.BeanUtils;

import com.fajar.medicalinventory.annotation.BaseField;
import com.fajar.medicalinventory.annotation.CustomEntity;
import com.fajar.medicalinventory.annotation.Dto;
import com.fajar.medicalinventory.annotation.FormField;
import com.fajar.medicalinventory.dto.model.BaseModel;
import com.fajar.medicalinventory.entity.setting.EntityUpdateInterceptor;
import com.fajar.medicalinventory.exception.ApplicationException;
import com.fajar.medicalinventory.util.EntityUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.extern.slf4j.Slf4j;

@Dto
@Slf4j
@MappedSuperclass
public class BaseEntity<M extends BaseModel> implements Serializable {

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = 5713292970611528372L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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

	public <T> boolean idEquals(T object) {
		Long id = ((BaseEntity) object).getId();
		if (null == getId() || null == id)
			return false;
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

	public void validateUniqueKeys(List<BaseEntity> entities) {
	}

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

	public void validateNullValues() {
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
		return idList;
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

	@JsonIgnore
	public final Class<M> getTypeArgument() {
		Class<?> _class = getClass();
		java.lang.reflect.Type genericeSuperClass = _class.getGenericSuperclass();
		ParameterizedType parameterizedType = (ParameterizedType) genericeSuperClass;
		return (Class<M>) parameterizedType.getActualTypeArguments()[0];
	}

	protected M getEntityNewInstance() throws Exception {
		CustomEntity customEntity = getClass().getAnnotation(CustomEntity.class);
		Objects.requireNonNull(customEntity);
		Class<? extends BaseModel> entityClass = getTypeArgument();
		M instance = (M) entityClass.newInstance();
		return instance;
	}

	private List<Field> getObjectModelField() {
		List<Field> filtered = new ArrayList<>();
		for (Field field : EntityUtil.getDeclaredFields(getClass())) {
			if (BaseEntity.class.equals(field.getType().getSuperclass())) {
				filtered.add(field);
			}
		}

		return filtered;
	}

	/**
	 * copy field having supperClass' type of baseEntity
	 * 
	 * @param e
	 * @throws Exception
	 */
	protected void setObjectModel(M e, String...ignoredProperties) throws Exception {
		Class<? extends BaseModel> modelClass = e.getClass();
		Objects.requireNonNull(e);
		List<Field> fields = getObjectModelField();
		List ignoredPropertiesList = Arrays.asList(ignoredProperties);
		for (Field field : fields) {
			if (ignoredPropertiesList.contains(field.getName())) continue;
			field.setAccessible(true);
			try {
				Object value = field.get(this);
				if (value instanceof Serializable) {
					value = SerializationUtils.clone((Serializable)value);
				}
			if (null == value || false == (value instanceof BaseEntity))
				continue;
			String name = field.getName();
			Field modelField = EntityUtil.getDeclaredField(modelClass, name);

			if (null == modelField) {
				
				continue;
			}

			BaseModel finalValue = ((BaseEntity) value).toModel();
			modelField.set(e, finalValue);
			} catch (Error x) {
				// TODO: handle exception
				System.out.println(getClass()+" ERRORRRR:"+field.getName()+ " "+x.getMessage());
//				x.printStackTrace();
				return;
			}
			
		}
		setFieldValuesHavingEntityFieldProp(e); 
	}

	private void setFieldValuesHavingEntityFieldProp(M model) throws IllegalArgumentException, IllegalAccessException { 
		List<Field> modelFields = EntityUtil.getDeclaredFields(model.getClass());
		for (Field modelField : modelFields) {
			
			try {
			FormField formField = modelField.getAnnotation(FormField.class);
			if(!BaseModel.class.equals(modelField.getType().getSuperclass()) || null == formField) continue;
			if (!formField.entityField().trim().isEmpty())
			{
				
				Field entityField = EntityUtil.getDeclaredField(getClass(), formField.entityField().trim());
				if (null != entityField &&  BaseEntity.class.equals(entityField.getType().getSuperclass()) ) {
					Object value = entityField.get(this);
					if (value instanceof Serializable) {
						value = SerializationUtils.clone((Serializable)value);
					}
					if (null != value) {
						BaseModel finalValue = ((BaseEntity) value).toModel();
						modelField.setAccessible(true);
						modelField.set(model, finalValue);
					}
				}
			}
			} catch (Error e) {
				// TODO: handle exception
				log.error("ERROR : {}", e.getMessage());
			}
		}
		 
	}


	protected M copy(M e, String... ignoredProperties) {
		try {
			setObjectModel(e, ignoredProperties);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		BeanUtils.copyProperties(this, e, ignoredProperties);
		e.setNulledFields(null);
		return e;
	}

	public static <T extends BaseEntity> Class getModelClass(Class<T> _class) {
		try {
			if (BaseEntity.class.equals(_class.getSuperclass())) {
				java.lang.reflect.Type genericeSuperClass = _class.getGenericSuperclass();
				ParameterizedType parameterizedType = (ParameterizedType) genericeSuperClass;
				return (Class) parameterizedType.getActualTypeArguments()[0];
			}
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
		return null;
	}

	public void preventStackOverFlowError() {

	}

	public static Field getModelField(Field entityField) {
		log.info("get model field for: {}", entityField.getName());
		Class<? extends BaseEntity> entityClass = (Class<? extends BaseEntity>) entityField.getDeclaringClass();
		Class modelClass = BaseEntity.getModelClass(entityClass);
		if (null == modelClass)
			return null;
		Field modelField = EntityUtil.getDeclaredField(modelClass, entityField.getName());
		return modelField;
	}

	public static Field getModelField(String fieldName, Class entityClass) {
		Class modelClass = BaseEntity.getModelClass(entityClass);
		if (null == modelClass)
			return null;
		Field modelField = EntityUtil.getDeclaredField(modelClass, fieldName);
		return modelField;
	}
}
