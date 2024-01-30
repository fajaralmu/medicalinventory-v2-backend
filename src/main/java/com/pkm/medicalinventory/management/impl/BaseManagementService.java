package com.pkm.medicalinventory.management.impl;

import org.springframework.stereotype.Service;

import com.pkm.medicalinventory.entity.BaseEntity;
import com.pkm.medicalinventory.entity.setting.EntityUpdateInterceptor;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BaseManagementService extends ManagementService<BaseEntity> {

	@Override
	public BaseEntity saveEntity(BaseEntity entity, boolean newRecord) {
		log.info("saving entity: {}", entity.getClass());
		entity = copyNewElement(entity, newRecord);
		
		validateEntityFormFields(entity, newRecord);
		
		interceptPreUpdate(entity);
		BaseEntity newEntity = entityRepository.save(entity);
		return newEntity;
	}

	/**
	 * execute things before persisting
	 * 
	 * @param entity
	 * @param updateInterceptor
	 */
	private void interceptPreUpdate(BaseEntity entity) {
		EntityUpdateInterceptor<BaseEntity> updateInterceptor = entity.modelUpdateInterceptor();
		if (null != updateInterceptor && null != entity) {
			log.info("Pre Update {}", entity.getClass().getSimpleName());
			try {
				updateInterceptor.preUpdate(entity);
				log.info("success pre update");
			} catch (Exception e) {

				log.error("Error pre update entity");
				e.printStackTrace();
				throw e;
			}
		}
	}

	
}