package com.fajar.medicalinventory.dto.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.MappedSuperclass;

import org.springframework.beans.BeanUtils;

import com.fajar.medicalinventory.annotation.Dto;
import com.fajar.medicalinventory.annotation.FormField;
import com.fajar.medicalinventory.entity.BaseEntity;
import com.fajar.medicalinventory.exception.ApplicationException;
import com.fajar.medicalinventory.util.EntityUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@MappedSuperclass
@JsonInclude(value=Include.NON_NULL)
public abstract class BaseModel<E extends BaseEntity> implements Serializable {

	public BaseModel() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -64034238773261408L;
	@FormField(labelName = "Record Id")
	private Long id;
	@JsonIgnore
	private Date createdDate;
	@JsonIgnore
	private Date modifiedDate;
	@JsonIgnore
	private Date deleted;
	private List<String> nulledFields = new ArrayList<>();

	public E toEntity() {
		try {
			E instance = getEntityNewInstance();
			return copy(instance);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	protected E getEntityNewInstance() throws Exception {
		Dto dto = getClass().getAnnotation(Dto.class);
		Objects.requireNonNull(dto);
		Class<? extends BaseEntity> entityClass = this.getTypeArgument();
		E instance = (E) entityClass.newInstance();
		return instance;
	}

	@JsonIgnore
	public final Class<E> getTypeArgument() {
		Class<?> _class = getClass();
		java.lang.reflect.Type genericeSuperClass = _class.getGenericSuperclass();
		ParameterizedType parameterizedType = (ParameterizedType) genericeSuperClass;
		return (Class<E>) parameterizedType.getActualTypeArguments()[0];
	}

	public static <M extends BaseModel> Class getEntityClass(Class<M> _class) {
		try {
			if (BaseModel.class.equals(_class.getSuperclass())) {
				java.lang.reflect.Type genericeSuperClass = _class.getGenericSuperclass();
				ParameterizedType parameterizedType = (ParameterizedType) genericeSuperClass;
				return (Class) parameterizedType.getActualTypeArguments()[0];
			}
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
		return null;
	}

	List<Field> getObjectModelField() {
		List<Field> fields = EntityUtil.getDeclaredFields(getClass());
		List<Field> filtered = new ArrayList<>();
		for (Field field : fields) {
			if (field.getType().getSuperclass() == null)
				continue;
			if (field.getType().getSuperclass().equals(BaseModel.class)) {
				filtered.add(field);
			}
		}

		return filtered;
	}

	void setObjectModel(E e) throws Exception {
		Class<? extends BaseEntity> entityClass = e.getClass();
		Objects.requireNonNull(e);
		List<Field> fields = getObjectModelField();
		for (Field field : fields) {
			Object value = field.get(this);
			if (null == value || false == (value instanceof BaseModel))
				continue;
			String name = field.getName();
			Field entityField = EntityUtil.getDeclaredField(entityClass, name);
			if (null == entityField)
				continue;

			BaseEntity finalValue = ((BaseModel) value).toEntity();
			entityField.set(e, finalValue);
		}
	}

	protected E copy(E e, String... ignoredProperties) {
		try {
			setObjectModel(e);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		BeanUtils.copyProperties(this, e, ignoredProperties);
		return e;
	}

	public static <E extends BaseEntity, M extends BaseModel> List<M> toModels(List<E> entities) {
		List<M> models = new ArrayList<>();
		entities.forEach(e -> models.add((M) e.toModel()));
		return models;
	}

	public static Field getEntityField(Field field) {
		Class<? extends BaseModel> _modelClass = (Class<? extends BaseModel>) field.getDeclaringClass();
		Class<?> entityClass = getEntityClass(_modelClass);
		return EntityUtil.getDeclaredField(entityClass, field.getName());
	}

}
