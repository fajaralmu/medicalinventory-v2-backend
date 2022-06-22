package com.pkm.medicalinventory.repository;

import com.pkm.medicalinventory.constants.AuthorityType;
import com.pkm.medicalinventory.entity.Authority;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

	Authority findTop1ByName(AuthorityType type);
 

}
