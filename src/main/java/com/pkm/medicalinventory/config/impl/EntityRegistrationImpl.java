package com.pkm.medicalinventory.config.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.pkm.medicalinventory.config.EntityRegistration;
import com.pkm.medicalinventory.entity.BaseEntity;
import com.pkm.medicalinventory.util.CollectionUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EntityRegistrationImpl implements EntityRegistration {
	@Autowired
	private ApplicationContext applicationContext;

	private List<Type> entityClassess = new ArrayList<>();

	public List<Type> getEntityClassess() {
		return entityClassess;
	}

	@PostConstruct
	public void init() {
		log.info("Entity Registration INITIALIZE");
		getJpaReporitoriesBean();
	}

	private void getJpaReporitoriesBean() {
		log.info("//////////////GET JPA REPOSITORIES BEANS///////////////");
	 
		entityClassess.clear();
		String[] beanNames = applicationContext.getBeanNamesForType(JpaRepository.class);
		if (null == beanNames)
			return;

		log.info("JPA REPOSITORIES COUNT: " + beanNames.length);
		for (int i = 0; i < beanNames.length; i++) {
			String beanName = beanNames[i];
			JpaRepository<?, ?> beanObject = (JpaRepository<?, ?>) applicationContext.getBean(beanName);

			if (null == beanObject)
				continue;
			Class<?>[] interfaces = beanObject.getClass().getInterfaces();

			// log.info("beanObject: {}", beanObject);
			if (null == interfaces)
				continue;

			Type type = getTypeArgument(interfaces[0], 0);

			entityClassess.add(type);

			log.info(i + "." + beanName + ". entity type: " + type);
		}
	}

	private ParameterizedType getJpaRepositoryType(Class<?> _class) {
		Type[] genericInterfaces = _class.getGenericInterfaces();
		if (CollectionUtil.emptyArray(genericInterfaces))
			return null;

		try {
			for (int i = 0; i < genericInterfaces.length; i++) {
				Type genericInterface = genericInterfaces[i];
				if (genericInterface.getTypeName()
						.startsWith("org.springframework.data.jpa.repository.JpaRepository")) {
					return (ParameterizedType) genericInterface;
				}
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	private Type getTypeArgument(Class<?> _class, int argNo) {
		try {

			ParameterizedType jpaRepositoryType = getJpaRepositoryType(_class);

			Type[] typeArguments = jpaRepositoryType.getActualTypeArguments();// type.getTypeParameters();
			CollectionUtil.printArray(typeArguments);

			if (CollectionUtil.emptyArray(typeArguments)) {
				return null;
			}

			Type typeArgument = typeArguments[argNo];
			log.debug("typeArgument: {}", typeArgument);
			return typeArgument;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String readFile(String path) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(path));
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			String everything = sb.toString();
			return everything;
		} finally {
			br.close();
		}
	}
}
