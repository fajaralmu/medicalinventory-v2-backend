package com.pkm.medicalinventory.repository.main;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.persistence.JoinColumn;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.pkm.medicalinventory.annotation.CustomEntity;
import com.pkm.medicalinventory.annotation.Dto;
import com.pkm.medicalinventory.config.EntityRegistration;
import com.pkm.medicalinventory.dto.model.BaseModel;
import com.pkm.medicalinventory.entity.BaseEntity;
import com.pkm.medicalinventory.entity.setting.EntityManagementConfig;
import com.pkm.medicalinventory.entity.setting.EntityUpdateInterceptor;
import com.pkm.medicalinventory.management.IManagementService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EntityRepositoryImpl implements EntityRepository {
	@Autowired
	private SessionFactory sessionFactory;
	@Autowired
	private EntityRegistration entityReg;
	@Autowired
	private CustomRepository customRepository;
	@Autowired
	private ApplicationContext applicationContext;

	private final Map<String, EntityManagementConfig> entityConfiguration = new HashMap<String, EntityManagementConfig>();

	/**
	 * put configuration to entityConfiguration map
	 * 
	 * @param _class
	 * @param updateService
	 * @param updateInterceptor
	 */
	private <T extends BaseEntity> void putConfig(Class<T> _class, IManagementService<T> updateService,
			EntityUpdateInterceptor<?> updateInterceptor) {
		String key = _class.getSimpleName().toLowerCase();
		entityConfiguration.put(key, config(key, _class, updateService, updateInterceptor));
		log.info("put entity config, key: {} - class: {}", key, _class);
	}

	@PostConstruct
	public void init() throws Exception {
		putEntitiesConfig();
	}

	private void putEntitiesConfig() throws Exception {
		entityConfiguration.clear();

		List<Type> persistenceClasses = entityReg.getEntityClassess();
		log.info(">>>> persistenceClasses count: {}", persistenceClasses.size());
		for (Type type : persistenceClasses) {
			log.info("checking : {}", type);
			try {
				Class<? extends BaseEntity> entityClass = (Class<? extends BaseEntity>) type;
				CustomEntity customEntity = entityClass.getAnnotation(CustomEntity.class);
				if (null == customEntity) {
					log.info(" SKIP {}, cause = customEntity is null", type);
					continue;
				}
				Class<? extends BaseModel> modelClass = BaseEntity.getModelClass(entityClass);
				if (null == modelClass.getAnnotation(Dto.class)) {
					log.info(" SKIP {}, cause = {}'s Dto is null", type, modelClass);
					continue;
				}
				String beanName = modelClass.getAnnotation(Dto.class).managementService();
				// String beanName =
				// StringUtil.lowerCaseFirstChar(updateServiceClass.getSimpleName());

				IManagementService updateServiceBean = (IManagementService) applicationContext.getBean(beanName);
				EntityUpdateInterceptor updateInterceptor = ((BaseEntity) entityClass.newInstance())
						.modelUpdateInterceptor();

				log.info("Registering entity config: {}, updateServiceBean: {}", entityClass.getSimpleName(),
						updateServiceBean);

				putConfig(entityClass, updateServiceBean, updateInterceptor);
			} catch (Exception e) {
				log.error("Error registering entity: {}", type.getTypeName());
				e.printStackTrace();
			}

		}
		log.info("///////////// END PUT ENTITY CONFIGS: {} //////////////", entityConfiguration.size());
	}

	/**
	 * get entity configuration from map by entity code
	 * 
	 * @param key
	 * @return
	 */
	public EntityManagementConfig getConfig(String entityCode) {
		return entityConfiguration.get(entityCode);
	}

	private EntityManagementConfig config(
		String object, Class<? extends BaseEntity> class1,
		IManagementService service,
		EntityUpdateInterceptor interceptor
	) {
		return new EntityManagementConfig(object, class1, service, interceptor);
	}

	public <T extends BaseEntity> T save(T entity) {
		boolean relationValid = validateRelation(entity);
		if (!relationValid) {
			throw new InvalidParameterException("Entity Relation INVALID");
		}

		log.info("customRepository: {}", customRepository);
		DatabaseProcessor databatseProcessor = customRepository.createDatabaseProcessor();
		return databatseProcessor.save(entity);
	}

	private <T extends BaseEntity> boolean validateRelation(T baseEntity) {
		List<Field> joinColumns = getJoinColumn(baseEntity.getClass());
		if (joinColumns.size() == 0)
			return true;

		for (Field field : joinColumns) {
			try {
				field.setAccessible(true);
				Object value = field.get(baseEntity);
				if (value == null || (value instanceof BaseEntity) == false) {
					continue;
				}

				BaseEntity entity = (BaseEntity) value;
				BaseEntity result = findById(entity.getClass(), entity.getId());

				if (result == null)
					return false;
				
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}

		}

		return true;
	}

	private List<Field> getJoinColumn(Class<? extends BaseEntity> clazz) {
		List<Field> joinColumns = new ArrayList<>();
		Field[] fields = clazz.getFields();

		for (Field field : fields) {
			if (field.getAnnotation(JoinColumn.class) != null) {
				joinColumns.add(field);
			}
		}

		return joinColumns;
	}
	
	public <ID extends Serializable, T extends BaseEntity> T findById(Class<T> clazz, ID id) {
		log.info("find {} By Id: {}", clazz.getSimpleName(), id);
		Session session = null;
		try {
			session = sessionFactory.openSession();
			Object record = session.get(clazz, id);
			if (record != null && record.getClass().isInstance(clazz)) {
				return (T) record;
			}
			log.debug("{} is NULL", clazz.getSimpleName());
			return null;
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (session != null)
				session.close();
		}

	}
	public <T extends BaseEntity> List<T> findAll(Class<T> clazz) {
		log.info("find all {}", clazz.getSimpleName());
		Session session = null;
		try {
			session = sessionFactory.openSession();
			return session.createCriteria(clazz).list();
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (session != null)
				session.close();
		}
		
	}

	/**
	 * delete entity by id
	 * 
	 * @param id
	 * @param class1
	 * @return
	 */
	public <T extends BaseEntity> boolean deleteById(Long id, Class<T> class1) {
		log.info("Will delete entity: {}, id: {}", class1.getClass(), id);
		DatabaseProcessor databatseProcessor = customRepository.createDatabaseProcessor();
		return databatseProcessor.delete(class1, id);

	}
//	private <T extends BaseEntity> JpaRepository findRepo(Class<T> entityClass) {
//		JpaRepository repository = entityReg.getJpaRepository(entityClass);
//		return repository;
//	}
}
