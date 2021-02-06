package com.fajar.medicalinventory.entity.setting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.fajar.medicalinventory.annotation.AdditionalQuestionField;
import com.fajar.medicalinventory.annotation.Dto;
import com.fajar.medicalinventory.dto.model.BaseModel;
import com.fajar.medicalinventory.entity.BaseEntity;
import com.fajar.medicalinventory.util.StringUtil;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Data 
@AllArgsConstructor
@Dto
@Slf4j
public class EntityProperty implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 2648801606702528928L;

	@Setter(value = AccessLevel.NONE)
	private String groupNames;
	private String entityName;
	private String alias;
	private String fieldNames;
	private String idField;
	private String detailFieldName; 

	private int formInputColumn; 
	private boolean editable = true; 
	private boolean creatable = true; 
	private boolean withDetail = false; 
	private boolean withProgressWhenUpdated = false;
 
	private List<EntityElement> elements = new ArrayList<>();
	private List<String> fieldNameList = new ArrayList<>();;

	private boolean ignoreBaseField;
	private boolean isQuestionare;

	final Class<? extends BaseModel> modelClass;

	public EntityProperty(Class<? extends BaseModel> modelClass) {
		this.modelClass = modelClass;

		Dto dto = modelClass.getAnnotation(Dto.class);
		Objects.requireNonNull(dto);
		Class<? extends BaseEntity> entityClass = dto.entityClass();
		
		setIgnoreBaseField(dto.ignoreBaseField());
		setEntityName(entityClass.getSimpleName().toLowerCase());
		setWithProgressWhenUpdated(dto.withProgressWhenUpdated());
		setCreatable(dto.creatable());
		setAlias(dto.value().isEmpty() ? StringUtil.extractCamelCase(entityClass.getSimpleName()) : dto.value());
		setEditable(dto.editable());
		setFormInputColumn(dto.formInputColumn().value);
	}

	public void setElementJsonList() {

//		this.dateElementsJson = MyJsonUtil.listToJson(dateElements);
//		this.imageElementsJson = MyJsonUtil.listToJson(imageElements);
//		this.currencyElementsJson = MyJsonUtil.listToJson(currencyElements);
//		this.multipleSelectElementsJson = MyJsonUtil.listToJson(multipleSelectElements);
	}

	public void setGroupNames(String[] groupNamesArray) {
		int removedIndex = 0;
		for (int i = 0; i < groupNamesArray.length; i++) {
			if (groupNamesArray[i] == AdditionalQuestionField.DEFAULT_GROUP_NAME) {
				removedIndex = i;
			}
		}
		groupNamesArray = ArrayUtils.remove(groupNamesArray, removedIndex);
		this.groupNames = String.join(",", groupNamesArray);
		groupNames += "," + AdditionalQuestionField.DEFAULT_GROUP_NAME;
	}

//	static void main(String[] args) {
//		args =new String[] {"OO", "ff", "fff22"};
//		for (int i = 0; i < args.length; i++) {
//			if(args[i] == "OO")
//		}
//	}

	public String getGridTemplateColumns() {
		if (formInputColumn == 2) {
			return "20% 70%";
		}
		return StringUtils.repeat("auto ", formInputColumn);
	}

	public void determineIdField() {
		if (null == elements) {
			log.error("Entity ELements is NULL");
			return;
		}
		for (EntityElement entityElement : elements) {
			if (entityElement.isIdField() && getIdField() == null) {
				setIdField(entityElement.getId());
			}
		}
	}

}
