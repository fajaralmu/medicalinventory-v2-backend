package com.pkm.medicalinventory.config;

import java.lang.reflect.Type;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pkm.medicalinventory.entity.BaseEntity;

public interface EntityRegistration {

	List<Type> getEntityClassess();

	<T extends BaseEntity> JpaRepository getJpaRepository(Class<T> entityClass);

}
