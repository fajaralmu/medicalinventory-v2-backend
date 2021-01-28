package com.fajar.medicalinventory.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.fajar.medicalinventory.entity.Supplier;

@Repository
public interface SupplierRepository extends CrudRepository<Supplier, Long>{

	
}

