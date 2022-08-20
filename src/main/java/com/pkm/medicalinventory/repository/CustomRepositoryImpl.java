package com.pkm.medicalinventory.repository;

import java.lang.reflect.Field;

import com.pkm.medicalinventory.util.EntityUtil;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomRepositoryImpl {
	@Autowired
	private SessionFactory sessionFactory;

	public CustomRepositoryImpl() {
		log.info("-------CustomRepositoryImpl-----------");;
	}

	public DatabaseProcessor createDatabaseProcessor() {
		return new DatabaseProcessor(sessionFactory );
	}

	public static <O> O fillObject(Class<O> objectClass, Object[] propertiesArray, String[] propertyOrder)
			throws Exception {
		O object = objectClass.getDeclaredConstructor().newInstance();
		if (null == propertiesArray) 
		{
			return object;
		}
		for (int j = 0; j < propertiesArray.length; j++) {
			String propertyName = propertyOrder[j];
			Object propertyValue = propertiesArray[j];
			Field field = EntityUtil.getDeclaredField(object.getClass(), propertyName);
//			object.getClass().getDeclaredField(propertyName);
//	field.setAccessible(true);

			if (field != null && propertyValue != null) {
				final Class<?> fieldType = field.getType();
				log.info("Type: {} : {}", fieldType, propertyValue);

				if (fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
					propertyValue = Integer.parseInt(propertyValue.toString());
				} else if (field.getType().equals(Long.class) || fieldType.equals(long.class)) {
					propertyValue = Long.parseLong(propertyValue.toString());
				} else if (field.getType().equals(Double.class) || fieldType.equals(double.class)) {
					propertyValue = Double.parseDouble(propertyValue.toString());
				}

				field.set(object, propertyValue);
			}

		}
		return object;
	}
}
