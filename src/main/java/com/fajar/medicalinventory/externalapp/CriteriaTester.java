package com.fajar.medicalinventory.externalapp;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.persistence.Entity;

import org.apache.commons.io.FileUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.fajar.medicalinventory.dto.WebRequest;
import com.fajar.medicalinventory.entity.BaseEntity;
import com.fajar.medicalinventory.util.EntityUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CriteriaTester {
	final static String inputDir = "D:\\Development\\Eclipse\\universal-good-shop-v2\\src\\"
			+ "main\\java\\com\\fajar\\medicalinventory\\entity\\";
	final static String outputDir = "D:\\Development\\entities_json\\";
	// test
	static Session testSession;

	static ObjectMapper mapper = new ObjectMapper();
	static List<Class<?>> managedEntities = new ArrayList<>();

	public static void main2(String[] args) throws Exception {

		setSession();

		// String filterJSON =
		// "{\"entity\":\"product\",\"filter\":{\"exacts\":false,\"limit\":10,\"page\":0,\"fieldsFilter\":{\"withStock\":false,\"withSupplier\":false,\"withCategories\":false,\"category,id[EXACTS]\":\"4\",\"name\":\"\"},\"orderBy\":null,\"orderType\":null}}";
		String filterJSON = "{\"entity\":\"product\",\"filter\":{\"limit\":5,\"page\":9,\"orderBy\":null,\"orderType\":null,\"fieldsFilter\":{}}}";
		WebRequest request = mapper.readValue(filterJSON, WebRequest.class);
//		CriteriaBuilder cb = new CriteriaBuilder(testSession, Product.class, request.getFilter());
//		Criteria criteria = cb.createRowCountCriteria();
//
//		criteria.list();

	}

	public static List<Class> getIndependentEntities(List<Class<?>> managedEntities) {
		List<Class> independentEntities = new ArrayList<>();
		for (Class entityCLass : managedEntities) {
//			System.out.println("-"+entityCLass);
			List<Field> fields = EntityUtil.getDeclaredFields(entityCLass);
			int dependentCount = 0;
			for (Field field : fields) {
				dependentCount = dependentCount + (printDependentFields(field, managedEntities) ? 1 : 0);
			}
			if (dependentCount == 0) {
				independentEntities.add(entityCLass);
			}
		}

		return independentEntities;
	}

	public static List<Class<?>> getDependentEntities(List<Class<?>> managedEntities) {

		List<Class<?>> independentEntities = new ArrayList<>();
		for (Class entityCLass : managedEntities) {
//			System.out.println("-"+entityCLass);
			List<Field> fields = EntityUtil.getDeclaredFields(entityCLass);
			int dependentCount = 0;
			for (Field field : fields) {
				dependentCount = dependentCount + (printDependentFields(field, managedEntities) ? 1 : 0);
			}
			if (dependentCount > 0) {
				independentEntities.add(entityCLass);
			}
		}

		return independentEntities;
	}

	private static boolean printDependentFields(Field field, List<Class<?>> managedEntities) {

		for (Class<?> class3 : managedEntities) {
			if (field.getType().equals(class3)) {
				return true;
			}
		}
		return false;
	}

	public static void main(String[] args) throws Exception {
		setSession();
		  
		System.exit(0);
	}

	static void insertRecords() throws Exception {
		List<Class<?>> entities = getDependentEntities(getDependentEntities(managedEntities));
		Transaction tx = testSession.beginTransaction();

		for (Class clazz : entities) {
			insertRecord(clazz);
		}
		tx.commit();
	}

	private static <T extends BaseEntity> List<T> getObjectListFromFiles(Class<T> clazz) throws Exception {
		List<T> result = new ArrayList<>();
		String dirPath = outputDir + "//" + clazz.getSimpleName();
		File file = new File(dirPath);
		String[] fileNames = file.list();
		int c = 0;
		if (fileNames == null)
			return result;
		for (String fileName : fileNames) {
			String fullPath = dirPath + "//" + fileName;
			File jsonFile = new File(fullPath);
			String content = FileUtils.readFileToString(jsonFile);
			T entity = (T) mapper.readValue(content, clazz);
			result.add(entity);
		}
		return result;
	}

	private static void insertRecord(Class clazz) throws Exception {

		System.out.println(clazz);
		List<BaseEntity> list = getObjectListFromFiles(clazz);
		int c = 0;
		for (BaseEntity entity : list) {
			try {
				testSession.save(entity);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// if (c > 50) return;
			c++;
			System.out.println(clazz + " " + c + "/" + list.size());
		}
	}

	public static void printRecords(Class<?> _class) throws Exception {
		System.out.println("================= " + _class.getSimpleName() + " ===============");
		Criteria criteria = testSession.createCriteria(_class);
		List list = criteria.list();
		for (int i = 0; i < list.size(); i++) {
			String JSON = (mapper.writeValueAsString(list.get(i)));
			System.out.println(_class.getSimpleName() + " - " + i);
			FileUtils.writeStringToFile(
					new File(outputDir + _class.getSimpleName() + "\\" + _class.getSimpleName() + "_" + i + ".json"),
					JSON);
		}
	}

	static void setSession() {

		org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();
		configuration.setProperties(additionalPropertiesPostgres());

		managedEntities = getManagedEntities();
		for (Class class1 : managedEntities) {
			configuration.addAnnotatedClass(class1);
		}

		SessionFactory factory = configuration./* setInterceptor(new HibernateInterceptor()). */buildSessionFactory();
		testSession = factory.openSession();
	}

	static List<Class<?>> getManagedEntities() {
		List<Class<?>> returnClasses = new ArrayList<>();
		List<String> names = TypeScriptModelCreators.getJavaFiles(inputDir);
		List<Class> classes = TypeScriptModelCreators.getJavaClasses("com.fajar.medicalinventory.entity", names);
		for (Class class1 : classes) {
			if (null != class1.getAnnotation(Entity.class)) {
				returnClasses.add(class1);
			}
		}
		return returnClasses;
	}

	private static Properties additionalProperties() {

		String dialect = "org.hibernate.dialect.MySQLDialect";
		String ddlAuto = "update";

		Properties properties = new Properties();
		properties.setProperty("hibernate.dialect", dialect);
		properties.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/goodshopv2");
		properties.setProperty("hibernate.connection.username", "root");
		properties.setProperty("hibernate.connection.password", "");

		properties.setProperty("hibernate.connection.driver_class", com.mysql.jdbc.Driver.class.getCanonicalName());
		properties.setProperty("hibernate.current_session_context_class", "thread");
		properties.setProperty("hibernate.show_sql", "true");
		properties.setProperty("hibernate.connection.pool_size", "1");
		properties.setProperty("hbm2ddl.auto", ddlAuto);

		return properties;
	}

	private static Properties additionalPropertiesPostgresOffline() {

		String dialect = "org.hibernate.dialect.PostgreSQLDialect";
		String ddlAuto = "update";

		Properties properties = new Properties();
		properties.setProperty("hibernate.dialect", dialect);
		properties.setProperty("hibernate.connection.url", "jdbc:postgresql://localhost:5432/universal_commerce");
		properties.setProperty("hibernate.connection.username", "postgres");
		properties.setProperty("hibernate.connection.password", "root");

		properties.setProperty("hibernate.connection.driver_class", org.postgresql.Driver.class.getCanonicalName());
		properties.setProperty("hibernate.current_session_context_class", "thread");
		properties.setProperty("hibernate.show_sql", "true");
		properties.setProperty("hibernate.connection.pool_size", "1");
		properties.setProperty("hbm2ddl.auto", ddlAuto);

		return properties;
	}

	private static Properties additionalPropertiesPostgres() {

		String dialect = "org.hibernate.dialect.PostgreSQLDialect";
		String ddlAuto = "update";

		Properties properties = new Properties();
		properties.setProperty("hibernate.dialect", dialect);
		properties.setProperty("hibernate.connection.url",
				"jdbc:postgresql://ec2-54-157-12-250.compute-1.amazonaws.com:5432/d1eu0qub2adiiv");
		properties.setProperty("hibernate.connection.username", "veqlrgwoojdelw");
		properties.setProperty("hibernate.connection.password",
				"d8b34a7856fb4ed5e56d082db5a62dd3b527dd848e95ce1e6a3652001a04f7fe");

		properties.setProperty("hibernate.connection.driver_class", org.postgresql.Driver.class.getCanonicalName());
		properties.setProperty("hibernate.current_session_context_class", "thread");
		properties.setProperty("hibernate.show_sql", "true");
		properties.setProperty("hibernate.connection.pool_size", "1");
		properties.setProperty("hbm2ddl.auto", ddlAuto);
		properties.setProperty("hibernate.temp.use_jdbc_metadata_defaults", "false");
		return properties;
	}
}
