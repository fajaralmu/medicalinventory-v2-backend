package com.fajar.medicalinventory.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.fajar.medicalinventory.entity.Unit;

@Repository
public interface UnitRepository extends CrudRepository<Unit, Long> {
 
}
