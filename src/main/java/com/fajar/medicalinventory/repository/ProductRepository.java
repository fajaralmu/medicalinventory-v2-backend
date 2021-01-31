package com.fajar.medicalinventory.repository;
 
import java.math.BigInteger;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fajar.medicalinventory.entity.Product;
import com.fajar.medicalinventory.entity.Unit;
 

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	 
	List<Product> findByUnit(Unit unit);
	List<Product> findByName(String name);
	List<Product> findByUtilityTool(boolean isUtility);
	List<Product> findByOrderByName(Pageable of);
	@Query("select count(p) from Product p")
	BigInteger countAll();
}
