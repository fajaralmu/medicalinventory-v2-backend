package com.fajar.medicalinventory.dto.model;

import java.util.HashSet;
import java.util.Set;

import com.fajar.medicalinventory.annotation.Dto;
import com.fajar.medicalinventory.entity.Authority;
import com.fajar.medicalinventory.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserModel extends BaseModel<User>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3896877759244837620L;
	private String username;
	private String displayName;
	private String password;
	private String profileImage;

	@Default
	private Set<AuthorityModel> authorities = new HashSet<>();

	@JsonIgnore
	private String requestId; 

	@Override
	public User toEntity() {
		
		User user = super.toEntity();
		Set<Authority> _authorities = new HashSet<Authority>();
		this.authorities.forEach(a->{_authorities.add(a.toEntity());});
		user.setAuthorities(_authorities );
		return user;
	}
	
}
