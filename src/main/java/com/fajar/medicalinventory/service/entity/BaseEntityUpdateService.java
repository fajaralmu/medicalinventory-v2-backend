package com.fajar.medicalinventory.service.entity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.medicalinventory.annotation.FormField;
import com.fajar.medicalinventory.annotation.StoreValueTo;
import com.fajar.medicalinventory.dto.WebResponse;
import com.fajar.medicalinventory.entity.BaseEntity;
import com.fajar.medicalinventory.entity.User;
import com.fajar.medicalinventory.entity.setting.EntityUpdateInterceptor;
import com.fajar.medicalinventory.repository.EntityRepository;
import com.fajar.medicalinventory.service.LogProxyFactory;
import com.fajar.medicalinventory.service.SessionValidationService;
import com.fajar.medicalinventory.service.resources.FileService;
import com.fajar.medicalinventory.util.CollectionUtil;
import com.fajar.medicalinventory.util.EntityUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BaseEntityUpdateService<T extends BaseEntity> {

	@Autowired
	protected FileService fileService;
	@Autowired
	protected EntityRepository entityRepository;
	@Autowired
	private SessionValidationService sessionValidationService;
	
	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	public WebResponse saveEntity(T baseEntity, boolean newRecord, HttpServletRequest httoHttpServletRequest) throws Exception {
		log.error("saveEntity Method not implemented");
		return WebResponse.failed("method not implemented");
	}

	protected T copyNewElement(T source, boolean newRecord) {
		try {
			return (T) EntityUtil.copyFieldElementProperty(source, source.getClass(), !newRecord);
		}catch (Exception e) {
			log.error("Error copy new element");
			e.printStackTrace();
			return source;
		}
	}

	protected List<String> removeNullItemFromArray(String[] array) {
		List<String> result = new ArrayList<>();
		for (String string : array) {
			if (string != null) {
				result.add(string);
			}
		}
		return result;

	}
	
	protected User getLoggedUser(HttpServletRequest httpServletRequest) {
		return sessionValidationService.getLoggedUser(httpServletRequest);
	}
	
	protected EntityUpdateInterceptor<T> getUpdateInterceptor(T baseEntity){
		return baseEntity.modelUpdateInterceptor();
	}
	
	/**
	 * validate object properties' value
	 * 
	 * @param object
	 * @param newRecord
	 */
	protected void validateEntityFields(BaseEntity object, boolean newRecord) {
		log.info("validating entity: {} newRecord: {}", object.getClass(), newRecord);
		try {

			BaseEntity existingEntity = null;
			if (!newRecord) {
				existingEntity = (BaseEntity) entityRepository.findById(object.getClass(), object.getId());
				if (null == existingEntity) {
					throw new Exception("Existing Entity Not Found");
				}
				object.validateNullValues();
			}

			List<Field> fields = EntityUtil.getDeclaredFields(object.getClass());
			for (int i = 0; i < fields.size(); i++) {
				Field field = fields.get(i);

				try {
					FormField formfield = field.getAnnotation(FormField.class);
					if (null == formfield) {
						continue;
					}

					Object fieldValue = field.get(object);
					log.info("validating field: {}, type: {}", field.getName(), formfield.type());
					if (fieldValue == null) {
						log.info("!! Skipping null-valued field: {}", field.getName());
						continue;
					}
					switch (formfield.type()) {
					case FIELD_TYPE_IMAGE:
						
						boolean isUpdateRecord =  newRecord == false;
						
						if (isUpdateRecord &&  fieldValue.equals(field.get(existingEntity))) {
							Object existingImage = field.get(existingEntity);
							log.info("existingImage : {}", existingImage);
							if ( existingImage.equals(fieldValue)) {
								field.set(object, existingImage);
							}
						} else {
							String imageName = updateImage(field, object, formfield.iconImage());
							field.set(object, imageName);
						}
						break;
					case FIELD_TYPE_FIXED_LIST:
						
						if (formfield.multipleSelect()) {
							String storeToFieldName = field.getAnnotation(StoreValueTo.class).value(); 
							
							Field idField = CollectionUtil.getIDFieldOfUnderlyingListType(field);
							Field storeToField = EntityUtil.getDeclaredField(object.getClass(), storeToFieldName);
							
							Object[] valueAsArray = ((Collection) fieldValue).toArray(); 
							CharSequence[] actualFieldValue = new String[valueAsArray.length];
							
							for (int j = 0; j < valueAsArray.length; j++) {
								actualFieldValue[j] = String.valueOf(idField.get(valueAsArray[j]));
							}
							
							storeToField.set(object, String.join("~", actualFieldValue));
						}
						break;
					default:
						break;
					}
				} catch (Exception e) {
					log.error("Error validating field: {}", field.getName());
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			//
			log.error("Error validating entity {}", object.getClass().getSimpleName());
			e.printStackTrace();
		}
	}

	/**
	 * update image field, writing to disc
	 * 
	 * @param field
	 * @param object
	 * @return
	 */
	private String updateImage(Field field, BaseEntity object, boolean isIcon) {
		log.info("updating image {}", field.getName());
		try {
			Object base64Value = field.get(object);
			return writeImage(object, base64Value, isIcon);

		} catch (IllegalArgumentException | IllegalAccessException e) {

			e.printStackTrace();
		}
		return null;
	}

	private String writeImage(BaseEntity object, Object base64Value, boolean isIcon) {
		String fileName = null;
		if (null != base64Value && base64Value.toString().trim().isEmpty() == false) {
			try {
				if(isIcon) {
					fileName = fileService.writeIcon(object.getClass().getSimpleName(), base64Value.toString(), null);
				}else {
					fileName = fileService.writeImage(object.getClass().getSimpleName(), base64Value.toString());
				}
				
			} catch (Exception e) {
				log.error("Error writing image for {}", object.getClass().getSimpleName());
				e.printStackTrace();
			}
		}
		return fileName;
	}
}
