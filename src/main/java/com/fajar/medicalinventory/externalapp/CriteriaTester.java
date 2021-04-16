package com.fajar.medicalinventory.externalapp;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;

import org.apache.commons.io.FileUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.fajar.medicalinventory.dto.Filter;
import com.fajar.medicalinventory.entity.BaseEntity;
import com.fajar.medicalinventory.entity.ProductFlow;
import com.fajar.medicalinventory.querybuilder.CriteriaBuilder;
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

	public static void main2(String[] args) {
		String queryString = "select sum(pf.count-pf.usedCount) from ProductFlow pf "
				+ " left join pf.transaction tx "
				+ " where tx.type = 'TRANS_OUT_TO_WAREHOUSE' "
				+ " and tx.healthCenterDestination.id = ? "
				+ " and (pf.count-pf.usedCount) > 0";
//		Session session = getSession();
		setSession();
		org.hibernate.Query q = testSession.createQuery(queryString);
		q.setParameter(0, 1442L);
		Object result = q.uniqueResult();
		System.out.println("RESULT: "+result);
	}
	
	public static void main (String[] args) throws Exception {

		setSession();

		// String filterJSON =
		// "{\"entity\":\"product\",\"filter\":{\"exacts\":false,\"limit\":10,\"page\":0,\"fieldsFilter\":{\"withStock\":false,\"withSupplier\":false,\"withCategories\":false,\"category,id[EXACTS]\":\"4\",\"name\":\"\"},\"orderBy\":null,\"orderType\":null}}";
		 
		Map<String, Object> fieldsFilter = new HashMap<String, Object>();
//		fieldsFilter.put("referenceProductFlow", "6013");
//		fieldsFilter.put("product.id", "1");
//		fieldsFilter.put("product", "1");
		fieldsFilter.put("transaction", "2020");
		fieldsFilter.put("transaction2", "2020");
//		fieldsFilter.put("count", "0");
		Filter filter = Filter.builder().orderBy("transaction2").exacts(true).limit(10). fieldsFilter(fieldsFilter ).build();
		CriteriaBuilder cb = new CriteriaBuilder(testSession, ProductFlow.class,  filter );
		Criteria criteria = cb.createCriteria();
	try {
			List list = criteria.list();
			list.forEach(System.out::println);
	}catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();
	}	
		testSession.close();
		System.exit(0);

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

		testSession = HibernateSessions.setSession();
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

	 
}
