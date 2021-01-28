package com.fajar.medicalinventory.entity;

import static com.fajar.medicalinventory.constants.FieldType.FIELD_TYPE_TEXTAREA;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fajar.medicalinventory.annotation.Dto;
import com.fajar.medicalinventory.annotation.FormField;
import com.fajar.medicalinventory.constants.FieldType;
import com.fajar.medicalinventory.constants.FontAwesomeIcon;
import com.fajar.medicalinventory.entity.setting.SingleImageModel;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto(ignoreBaseField = false, updateService = "shopProfileUpdateService")
@Entity
@Table(name = "shop_profile")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationProfile extends BaseEntity  implements SingleImageModel{

	/**
	* 
	*/
	private static final long serialVersionUID = 4095664637854922384L;
	@Column 
	@FormField
	private String name;
	@Column(name = "mart_code", unique = true)
	@FormField(type = FieldType.FIELD_TYPE_HIDDEN)
	private String appCode;
	@Column(name = "short_description")
	@FormField(type = FIELD_TYPE_TEXTAREA)
	private String shortDescription;
	@Column
	@FormField(type = FIELD_TYPE_TEXTAREA)
	private String about;
	@Column(name = "welcoming_message")
	@FormField(type = FIELD_TYPE_TEXTAREA)
	private String welcomingMessage;
	@Column
	@FormField(type = FIELD_TYPE_TEXTAREA)
	private String address;

	@Column
	@FormField(type = FIELD_TYPE_TEXTAREA)
	private String contact;
	@Column
	@FormField
	private String website;
	@FormField(type = FieldType.FIELD_TYPE_IMAGE, required = false, defaultValue = "DefaultIcon.BMP")
	@Column(name = "icon_url")
	private String iconUrl;
	@FormField(type = FieldType.FIELD_TYPE_IMAGE, iconImage = true, required = false, defaultValue = "DefaultIcon.BMP")
	@Column(name = "page_icon_url")
	private String pageIcon;
	@FormField(type = FieldType.FIELD_TYPE_IMAGE, required = false, defaultValue = "DefaultBackground.BMP")
	@Column(name = "background_url")
	private String backgroundUrl;
	@FormField(type = FieldType.FIELD_TYPE_PLAIN_LIST, hasPreview = true, previewLink = "fa-preview" , required = false, defaultValue = "home")
	@Column(name= "footer_icon_class")
	@Enumerated(EnumType.STRING) 
	private FontAwesomeIcon footerIconClass; 
	
	@FormField(type = FieldType.FIELD_TYPE_COLOR, required = false, defaultValue = "#1e1e1e")
	@Column(name = "general_color")
	private String color;
	@FormField(type = FieldType.FIELD_TYPE_COLOR, required = false, defaultValue = "#f5f5f5")
	@Column(name = "font_color")
	private String fontColor;
	
	@Transient
	private String assetsPath;
	
	@JsonIgnore
	@Transient
	private String FooterIconClassValue;
	public String getFooterIconClassValue() {
		if(null == footerIconClass) {
			return "fa fa-home"; 
		}
		return footerIconClass.value;
	}
	@Override
	public void setImage(String image) {
		pageIcon = image;
		
	}
	@Override
	public String getImage() {
		return pageIcon;
	}
	
	

}
