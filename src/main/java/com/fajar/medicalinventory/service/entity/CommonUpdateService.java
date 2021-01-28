package com.fajar.medicalinventory.service.entity;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.fajar.medicalinventory.dto.WebResponse;
import com.fajar.medicalinventory.entity.BaseEntity;
import com.fajar.medicalinventory.entity.setting.EntityUpdateInterceptor;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CommonUpdateService extends BaseEntityUpdateService<BaseEntity> {

	@Override
	public WebResponse saveEntity(BaseEntity entity, boolean newRecord, HttpServletRequest httoHttpServletRequest) {
		log.info("saving entity: {}", entity.getClass());
		entity = copyNewElement(entity, newRecord);

		validateEntityFields(entity, newRecord);
		interceptPreUpdate(entity);
		BaseEntity newEntity = entityRepository.save(entity);

		return WebResponse.builder().entity(newEntity).build();
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
