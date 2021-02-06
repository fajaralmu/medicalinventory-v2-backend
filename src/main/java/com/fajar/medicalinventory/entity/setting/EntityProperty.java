package com.fajar.medicalinventory.entity.setting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.fajar.medicalinventory.annotation.AdditionalQuestionField;
import com.fajar.medicalinventory.annotation.Dto;
import com.fajar.medicalinventory.dto.model.BaseModel;
import com.fajar.medicalinventory.util.MyJsonUtil;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
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
	

	private String imageElementsJson;
	private String dateElementsJson;
	private String multipleSelectElementsJson;
	private String currencyElementsJson;

	private int formInputColumn;
	@Default
	private boolean editable = true;
	@Default
	private boolean creatable = true;
	@Default
	private boolean withDetail = false;
	@Default
	private boolean withProgressWhenUpdated = false;

	@Builder.Default
	private List<String> dateElements = new ArrayList<String>();
	@Builder.Default
	private List<String> imageElements = new ArrayList<String>();
	@Builder.Default
	private List<String> currencyElements = new ArrayList<String>();
	@Builder.Default
	private List<String> multipleSelectElements = new ArrayList<String>();
	private List<EntityElement> elements;
	private List<String> fieldNameList;

	private boolean ignoreBaseField;
	private boolean isQuestionare;
	
	final Class<? extends BaseModel> modelClass;
	
	public EntityProperty(Class<? extends BaseModel> modelClass) {
		this.modelClass = modelClass;
	}

	public void setElementJsonList() {

		this.dateElementsJson = MyJsonUtil.listToJson(dateElements);
		this.imageElementsJson = MyJsonUtil.listToJson(imageElements);
		this.currencyElementsJson = MyJsonUtil.listToJson(currencyElements);
		this.multipleSelectElementsJson = MyJsonUtil.listToJson(multipleSelectElements);
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
