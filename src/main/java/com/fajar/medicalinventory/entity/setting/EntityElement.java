package com.fajar.medicalinventory.entity.setting;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Id;
import javax.persistence.JoinColumn;

import com.fajar.medicalinventory.annotation.AdditionalQuestionField;
import com.fajar.medicalinventory.annotation.BaseField;
import com.fajar.medicalinventory.annotation.Dto;
import com.fajar.medicalinventory.annotation.FormField;
import com.fajar.medicalinventory.constants.FieldType;
import com.fajar.medicalinventory.entity.BaseEntity;
import com.fajar.medicalinventory.util.CollectionUtil;
import com.fajar.medicalinventory.util.EntityUtil;
import com.fajar.medicalinventory.util.MyJsonUtil;
import com.fajar.medicalinventory.util.StringUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@AllArgsConstructor
@Dto
@Slf4j 
@JsonInclude(Include.NON_NULL)
public class EntityElement implements Serializable {

	@JsonIgnore
	final ObjectMapper objectMapper = new ObjectMapper();
	/**
	 * 
	 */
	private static final long serialVersionUID = -6768302238247458766L;

	public final boolean ignoreBaseField;
	public final boolean isGrouped;
	public final boolean editable;
	@JsonIgnore
	public final Field field;

	private String id;
	private String type;
	private String className;
	private String lableName;
	private String jsonList;
	private String optionItemName;
	private String optionValueName;
	private String entityReferenceName;
	private String entityReferenceClass;

	private String detailFields;
	private String inputGroupname;
	private String previewLink;
	private String[] defaultValues;

	private List<Object> plainListValues;
	private List<BaseEntity> options;

	private boolean identity;
	private boolean required;
	private boolean idField;
	private boolean skipBaseField;
	private boolean hasJoinColumn;
	private boolean multiple;
	private boolean showDetail;
	private boolean detailField;
	private boolean multipleSelect;
	private boolean hasPreview;

	@JsonIgnore
	public EntityProperty entityProperty; 
	@JsonIgnore
	private FormField formField;
	@JsonIgnore
	private BaseField baseField;
	@JsonIgnore
	public Map<String, List<?>> additionalMap;
	final FieldType fieldType;
 

	public EntityElement(Field field, EntityProperty entityProperty, Map<String, List<?>> additionalMap) {
		this.field = field;
		this.ignoreBaseField = entityProperty.isIgnoreBaseField();
		this.entityProperty = entityProperty;
		this.additionalMap = additionalMap;
		this.isGrouped = entityProperty.isQuestionare();
		this.formField = field.getAnnotation(FormField.class);
		
		if (formField!= null) {
			this.fieldType = formField.type();
			this.editable = formField.editable();
			setOptionItemName(formField.optionItemName());
			
		}else {
			this.fieldType = FieldType.FIELD_TYPE_TEXT;
			this.editable = false;
		}
		init();
	}

	private void init() {
		
		baseField = field.getAnnotation(BaseField.class);

		idField = field.getAnnotation(Id.class) != null;
		skipBaseField = !idField && (baseField != null && ignoreBaseField);

		identity = idField;
		hasJoinColumn = field.getAnnotation(JoinColumn.class) != null;
		

		checkIfGroupedInput();
	}
	
	public String getFieldTypeConstants() {
		try {
			return formField.type().toString();
		}catch (Exception e) {
			return null;
		}
	}

	public String getJsonListString(boolean removeBeginningAndEndIndex) {
		log.info("getJsonListString from json: {}", jsonList);
		try {
			String jsonStringified = objectMapper.writeValueAsString(jsonList).trim();
			if (removeBeginningAndEndIndex) {
				StringBuilder stringBuilder = new StringBuilder(jsonStringified);
				stringBuilder.setCharAt(0, ' ');
				stringBuilder.setCharAt(jsonStringified.length() - 1, ' ');
				jsonStringified = stringBuilder.toString().trim();
				log.info("jsonStringified: {}", jsonStringified);
			}
			jsonStringified = jsonStringified.replace("\\t", "");
			jsonStringified = jsonStringified.replace("\\r", "");
			jsonStringified = jsonStringified.replace("\\n", "");
			log.info("RETURN jsonStringified: {}", jsonStringified);
			return jsonStringified;
		} catch (Exception e) {
			return "{}";
		}
	}

	private void checkIfGroupedInput() {

		if (isGrouped) {
			AdditionalQuestionField annotation = field.getAnnotation(AdditionalQuestionField.class);
			inputGroupname = annotation != null ? annotation.value() : AdditionalQuestionField.DEFAULT_GROUP_NAME;
		}
	}

	public boolean build() throws Exception {
		boolean result = doBuild();
		setEntityProperty(null);
		return result;
	}
	
	public void setRequiredProp(FormField formField) {
		if (formField.type().equals(FieldType.FIELD_TYPE_CHECKBOX)) {
			setRequired(false);
		} else {
			setRequired(true);
		}
	}

	private boolean doBuild() throws Exception {

		boolean formFieldIsNullOrSkip = (formField == null || skipBaseField);
		if (formFieldIsNullOrSkip) {
			return false;
		}

		String lableName = formField.lableName().equals("") ? field.getName() : formField.lableName();
		FieldType determinedFieldType = determineFieldType();

		try {

			checkFieldType(determinedFieldType);
			boolean hasJoinColumn = field.getAnnotation(JoinColumn.class) != null;
			boolean collectionOfBaseEntity = CollectionUtil.isCollectionOfBaseEntity(field);

			if (hasJoinColumn || collectionOfBaseEntity) {
				processJoinColumn(determinedFieldType);
			}

			checkDetailField(); 
			setLableName(StringUtil.extractCamelCase(lableName));
			setType(determinedFieldType.value);
			
			setId(field.getName());
			setIdentity(idField);
			setRequiredProp(formField );
			setMultiple(formField.multipleImage());
			setClassName(field.getType().getCanonicalName());
			setShowDetail(formField.showDetail());
			

			setHasPreview(formField.hasPreview());
			if(isHasPreview()) {
				setPreviewLink(formField.previewLink());
			}
			
		} catch (Exception e1) {
			e1.printStackTrace();
			throw e1;
		}
		return true;
	}

	private void checkDetailField() {

		if (formField.detailFields().length > 0) {
			setDetailFields(String.join("~", formField.detailFields()));
		}
		if (formField.showDetail()) {
			setOptionItemName(formField.optionItemName());
			setDetailField(true);
		}
	}

	private void checkFieldType(FieldType fieldType) throws Exception {

		switch (fieldType) {
		case FIELD_TYPE_IMAGE:
			processImageType();
			break;
		case FIELD_TYPE_CURRENCY:
			processCurrencyType();
			break;
		case FIELD_TYPE_DATE:
			processDateType();
			break;
		case FIELD_TYPE_PLAIN_LIST:
			processPlainListType();
			break;
		case FIELD_TYPE_FIXED_LIST:
			if(formField.multipleSelect()) {
				processMultipleSelectElements();
			}
			break;
		default:
			break;

		}

	}
	
	private void processMultipleSelectElements() {
		entityProperty.getMultipleSelectElements().add(field.getName());
	}

	private void processCurrencyType() {
		entityProperty.getCurrencyElements().add(field.getName());
	}

	private void processImageType() {
		entityProperty.getImageElements().add(field.getName());
	}

	private void processDateType() {
		entityProperty.getDateElements().add(field.getName());
	}

	private void processPlainListType() throws Exception {
		log.info("Process Plain List Type: {}", field.getName());
		String[] availableValues = formField.availableValues();
		Object[] arrayOfObject = CollectionUtil.toObjectArray(availableValues);

		if (availableValues.length > 0) {
			setPlainListValues(Arrays.asList(arrayOfObject));

		} else if (field.getType().isEnum()) {
			Object[] enumConstants = field.getType().getEnumConstants();
			setPlainListValues(Arrays.asList(enumConstants));

		} else {
			log.error("Ivalid element: {}", field.getName());
			throw new Exception("Invalid Element");
		}
	}

	private FieldType determineFieldType() {

		FieldType fieldType;

		if (EntityUtil.isNumericField(field)) {
			fieldType = FieldType.FIELD_TYPE_NUMBER;

		} else if (field.getType().equals(Date.class) && field.getAnnotation(JsonFormat.class) == null) {
			fieldType = FieldType.FIELD_TYPE_DATE;

		} else if (idField) {
			fieldType = FieldType.FIELD_TYPE_HIDDEN;
		} else {
			fieldType = formField.type();
		}
		return fieldType;
	}

	private void processJoinColumn(FieldType fieldType) throws Exception {
		log.info("field {} of {} is join column, type: {}", field.getName(), field.getDeclaringClass(), fieldType);

		Class<?> referenceEntityClass = field.getType();
		Field referenceEntityIdField = EntityUtil.getIdFieldOfAnObject(field);

		if (referenceEntityIdField == null) {
			throw new Exception("ID Field Not Found");
		}

		if (fieldType.equals(FieldType.FIELD_TYPE_FIXED_LIST) && additionalMap != null) {

			List<BaseEntity> referenceEntityList = (List<BaseEntity>) additionalMap.get(field.getName());
			if (null == referenceEntityList || referenceEntityList.size() == 0) {
				throw new RuntimeException(
						"Invalid object list provided for key: " + field.getName() + " in EntityElement.AdditionalMap");
			}
			log.info("Additional map with key: {} . Length: {}", field.getName(), referenceEntityList.size());
			if (referenceEntityList != null) {
				setOptions(referenceEntityList);
				setJsonList(MyJsonUtil.listToJson(referenceEntityList));
			}
			

		} else if (fieldType.equals(FieldType.FIELD_TYPE_DYNAMIC_LIST)) {

//			setEntityReferenceClass(referenceEntityClass.getSimpleName());
		}
		
		setEntityReferenceClass(referenceEntityClass.getSimpleName());
		setOptionValueName(referenceEntityIdField.getName());
		setMultipleSelect(formField.multipleSelect());
		setOptionItemName(formField.optionItemName());
	}

}
