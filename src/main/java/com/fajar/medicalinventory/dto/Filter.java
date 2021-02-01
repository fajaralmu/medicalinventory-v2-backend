package com.fajar.medicalinventory.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fajar.medicalinventory.annotation.Dto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class Filter implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -5151185528546046666L;
	@Builder.Default
	private Integer limit = 0;
	@Builder.Default
	private Integer page = 0;
	private String orderType;
	private String orderBy; 
	@Builder.Default
	private boolean exacts = false;
	@Builder.Default
	private Integer day = 0;
	@Builder.Default
	private Integer year = 0;
	@Builder.Default
	private Integer month = 0; 
	@Builder.Default
	private Map<String, Object> fieldsFilter = new HashMap<>();
	
	private Integer monthTo;
	private Integer yearTo; 
	
	private boolean ignoreEmptyValue;
	
	@JsonIgnore
	private int maxValue;

}
