package com.pkm.medicalinventory.repository.main;

import com.pkm.medicalinventory.entity.Unit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {
 
}
