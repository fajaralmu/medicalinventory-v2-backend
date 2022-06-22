package com.pkm.medicalinventory.dto.model;

import com.pkm.medicalinventory.annotation.Dto;
import com.pkm.medicalinventory.entity.Configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Dto()
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfigurationModel extends BaseModel<Configuration> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5536800374303913968L;
	
	private String code;
	private int expiredWarningDays;
	private int leadTime;
	private int cycleTime;
	@Override
	public Configuration toEntity() {
		Configuration entity = new Configuration();
		return null;
	}

}
