package com.pkm.medicalinventory.repository;

import java.lang.reflect.Field;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pkm.medicalinventory.entity.BaseEntity;
import com.pkm.medicalinventory.entity.setting.EntityManagementConfig;
 
public interface EntityRepository {
	/**
	 * get entity configuration from map by entity code
	 * 
	 * @param key
	 * @return
	 */
	public EntityManagementConfig getConfig(String entityCode);
	/**
	 * save entity
	 * 
	 * @param <T>
	 * @param baseEntity
	 * @return
	 */
	public <T extends BaseEntity, ID> T save(T baseEntity);

	public <T extends BaseEntity> T savev2(T entity);

	public <T extends BaseEntity> boolean validateJoinColumn(T baseEntity);

	public List<Field> getJoinColumn(Class<? extends BaseEntity> clazz);

		/**
	 * find suitable repository (declared in this class) for given entity object
	 * 
	 * @param entityClass
	 * @return
	 */
	public <T extends BaseEntity> JpaRepository findRepo(Class<T> entityClass);
		

	/**
	 * find by id
	 * 
	 * @param clazz
	 * @param ID
	 * @return
	 */
	public <ID, T extends BaseEntity> T findById(Class<T> clazz, ID ID);

	// public <ID extends Serializable, T extends BaseEntity> T findByIdv2(Class<T>
	// _class, ID id) {
	// T result = databaseReader.getById(_class, id);
	//
	// return result;
	// }

	/**
	 * find all entity
	 * 
	 * @param clazz
	 * @return
	 */
	public <T extends BaseEntity> List<T> findAll(Class<T> clazz);

	/**
	 * delete entity by id
	 * 
	 * @param id
	 * @param class1
	 * @return
	 */
	public <T extends BaseEntity> boolean deleteById(Long id, Class<T> class1);
	public EntityManagementConfig getConfiguration(String key);

	public List findByKey(Class entityClass, Field idField, Object... objectArray);

}
