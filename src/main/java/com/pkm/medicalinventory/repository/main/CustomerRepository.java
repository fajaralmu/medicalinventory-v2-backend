package com.pkm.medicalinventory.repository.main;

import com.pkm.medicalinventory.entity.Customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>{
 
}
