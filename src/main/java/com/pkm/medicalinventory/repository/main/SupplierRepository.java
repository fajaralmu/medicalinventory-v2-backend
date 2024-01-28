package com.pkm.medicalinventory.repository.main;

import com.pkm.medicalinventory.entity.Supplier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long>{

	
}

