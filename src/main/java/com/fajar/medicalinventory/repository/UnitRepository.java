package com.fajar.medicalinventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fajar.medicalinventory.entity.Unit;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {
 
}
