package com.fajar.medicalinventory.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fajar.medicalinventory.annotation.Dto;
import com.fajar.medicalinventory.dto.model.BaseModel;
import com.fajar.medicalinventory.entity.BaseEntity;
import com.fajar.medicalinventory.entity.setting.EntityElement;
import com.fajar.medicalinventory.entity.setting.EntityProperty;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class EntityPropertyBuilder {

	public static EntityProperty createEntityProperty(Class<? extends BaseModel> clazz, HashMap<String, List<?>> additionalObjectList)
			throws Exception {
		if (clazz == null ||EntityUtil. getClassAnnotation(clazz, Dto.class) == null) {
			return null;
		}

		Dto dto =  clazz.getAnnotation(Dto.class);
		Class<? extends BaseEntity> entityClass = dto.entityClass();
		final boolean ignoreBaseField = dto.ignoreBaseField(); 

		EntityProperty entityProperty = EntityProperty.builder().ignoreBaseField(ignoreBaseField)
				.modelClass(clazz)
				.entityName(entityClass.getSimpleName().toLowerCase())
				.withProgressWhenUpdated(dto.withProgressWhenUpdated())
				.creatable(dto.creatable())
				.build();
		try {

			List<Field> fieldList = EntityUtil.getDeclaredFields(clazz);
 
			List<EntityElement> entityElements = new ArrayList<>();
			List<String> fieldNames = new ArrayList<>();
			String fieldToShowDetail = "";

			for (Field field : fieldList) {

				final EntityElement entityElement = new EntityElement(field, entityProperty, additionalObjectList);

				if (false == entityElement.build()) {
					continue;
				}
				if (entityElement.isDetailField()) {
					fieldToShowDetail = entityElement.getId();
				}

				fieldNames.add(entityElement.getId());
				entityElements.add(entityElement);
			}

			entityProperty
					.setAlias(dto.value().isEmpty() ? StringUtil.extractCamelCase(clazz.getSimpleName()) : dto.value());
			entityProperty.setEditable(dto.editable());
			entityProperty.setElementJsonList();
			entityProperty.setElements(entityElements);
			entityProperty.setDetailFieldName(fieldToShowDetail);
			entityProperty.setDateElementsJson(MyJsonUtil.listToJson(entityProperty.getDateElements()));
			entityProperty.setFieldNames(MyJsonUtil.listToJson(fieldNames));
			entityProperty.setFieldNameList(fieldNames);
			entityProperty.setFormInputColumn(dto.formInputColumn().value);
			entityProperty.determineIdField();

			log.info("============ENTITY PROPERTY: {} ", entityProperty);

			return entityProperty;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}
}
