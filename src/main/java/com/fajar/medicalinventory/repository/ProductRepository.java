package com.fajar.medicalinventory.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.fajar.medicalinventory.entity.Product;
import com.fajar.medicalinventory.entity.Unit;
 

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {
	 
	List<Product> findByUnit(Unit unit);
	List<Product> findByName(String name);
	List<Product> findByUtilityTool(boolean isUtility);
}
