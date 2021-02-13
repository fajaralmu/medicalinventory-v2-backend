package com.fajar.medicalinventory.querybuilder;

import static com.fajar.medicalinventory.util.EntityUtil.getClassAnnotation;
import static com.fajar.medicalinventory.util.EntityUtil.getDeclaredField;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import com.fajar.medicalinventory.annotation.FormField;
import com.fajar.medicalinventory.dto.KeyValue;
import com.fajar.medicalinventory.entity.BaseEntity;
import com.fajar.medicalinventory.util.EntityUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QueryUtil {
	
	
	public static final String DAY_SUFFIX = "-day";
	public static final String MONTH_SUFFIX = "-month";
	public static final String YEAR_SUFFIX = "-year";
	public static final String FILTER_DATE_DAY = "DAY";
	public static final String FILTER_DATE_MON1TH = "MONTH";
	public static final String FILTER_DATE_YEAR = "YEAR";
	
	//placeholders
	public static final String SQL_RAW_DATE_FILTER = " ${MODE}(`${TABLE_NAME}`.`${COLUMN_NAME}`) = ${VALUE} ";
	 
	public static Field getFieldByName(String name, List<Field> fields) {
		return EntityUtil.getObjectFromListByFieldName("name", name, fields);
	}
 
	public static String getColumnName(Field field) {
		log.info("get column Name " + field.getDeclaringClass() + " from " + field.getName());

		if (field.getAnnotation(Column.class) == null)
			return field.getName();
		String columnName = ((Column) field.getAnnotation(Column.class)).name();
		if (columnName == null || columnName.equals("")) {
			columnName = field.getName();
		}
		return columnName;
	}
	/**	 
	 * return keyValue of tableName and columnName of the referenced entity
	 * @param currentKey
	 * @param field
	 * @param actualColumnName
	 * @return
	 */
	public static KeyValue<String, String> checkIfJoinColumn(String currentKey, Field relativeToEntityClass, Field field, boolean actualColumnName) {
		 
		String multiKeyColumnName = getMultiKeyColumnName(currentKey);
		KeyValue<String, String> keyValue = new KeyValue<>();
		boolean isMultiKey 	= null != multiKeyColumnName; 
		boolean validColumn = false;
		
		if (field.getAnnotation(JoinColumn.class) != null || isMultiKey) { 

			try {
				Class<?> fieldClass		= field.getType();
				String joinTableName 	= getTableName(fieldClass); 
				String referenceFieldName = "";

				if (isMultiKey) {
					referenceFieldName = multiKeyColumnName;
					joinTableName = getTableName(relativeToEntityClass.getType()); 
					keyValue.setMultiKey(isMultiKey);
				}else {
					referenceFieldName = getOptionItemName(field);
				}

				Field 	referenceEntityField 	= getDeclaredField(fieldClass, referenceFieldName);
				String 	fieldColumnName 		= actualColumnName ? getColumnName(referenceEntityField)  : referenceFieldName;

				if (fieldColumnName == null || fieldColumnName.equals("")) {
					validColumn = false;
				}else {
					 
					keyValue.setKey(joinTableName);
					keyValue.setValue(fieldColumnName);
					validColumn = true;
				}
				
			} catch ( Exception e) {
				
				log.warn(e.getClass() + " " + e.getMessage() + " " + field.getType());
				e.printStackTrace(); 
				validColumn = false;
			}

		} else { 
			return null;
		} 
		
		keyValue.setValid(validColumn);
		 
		log.info("keyValue: {}", keyValue);
		return keyValue;
	}

	private static String getMultiKeyColumnName(String currentKey) {
		String[] multiKey 	= currentKey.split("\\.");
		boolean isMultiKey 	= multiKey.length == 2;
		if (isMultiKey) {
			log.info("Multi Key: {}", currentKey);
			log.info("key name: {}", multiKey[1]);
			return  multiKey[1]; 
		} 
		else {
			return null;
		}
	} 

	private static String getOptionItemName(Field field) {
		 
		Field modelField = BaseEntity.getModelField(field);
		if (null == modelField) {
			System.out.println("modelField "+field.getName()+" IS NULL ");
		}
		FormField formField = modelField.getAnnotation(FormField.class);
		String referenceFieldName = formField.optionItemName();
		return referenceFieldName;
	}
	
	public static String getTableName(Class<?> entityClass) {
		log.info("getTableName From entity class: " + entityClass.getCanonicalName());
		
		Table table = getClassAnnotation(entityClass, Table.class);

		if (table != null) {
			boolean tableNameExist = table.name() != null && !table.name().equals("");
			if (tableNameExist) {
				return table.name();
			}
		}
		return entityClass.getSimpleName().toLowerCase();
	}
	/**
	 * 
	 * @param <T>
	 * @param _class
	 * @return fields having BaseEntity superClass type and @JoinColumn annotation
	 */
	public static  <T extends BaseEntity> List<Field> getJoinColumnFields(Class<T> _class){
		List<Field> joinColumns = new ArrayList<>();
		
		List<Field> fields = EntityUtil.getDeclaredFields(_class);
		for (int i = 0; i < fields.size(); i++) {
			final Field field = fields.get(i);
			
			JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
			if(null != joinColumn && field.getType().getSuperclass().equals(BaseEntity.class)) {
				field.setAccessible(true);
				joinColumns.add(field);
			}
		}
		
		return joinColumns;
	}

}
