package com.fajar.medicalinventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fajar.medicalinventory.entity.Supplier;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long>{

	
}

