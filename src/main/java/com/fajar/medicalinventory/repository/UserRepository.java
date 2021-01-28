package com.fajar.medicalinventory.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fajar.medicalinventory.entity.User;

public interface UserRepository extends JpaRepository< User	, Long> {

	User findByUsername(String username);

	User findTop1ByUsername(String username);
	
	/**
	 * 
	 * @param authorityType {@linkplain com.fajar.medicalinventory.constants.entity.AuthorityType}
	 * @return
	 */
	@Query(value="select * from users left join user_authority " + 
			"on users.id = user_authority.user_id " + 
			"left join authority on authority.id = user_authority.authority_id " + 
			"where authority.name = ?1", nativeQuery = true)
    List<User> getByAuthority(String authorityType);
	
}