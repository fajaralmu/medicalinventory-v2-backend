/*
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fajar.medicalinventory.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fajar.medicalinventory.annotation.Dto;
import com.fajar.medicalinventory.annotation.FormField;
import com.fajar.medicalinventory.constants.FieldType;
import com.fajar.medicalinventory.entity.setting.MultipleImageModel;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author fajar
 */
@Dto
@Entity
@Table(name = "product")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product extends BaseEntity implements MultipleImageModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4398862371443391887L;
	 
	@Column(unique = true)
	@FormField
	private String code;
	@Column
	@FormField
	private String name;
	@Column
	@FormField(type=FieldType.FIELD_TYPE_TEXTAREA)
	private String description;
	@ManyToOne
	@FormField(type = FieldType.FIELD_TYPE_FIXED_LIST, optionItemName = "name")
	@JoinColumn(name = "unit_id")
	private Unit unit;
	@FormField
	@Column(name = "utility_tool")
	private boolean utilityTool;

	// web stuff

	// private String namasatuan;
	@Transient
	@JsonIgnore
	private Integer jmlobat;
	@Transient
	@JsonIgnore
	private double stokaman;
	@Transient
	@JsonIgnore
	private double nextorder;
	@Transient
	@JsonIgnore
	private Integer hargasatuan;
	@Transient
	@JsonIgnore
	private Integer kumulatifpakai;
	@Transient
	@JsonIgnore
	private String kelas;
	@Transient
	@JsonIgnore
	private Integer pemakaian;
	@Transient
	@JsonIgnore
	private String keterangan;
	
	@Column(name="imahe_names")
	@FormField(multipleImage = true, type=FieldType.FIELD_TYPE_IMAGE)
	private String imageNames;
	@Override
	public void setImageNamesArray(String[] image) {

		this.imageNames = String.join("~", image);
	}
	
	@JsonIgnore
	@Override
	public String[] getImageNamesArray() {
		if (null == imageNames) {return new String[] {}; }

		return imageNames.split("~");
	}

 

}
