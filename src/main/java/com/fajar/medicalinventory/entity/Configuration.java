package com.fajar.medicalinventory.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fajar.medicalinventory.annotation.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Dto
@Entity
@Table(name = "appplication_configuration")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Configuration extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5536800374303913968L;
	
	@Column(name = "expired_warning_days")
	private int expiredWarningDays;
	@Column(name = "lead_time")
	private int leadTime;
	@Column(name = "cycle_time")
	private int cycleTime;

}
