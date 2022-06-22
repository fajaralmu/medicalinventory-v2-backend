/*
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pkm.medicalinventory.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pkm.medicalinventory.annotation.CustomEntity;
import com.pkm.medicalinventory.dto.model.ProductModel;
import com.pkm.medicalinventory.entity.setting.MultipleImageModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author fajar
 */
@CustomEntity 
@Entity
@Table(name = "product")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product extends BaseEntity<ProductModel> implements MultipleImageModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4398862371443391887L;

	 
	@Column(unique = true) 
	private String code;
	@Column 
	private String name;
	@Column 
	private String description;
	@ManyToOne 
	@JoinColumn(name = "unit_id")
	private Unit unit; 
	@Column(name = "utility_tool")
	private boolean utilityTool;

	// web stuff

	@Column(name = "image_names") 
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
	private Double price;

	public void addCount(int count2) {
		if (count == null)
			count = 0;
		setCount(getCount() + count2);
	}

}
