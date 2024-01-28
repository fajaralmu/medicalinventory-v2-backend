package com.pkm.medicalinventory.config;

import java.lang.reflect.Type;
import java.util.List;

public interface EntityRegistration {

	List<Type> getEntityClassess();

	//<T extends BaseEntity> JpaRepository getJpaRepository(Class<T> entityClass);

}
