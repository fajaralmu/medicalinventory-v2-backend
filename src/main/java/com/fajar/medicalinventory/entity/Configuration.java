package com.fajar.medicalinventory.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fajar.medicalinventory.annotation.CustomEntity;
import com.fajar.medicalinventory.dto.model.ConfigurationModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@CustomEntity(ConfigurationModel.class)
@Entity
@Table(name = "appplication_configuration")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Configuration extends BaseEntity<ConfigurationModel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5536800374303913968L;
	
	@Column(name="code", nullable = false, unique = true)
	private String code;
	@Column(name = "expired_warning_days")
	private int expiredWarningDays;
	@Column(name = "lead_time")
	private int leadTime;
	@Column(name = "cycle_time")
	private int cycleTime;

}
