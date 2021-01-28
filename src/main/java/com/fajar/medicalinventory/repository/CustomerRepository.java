package com.fajar.medicalinventory.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.fajar.medicalinventory.entity.Customer;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long>{
 
}
