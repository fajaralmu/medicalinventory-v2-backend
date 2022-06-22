package com.pkm.medicalinventory.service.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.pkm.medicalinventory.annotation.FormField;
import com.pkm.medicalinventory.entity.BaseEntity;
import com.pkm.medicalinventory.repository.EntityRepository;
import com.pkm.medicalinventory.util.CollectionUtil;
import com.pkm.medicalinventory.util.EntityUtil;

import lombok.extern.slf4j.Slf4j;

//@Service
@Slf4j
public class EntityValidation {
//	@Autowired
//	private EntityRepository entityRepository;

//	public static void validateDefaultValues(List<? extends BaseEntity> entities, EntityRepository entityRepository) {
//		for (int i = 0; i < entities.size(); i++) {
//			validateDefaultValue(entities.get(i), entityRepository);
//		}
//	}

	private static void fillRequiredEntityValue(Map<Field, List<Long>> relatedObjectIdContainer,
			Map<Field, List<BaseEntity>> relatedObjectContainer, EntityRepository entityRepository) {
		
		for (Field collectionTypeField : relatedObjectIdContainer.keySet()) {
			
			Type underlyingType = CollectionUtil.getGenericTypes(collectionTypeField)[0];
			List<Long> idList = relatedObjectIdContainer.get(collectionTypeField);
			Field idField = EntityUtil.getIdFieldOfAnObject((Class) underlyingType);
			List<BaseEntity> listOfEntities = entityRepository.findByKey((Class<? extends BaseEntity>) underlyingType,
					idField, idList.toArray());
			
			log.info("listOfEntities: {}", listOfEntities.size());
			relatedObjectContainer.put(collectionTypeField, listOfEntities);
		}
		
	}

	public static <T extends BaseEntity> T validateDefaultValue(T baseEntity,
			Map<Field, List<Long>> relatedObjectIdContainer) {
		List<Field> fields = EntityUtil.getDeclaredFields(baseEntity.getClass());

		for (Field field : fields) {

			try {

				field.setAccessible(true);
				FormField formField = field.getAnnotation(FormField.class);
				Object objectValue = field.get(baseEntity);

				if (field.getType().equals(String.class) && formField != null
						&& formField.defaultValue().equals("") == false) {

					if (objectValue == null || objectValue.toString().equals("")) {
						field.set(baseEntity, formField.defaultValue());
					}

				} else if (formField != null && formField.multiply().length > 1) {

					if (objectValue != null)
						continue;

					Object newValue = "1";
					String[] multiplyFields = formField.multiply();

					loop: for (String multiplyFieldName : multiplyFields) {

						Field multiplyField = EntityUtil.getDeclaredField(baseEntity.getClass(), multiplyFieldName);

						if (multiplyField == null) {
							continue loop;
						}
						multiplyField.setAccessible(true);

						Object multiplyFieldValue = multiplyField.get(baseEntity);
						String strVal = "0";

						if (multiplyFieldValue != null) {
							strVal = multiplyFieldValue.toString();
						}

						if (field.getType().equals(Long.class)) {
							newValue = Long.parseLong(newValue.toString()) * Long.parseLong(strVal);

						} else if (field.getType().equals(Integer.class)) {
							newValue = Integer.parseInt(newValue.toString()) * Integer.parseInt(strVal);

						} else if (field.getType().equals(Double.class)) {
							newValue = Double.parseDouble(newValue.toString()) * Double.parseDouble(strVal);
						}

					}
					field.set(baseEntity, newValue);

//				} else if (formField != null && formField.multipleSelect()) {
//					String storeToFieldName = EntityUtil.getFieldAnnotation(field, StoreValueTo.class).value();
//					Field storeToField = EntityUtil.getDeclaredField(baseEntity.getClass(), storeToFieldName);
//
//					Object idValues = storeToField.get(baseEntity);
//
//					if (null == idValues)
//						continue;
//					if ("" != idValues.toString()) {
//						String[] rawIdentities = idValues.toString().split("~");
//
//						List<Long> idList = CollectionUtil.arrayToList(toLongArray(rawIdentities));
//						if (relatedObjectIdContainer.get(field) == null) {
//							relatedObjectIdContainer.put(field, new ArrayList<>());
//						}
//						relatedObjectIdContainer.get(field).addAll(idList);
//						log.info("relatedObjectIdContainer.get(field).addAll(idList); {}", idList);
//
//					}

				}
			} catch (Exception e) {
				log.error("Error validating field, will conitnue loop");
				e.printStackTrace();
			}
		}
		return baseEntity;
	}

	public static Long[] toLongArray(String[] strings) {
		Long[] longs = new Long[strings.length];

		for (int i = 0; i < strings.length; i++) {
			longs[i] = Long.valueOf(strings[i]);
		}

		return longs;
	}

}
