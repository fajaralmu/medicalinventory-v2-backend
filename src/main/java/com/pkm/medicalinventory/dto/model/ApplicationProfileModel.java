package com.pkm.medicalinventory.dto.model;

import static com.pkm.medicalinventory.constants.FieldType.FIELD_TYPE_TEXTAREA;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pkm.medicalinventory.annotation.Dto;
import com.pkm.medicalinventory.annotation.FormField;
import com.pkm.medicalinventory.constants.FieldType;
import com.pkm.medicalinventory.constants.FontAwesomeIcon;
import com.pkm.medicalinventory.entity.ApplicationProfile;

import lombok.Data;

@Dto(ignoreBaseField = false)
@Data
public class ApplicationProfileModel extends BaseModel<ApplicationProfile>  {

	/**
	* 
	*/
	private static final long serialVersionUID = 4095664637854922384L;
	
	public ApplicationProfileModel() {
		super();
	}
	 
	@FormField
	private String name;
	@FormField(type = FieldType.FIELD_TYPE_HIDDEN)
	private String appCode;
	@FormField(type = FIELD_TYPE_TEXTAREA)
	private String shortDescription;
	
	@FormField(type = FIELD_TYPE_TEXTAREA)
	private String about;
	@FormField(type = FIELD_TYPE_TEXTAREA)
	private String welcomingMessage;
	
	@FormField(type = FIELD_TYPE_TEXTAREA)
	private String address;

	
	@FormField(type = FIELD_TYPE_TEXTAREA)
	private String contact;
	
	@FormField
	private String website;
//	@FormField(type = FieldType.FIELD_TYPE_IMAGE, required = false, defaultValue = "DefaultIcon.BMP")
//	private String iconUrl;
//	@FormField(type = FieldType.FIELD_TYPE_IMAGE, iconImage = true, required = false, defaultValue = "DefaultIcon.BMP")
//	private String pageIcon;
//	@FormField(type = FieldType.FIELD_TYPE_IMAGE, required = false, defaultValue = "DefaultBackground.BMP")
//	private String backgroundUrl;
	@FormField(type = FieldType.FIELD_TYPE_PLAIN_LIST, hasPreview = true, 
//			previewLink = "fa-preview" , 
			required = false, defaultValue = "home")
	private FontAwesomeIcon footerIconClass; 
	
	@FormField(type = FieldType.FIELD_TYPE_COLOR, required = false, defaultValue = "#1e1e1e")
	
	private String color;
	@FormField(type = FieldType.FIELD_TYPE_COLOR, required = false, defaultValue = "#f5f5f5")
	
	private String fontColor;
	private String assetsPath;
	
	@JsonIgnore
	private String FooterIconClassValue;
	public String getFooterIconClassValue() {
		if(null == footerIconClass) {
			return "fa fa-home"; 
		}
		return footerIconClass.value;
	} 

}
