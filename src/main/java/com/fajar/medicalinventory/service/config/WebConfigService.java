package com.fajar.medicalinventory.service.config;

import static com.fajar.medicalinventory.util.CollectionUtil.emptyArray;

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

import com.fajar.medicalinventory.config.LogProxyFactory;
import com.fajar.medicalinventory.entity.BaseEntity;
import com.fajar.medicalinventory.entity.User;
import com.fajar.medicalinventory.repository.AppProfileRepository;
import com.fajar.medicalinventory.util.CollectionUtil;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * this class is autowired via XML
 * 
 * @author Republic Of Gamers
 *
 */

@Data
@Slf4j
public class WebConfigService {
  
	private String uploadedImageRealPath;//, uploadedImagePath, reportPath;
	
	@Autowired
	private AppProfileRepository ProfileRepository;
	@Autowired
	private ApplicationContext applicationContext;
	

	private List<JpaRepository<?, ?>> jpaRepositories = new ArrayList<>();
	private List<Type> entityClassess = new ArrayList<>();
	private Map<Class<? extends BaseEntity>, JpaRepository> repositoryMap = new HashMap<>();
	private User defaultSuperAdminUser;

	@PostConstruct
	public void init() {
		log.info("WebConfigService INITIALIZE");

		getJpaReporitoriesBean();
		setLoggers();
	}

	private void setLoggers() {
		
		String[] beanNames = applicationContext.getBeanDefinitionNames();
		for (String string : beanNames) {
			Object beans = applicationContext.getBean(string);
			if (beans == null || !beans.getClass().getCanonicalName().startsWith("com.fajar")) continue;
			LogProxyFactory.setLoggers(beans);
			
		}
	}

	private void getJpaReporitoriesBean() {
		log.info("//////////////GET JPA REPOSITORIES BEANS///////////////");
		jpaRepositories.clear();
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

//			log.info("beanObject: {}", beanObject);
			if (null == interfaces)
				continue;

			Type type = getTypeArgument(interfaces[0], 0);

			entityClassess.add(type);
			jpaRepositories.add(beanObject);

			repositoryMap.put((Class) type, beanObject);

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

			if (emptyArray(typeArguments)) {
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

	public <T extends BaseEntity> JpaRepository getJpaRepository(Class<T> _entityClass) {
		log.info("get JPA Repository for: {}", _entityClass);

		JpaRepository result = this.repositoryMap.get(_entityClass);

		log.info("found repo object: {}", result);

		return result;
	}
	
	

	 

}
