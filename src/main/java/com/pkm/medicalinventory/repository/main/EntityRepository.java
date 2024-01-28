package com.pkm.medicalinventory.repository.main;

import java.io.Serializable;
import java.util.List;

import com.pkm.medicalinventory.entity.BaseEntity;
import com.pkm.medicalinventory.entity.setting.EntityManagementConfig;
 
public interface EntityRepository {

	public EntityManagementConfig getConfig(String entityCode);
	
	public <T extends BaseEntity> T save(T entity);
	
	public <ID extends Serializable, T extends BaseEntity> T findById(Class<T> clazz, ID ID) ;

	public <T extends BaseEntity> boolean deleteById(Long id, Class<T> class1);

	public <T extends BaseEntity> List<T> findAll(Class<T> type);

}
