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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA)
	private String description;
	@ManyToOne
	@FormField(type = FieldType.FIELD_TYPE_DYNAMIC_LIST, optionItemName = "name")
	@JoinColumn(name = "unit_id")
	private Unit unit;
	@FormField(type = FieldType.FIELD_TYPE_CHECKBOX)
	@Column(name = "utility_tool")
	private boolean utilityTool;

	// web stuff

	@Column(name = "image_names")
	@FormField(multipleImage = true, type = FieldType.FIELD_TYPE_IMAGE)
	private String imageNames;

	@JsonIgnore
	@Override
	public void setImageNamesArray(String[] image) {

		this.imageNames = String.join("~", image);
		System.out.println("imageNamesArray: " + imageNames);
	}

	@JsonIgnore
	@Override
	public String[] getImageNamesArray() {
		if (null == imageNames) {
			return new String[] {};
		}

		return imageNames.split("~");
	}

	@Transient
	@JsonIgnore
	private Integer count;
	@Transient
	@JsonIgnore
	private Integer price;

	@Transient
	@JsonIgnore
	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private double stokaman;
	@Transient
	@JsonIgnore
	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private double nextorder;

	@Transient
	@JsonIgnore
	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private Integer kumulatifpakai;
	@Transient
	@JsonIgnore
	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private String kelas;
	@Transient
	@JsonIgnore
	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private Integer pemakaian;
	 

	public double getStokaman() {
		return stokaman;
	}

	public void setStokaman(double stokaman) {
		this.stokaman = stokaman;
	}

	public double getNextorder() {
		return nextorder;
	}

	public void setNextorder(double nextorder) {
		this.nextorder = nextorder;
	}

	public Integer getKumulatifpakai() {
		return kumulatifpakai;
	}

	public void setKumulatifpakai(Integer kumulatifpakai) {
		this.kumulatifpakai = kumulatifpakai;
	}

	public String getKelas() {
		return kelas;
	}

	public void setKelas(String kelas) {
		this.kelas = kelas;
	}

	public Integer getPemakaian() {
		return pemakaian;
	}

	public void setPemakaian(Integer pemakaian) {
		this.pemakaian = pemakaian;
	} 

	public void addCount(int count2) {
		if (count == null)
			count = 0;
		setCount(getCount() + count2);
	}

}
