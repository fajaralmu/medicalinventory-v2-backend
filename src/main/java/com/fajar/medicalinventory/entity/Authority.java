package com.fajar.medicalinventory.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.fajar.medicalinventory.constants.AuthorityType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "authority")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Authority extends BaseEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2534190215509155334L;

	@Enumerated(EnumType.STRING)
	private AuthorityType name;

	public static Authority createAdmin() {
		// TODO Auto-generated method stub
		return Authority.builder().name(AuthorityType.ROLE_ADMIN).build();
	}
	public static Authority createUser() {
		// TODO Auto-generated method stub
		return Authority.builder().name(AuthorityType.ROLE_USER).build();
	}
}
