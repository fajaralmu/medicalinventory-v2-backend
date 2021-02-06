package com.fajar.medicalinventory.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.summary.Product;

import com.fajar.medicalinventory.annotation.Dto;
import com.fajar.medicalinventory.dto.model.BaseModel;
import com.fajar.medicalinventory.dto.model.ProductModel;
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
		EntityProperty entityProperty = new EntityProperty(clazz);
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
			
			entityProperty.setElementJsonList();
			entityProperty.setElements(entityElements);
			entityProperty.setDetailFieldName(fieldToShowDetail);
//			entityProperty.setDateElementsJson(MyJsonUtil.listToJson(entityProperty.getDateElements()));
			entityProperty.setFieldNames(MyJsonUtil.listToJson(fieldNames));
			entityProperty.setFieldNameList(fieldNames);
			entityProperty.determineIdField();

			log.info("============ENTITY PROPERTY: {} ", entityProperty);

			return entityProperty;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}
	
	public static void main(String[] args) throws Exception {
		EntityProperty prop = createEntityProperty(ProductModel.class, null);
		
	}
}
