package com.fajar.medicalinventory.service.entity;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.medicalinventory.dto.WebResponse;
import com.fajar.medicalinventory.entity.BaseEntity;
import com.fajar.medicalinventory.entity.setting.EntityUpdateInterceptor;
import com.fajar.medicalinventory.entity.setting.MultipleImageModel;
import com.fajar.medicalinventory.entity.setting.SingleImageModel;
import com.fajar.medicalinventory.service.resources.ImageUploadService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CommonUpdateService extends BaseEntityUpdateService<BaseEntity> {

	@Autowired
	private ImageUploadService imageUploadService;
	@Override
	public WebResponse saveEntity(BaseEntity entity, boolean newRecord, HttpServletRequest httpServletRequest) {
		log.info("saving entity: {}", entity.getClass());
		entity = copyNewElement(entity, newRecord);

		validateEntityFields(entity, newRecord);
		
		if (entity instanceof SingleImageModel) {
			imageUploadService.uploadImage((SingleImageModel) entity);
		}
		if (entity instanceof MultipleImageModel) {
			if (newRecord) {
				imageUploadService.writeNewImages((MultipleImageModel) entity, httpServletRequest);
			}else {
				MultipleImageModel existing = (MultipleImageModel) entityRepository.findById(entity.getClass(), entity.getId());
				imageUploadService.updateImages((MultipleImageModel) entity, existing , httpServletRequest);
			}
		}
		
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
