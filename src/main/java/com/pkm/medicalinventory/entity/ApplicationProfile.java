package com.pkm.medicalinventory.entity;

import static com.pkm.medicalinventory.constants.FieldType.FIELD_TYPE_TEXTAREA;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pkm.medicalinventory.annotation.CustomEntity;
import com.pkm.medicalinventory.annotation.Dto;
import com.pkm.medicalinventory.annotation.FormField;
import com.pkm.medicalinventory.constants.FieldType;
import com.pkm.medicalinventory.constants.FontAwesomeIcon;
import com.pkm.medicalinventory.dto.model.ApplicationProfileModel;
import com.pkm.medicalinventory.entity.setting.SingleImageModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto(ignoreBaseField = false, updateService = "shopProfileUpdateService")
@CustomEntity
@Entity
@Table(name = "shop_profile")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationProfile extends BaseEntity<ApplicationProfileModel>  implements SingleImageModel{

	/**
	* 
	*/
	private static final long serialVersionUID = 4095664637854922384L;
	@Column  
	private String name;
	@Column(name = "mart_code", unique = true)
	private String appCode;
	@Column(name = "short_description")
	private String shortDescription;
	@Column
	private String about;
	@Column(name = "welcoming_message")
	private String welcomingMessage;
	@Column
	private String address;

	@Column
	private String contact;
	@Column
	private String website;
	@Column(name = "icon_url")
	private String iconUrl;
	@Column(name = "page_icon_url")
	private String pageIcon;
	@Column(name = "background_url")
	private String backgroundUrl;
	@Column(name= "footer_icon_class")
	@Enumerated(EnumType.STRING) 
	private FontAwesomeIcon footerIconClass; 
	
	@Column(name = "general_color")
	private String color;
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
