package com.fajar.medicalinventory.dto.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.MappedSuperclass;

import org.springframework.beans.BeanUtils;

import com.fajar.medicalinventory.annotation.Dto;
import com.fajar.medicalinventory.entity.BaseEntity;
import com.fajar.medicalinventory.util.EntityUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@MappedSuperclass
public abstract class BaseModel<E extends BaseEntity> implements Serializable{

	public BaseModel() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -64034238773261408L;
	private Long id;
	 
	private Date createdDate;
	 
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
		Class<? extends BaseEntity> entityClass = dto.entityClass();
		E instance = (E) entityClass.newInstance();
		return instance;
	}
	
	List<Field> getObjectModelField() {
		List<Field> fields = EntityUtil.getDeclaredFields(getClass());
		List<Field> filtered = new ArrayList<>();
		for (Field field : fields) {
			if (field.getType().getSuperclass() == null) continue;
			if (field.getType().getSuperclass().equals(BaseModel.class)) {
				filtered.add(field);
			}
		}
		
		return filtered;
	}
	
	void setObjectModel(E e) throws  Exception {
		Class<? extends BaseEntity> entityClass = e.getClass();
		Objects.requireNonNull(e);
		List<Field> fields = getObjectModelField();
		for (Field field : fields) {
			Object value = field.get(this);
			if (null == value || false == (value instanceof BaseModel)) continue;
			String name = field.getName();
			Field entityField = EntityUtil.getDeclaredField(entityClass, name);
			if (null == entityField) continue;
			
			BaseEntity finalValue = ((BaseModel) value).toEntity();
			entityField.set(e, finalValue);
		}
	}
	
	protected E copy(E e) {
		try {
			setObjectModel(e);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		BeanUtils.copyProperties(this, e);
		return e;
	}

	public static <E extends BaseEntity, M extends BaseModel> List<M> toModels(List<E> entities) {
		List<M> models = new ArrayList<>();
		entities.forEach(e->models.add((M) e.toModel()));
		return models ;
	}
	
}
