package com.fajar.medicalinventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.medicalinventory.constants.AuthorityType;
import com.fajar.medicalinventory.entity.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

	Authority findTop1ByName(AuthorityType type);
 

}
