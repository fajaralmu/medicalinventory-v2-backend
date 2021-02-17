package com.fajar.medicalinventory.util;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.SerializationUtils;

import com.fajar.medicalinventory.entity.Product;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MapUtil {

	public static <T> List<T> mappedListToList(Map<?,List<T>> mappedList) {
		List<T> result = new ArrayList<T>();

		for (Object key : mappedList.keySet()) {
			result.addAll((List<T>) mappedList.get(key));
		}

		return result;
	}
	 
	public static <T> List<T> mapValuesToList(Map<?, T> map) {
		List<T> result = new LinkedList<T>();

		for (Object key : map.keySet()) {
			result.add(map.get(key));
		}

		return result;
	}
	/*
	 * public static void printMap(Map map) { Log.log("printing map"); for(Object
	 * key:map.keySet()) { Log.log("key:", key,":",map.get(key)); } }
	 */

	public static <T> T mapToObject(Map<?, ?> map, Class<T> objectClass) {
		Set<?> mapKeys = map.keySet();
		try {
			Object result = objectClass.newInstance();

			for (Object key : mapKeys) {
				Object value = map.get(key);
				Field field = EntityUtil.getDeclaredField(objectClass, key.toString());

				try {

					if (value != null && field != null) {

						Class<?> fieldType = field.getType();
						boolean isEnum = fieldType.isEnum();

						/**
						 * mapValue is map
						 */
						if (value.getClass().equals(Map.class) || value.getClass().equals(LinkedHashMap.class)) {

							value = mapToObject((Map) value, fieldType);
						} else
						/**
						 * long
						 */
						if (objectEquals(fieldType, long.class, Long.class)) {
							value = Long.valueOf(value.toString());
						} else
						/**
						 * int
						 */
						if (objectEquals(fieldType, int.class, Integer.class)) {
							value = Integer.parseInt(value.toString());
						} else
						/**
						 * double
						 */
						if (objectEquals(fieldType, double.class, Double.class)) {
							value = Double.valueOf(value.toString());
						} else
						/**
						 * date from Long
						 */
						if (fieldType.equals(Date.class) && (objectEquals(value.getClass(), Long.class, long.class))) {
							value = new Date((Long) value);
						} else
						/**
						 * long from date
						 */
						if (objectEquals(fieldType, long.class, Long.class) && value.getClass().equals(Date.class)) {
							value = ((Date) value).getTime();
						} else
						/**
						 * ENUM
						 */
						if (isEnum) {
							value = Enum.valueOf((Class) fieldType, value.toString());
						}

						field.setAccessible(true);
						field.set(result, value);
					}
				} catch (Exception e) {
					String valueType = value == null ? "NULL" : value.getClass().toString();
					log.debug("fieldType: ", (field == null ? "NULL" : field.getType()), " value: ", value, "type:",
							valueType);
					e.printStackTrace();
					continue;
				}
			}

			return (T) result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
	
	public static void main(String[] args) {
		List<Product > list1 = new ArrayList<>();
		list1.add(Product.builder().name("NAME 1").build());
		List<Product > list2 = (List<Product>) SerializationUtils.clone((Serializable) list1);
		list2.get(0).setName("NAME 2");
		System.out.println(list1.get(0).getName());
		System.out.println(list2.get(0).getName());
		
	}

	public static boolean objectEquals(Object object, Object... objects) {

		for (Object object2 : objects) {
			if (object.equals(object2)) {
				return true;
			}
		}

		return false;
	}

	public static <K, V> HashMap<K, V> singleMap(K key, V value) {

		return new HashMap<K, V>() {
			private static final long serialVersionUID = 1150764585262310376L;
			{
				put(key, value);
			}

		};
	}
}
