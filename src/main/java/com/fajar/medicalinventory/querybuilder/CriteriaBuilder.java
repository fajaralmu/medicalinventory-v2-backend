package com.fajar.medicalinventory.querybuilder;

import static com.fajar.medicalinventory.querybuilder.QueryUtil.getFieldByName;
import static com.fajar.medicalinventory.util.EntityUtil.getDeclaredField;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.JoinColumn;
import javax.persistence.Transient;

import org.apache.commons.lang3.SerializationUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import com.fajar.medicalinventory.annotation.FormField;
import com.fajar.medicalinventory.dto.Filter;
import com.fajar.medicalinventory.dto.KeyValue;
import com.fajar.medicalinventory.dto.model.ProductFlowModel;
import com.fajar.medicalinventory.entity.BaseEntity;
import com.fajar.medicalinventory.util.EntityUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CriteriaBuilder {

	private final Session hibernateSession;

	private int joinIndex = 1;
	private final boolean allItemExactSearch;

	private final Class<? extends BaseEntity> entityClass;
	private final Filter filter;
	private final Criteria criteria;
	private final Map<String, Object> fieldsFilter;
	private final Map<String, Integer> aliases = new HashMap<>();
	private final Map<String, String> aliasesOtherNames = new HashMap<>();
	private final List<Field> entityDeclaredFields;

	private static final String THIS = "this";

	public CriteriaBuilder(Session hibernateSession, Class<? extends BaseEntity> _class, Filter filter) {
		this.hibernateSession = hibernateSession;
		this.entityClass = _class;
		this.filter = SerializationUtils.clone(filter);
		this.allItemExactSearch = filter.isExacts();
		this.criteria = this.hibernateSession.createCriteria(entityClass, entityClass.getSimpleName());
		this.fieldsFilter = filter.getFieldsFilter();
		this.entityDeclaredFields = EntityUtil.getDeclaredFields(entityClass);

		this.setJoinColumnAliases();
		log.info("=======CriteriaBuilder Field Filters: {}", fieldsFilter);
	}

	private Field getFieldByKeyName(String name) {
		return getFieldByName(name, entityDeclaredFields, entityClass);
	}
	
	/**
	 * 
	 * @param keyName raw path name
	 * @param fieldValue
	 * @return
	 */
	private Criterion restrictionEquals( String keyName, Object fieldValue) {
		System.out.println("Rest EQ: "+entityClass+" key: "+keyName);
		String entityName = entityClass.getSimpleName();
		String columnName;
		boolean multiKey = keyName.contains(".");
		Field field;
		 
		if (multiKey) {
			System.out.println("multiKey: "+multiKey+" "+keyName);
			Field foreignField = getFieldByKeyName(keyName.split("\\.")[0]);
			field = EntityUtil.getDeclaredField(foreignField.getType(), keyName.split("\\.")[1]);
			
			if (field.getAnnotation(Transient.class)!=null) return null;
			
			String alias = getAlias(foreignField.getName()) + "." + QueryUtil.getColumnName(field);
			return Restrictions.sqlRestriction(alias + "='" + fieldValue + "'");
		} else {
			field = getFieldByKeyName(keyName);// EntityUtil.getDeclaredField(entityClass, keyName);
			
			if (field==null||field.getAnnotation(Transient.class)!=null) return null;
			
			KeyValue joinColumnResult = QueryUtil.checkIfJoinColumn(keyName,field, field, true);
			
			if (null != joinColumnResult) {
				// process join column
				FormField formField = field.getAnnotation(FormField.class);
				keyName = getAlias(keyName) + "." + formField.optionItemName();
				return Restrictions.sqlRestriction(keyName + "='" + fieldValue + "'");
			}

			columnName = entityName + '.' + keyName;
		}

		if (field.getType().equals(String.class) == false) {
			return nonStringEqualsExp(keyName, fieldValue);
		}

		Object validatedValue = validateFieldValue(field, fieldValue);
		return Restrictions.naturalId().set(columnName, validatedValue);
	}

	private Criterion nonStringEqualsExp(String fieldName, Object value) {

		Criterion sqlRestriction = Restrictions.sqlRestriction("{alias}." + fieldName + " = '" + value + "'");
		return sqlRestriction;
	}

	private Object validateFieldValue(Field field, Object fieldValue) {
		if (null == fieldValue) {
			return 0;
		}

		Class<?> fieldType = field.getType();
		
		if (EntityUtil.isNumericField(field)) {
			if (fieldType.equals(Long.class) || fieldType.equals(long.class)) {
				long value = Long.valueOf(fieldValue.toString());
				return value;
			}
			if (fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
				long value = Integer.valueOf(fieldValue.toString());
				return value;
			}
		}
		return fieldValue;
	}

	private void setCurrentAlias(String aliasName, String path) {
		setCurrentAlias(aliasName, path, null);
	}
	/**
	 * 
	 * @param aliasName must match fieldName of entityClass
	 */
	private void setCurrentAlias(String aliasName, String path, String otherName) {
		if (null != otherName) {
			aliasesOtherNames.put(aliasName, otherName);
			return;
		}
//		 System.out.println("setCurrentAlias: "+aliasName);
		if (null == aliasName || aliases.get(aliasName) != null)
			return;

		if (aliasName.equals(THIS)) {
			// this.currentAlias = "this_";
		} else {

			Field correspondingField = EntityUtil.getDeclaredField(entityClass, path);
			if (null == correspondingField) {
				return;
			}
			aliases.put(aliasName, joinIndex);
			criteria.createAlias(entityClass.getSimpleName() + "." + path, aliasName, JoinType.LEFT_OUTER_JOIN);
			if (aliasName.length() > 10) {
				aliasName = aliasName.substring(0, 10);
			}

			// this.currentAlias = aliasName.toLowerCase() + joinIndex + '_';
			System.out.println("______________"+aliases);
			System.out.println();
			joinIndex++;
		}
	}

	private String getAlias(final String aliasName) {
		if (THIS.equals(aliasName) || aliasName.toLowerCase().equals(entityClass.getSimpleName().toLowerCase())) {
			return "this_";
		}
		String pathName;
		if (aliasesOtherNames.get(aliasName) != null) {
			pathName = aliasesOtherNames.get(aliasName);
		} else {
			pathName = aliasName;
		}
		String tableName = pathName;
		if (aliasName.length() > 10) {
			tableName = aliasName.substring(0, 10);
		}
		return tableName.toLowerCase() + aliases.get(aliasName) + "_";
	}

	private void setJoinColumnAliases() {
		List<QueryField> joinColumns = QueryUtil.getJoinColumnFieldsByBaseModel(getModelClass());
//		List<Field> joinColumns = QueryUtil.getJoinColumnFields(entityClass);
		for (int i = 0; i < joinColumns.size(); i++) {
			QueryField queryField =joinColumns.get(i);
			String alias = queryField.getModelField().getName();
			String path = queryField.getEntityField().getName();
			setCurrentAlias(alias, path, queryField.getOtherName());
		}

	}
//	private void setJoinColumnAliases() {
//		List<QueryField> joinColumns = QueryUtil.getJoinColumnFieldsByBaseModel(getModelClass());
////		List<Field> joinColumns = QueryUtil.getJoinColumnFields(entityClass);
//		for (int i = 0; i < joinColumns.size(); i++) {
//			QueryField queryField =joinColumns.get(i);
//			setCurrentAlias(joinColumns.get(i).getName());
//		}
//
//	}
	
	public static void main(String[] args) {
		List<QueryField> joinColumns = QueryUtil.getJoinColumnFieldsByBaseModel(ProductFlowModel.class);
		for (int i = 0; i < joinColumns.size(); i++) {
			QueryField q = joinColumns.get(i);
			System.out.println(q.getEntityField().getName()+" - "+q.getModelField().getName() );
		}
	}

	public Criteria createRowCountCriteria() {

		Criteria criteria = createCriteria(true);
		criteria.setProjection(Projections.rowCount());
		return criteria;
	}

	public Criteria createCriteria() {
		return createCriteria(false);
	}

	private Criteria createCriteria(boolean onlyRowCount) {

		String entityName = entityClass.getSimpleName();
		setCurrentAlias(THIS, null);

		for (final String rawKey : fieldsFilter.keySet()) {
			setCurrentAlias(THIS, null);

			log.info("##" + rawKey + ":" + fieldsFilter.get(rawKey));
			if (fieldsFilter.get(rawKey) == null)
				continue;

			String currentKey = rawKey; 
			log.info("Raw key: {} Now KEY: {}", rawKey, currentKey);
			// check if date
			Criterion dateFilterSql = getDateFilter(rawKey, currentKey);

			if (null != dateFilterSql) {
				log.info(" {} is date ", rawKey);
				addCriteria(dateFilterSql);
				continue;
			}

			final boolean multiKey = rawKey.contains(".");
			final Field field, fieldRelativeToEntityClass;
			if (multiKey) {
				fieldRelativeToEntityClass = getDeclaredField(entityClass, rawKey.split("\\.")[0]);
				field = getDeclaredField(fieldRelativeToEntityClass.getType(), rawKey.split("\\.")[1]);
			} else {
				field = fieldRelativeToEntityClass = getFieldByKeyName(currentKey);
			}

			if (field == null) {
				log.warn("Field Not Found :" + currentKey + " !");
				System.err.println("Field Not Found :" + currentKey + " !");
				continue;
			}

			String fieldName = field.getName();
			KeyValue<String, String> joinColumnResult = QueryUtil.checkIfJoinColumn(currentKey, fieldRelativeToEntityClass,field, false);
			System.out.println("joinColumnResult: "+joinColumnResult);
			
			//TODO: process exact with join column
			if (null != joinColumnResult) {
				if (joinColumnResult.isValid()) {
					Class<?> _class = field.getType();
					if (joinColumnResult.isMultiKey()) {
						fieldName = joinColumnResult.getKey();
						_class = fieldRelativeToEntityClass.getType();
					}
					if (allItemExactSearch) {
						Criterion eq = restrictionEquals( fieldName+ "." + joinColumnResult.getValue(), fieldsFilter.get(rawKey));
						addCriteria(eq);
					} else {
						Criterion like = restrictionLike( fieldName+ "." + joinColumnResult.getValue(), _class,
								fieldsFilter.get(rawKey));
						addCriteria(like);
					}
				} else {
					continue;
				}
			} else {
				if (allItemExactSearch) {
					Criterion eq = restrictionEquals(currentKey, fieldsFilter.get(rawKey));
					addCriteria(eq); 
				} else {
					Criterion like = (restrictionLike(entityName + "." + currentKey, entityClass, fieldsFilter.get(rawKey)));
					addCriteria(like);
				}
			}

		}

		if (onlyRowCount) {
			return criteria;
		}
		
		addOrderOffsetLimit(filter);  
		return criteria;

	}
	
	private void addCriteria(Criterion c) {
		if (null != c ) {
			criteria.add(c );
		}
	}

	private void addOrderOffsetLimit(Filter filter) {
		if (filter.getLimit() > 0) {
			criteria.setMaxResults(filter.getLimit());
			if (filter.getPage() > 0) {
				criteria.setFirstResult(filter.getPage() * filter.getLimit());
			}
		}
		String orderBy = extractOrderByKey(filter);
		if (null != orderBy) {

			Order order;
			if (filter.getOrderType()!=null && filter.getOrderType().toLowerCase().equals("desc")) {
				order = Order.desc(orderBy);
			} else {
				order = Order.asc(orderBy);
			}

			criteria.addOrder(order);
		}

	}

	private String extractOrderByKey(Filter filter) {
		String orderBy = filter.getOrderBy();
		Field field = getFieldByKeyName(orderBy);
		 
		if (null == field) {
			log.info("{} is not {} field", orderBy, entityClass);
			return null;
		}
		if (field.getAnnotation(Transient.class) != null) {
			log.info("{} is TRANSIENT FIELD", orderBy);
			return null;
		}
		if (field.getAnnotation(JoinColumn.class) != null) {
			Class modelClass = getModelClass();
			Field modelField = EntityUtil.getDeclaredField(modelClass, orderBy);
			if (null == modelField) {
				log.info("model field : {} NOT FOUND", field.getName());
				return null;
			}
			FormField formField = modelField.getAnnotation(FormField.class);
			if (null == formField) {
				log.info("formField : {} NOT FOUND", modelField.getName());
				return null;
			}
			String foreginFieldName = formField.optionItemName();
			Field foreignField = EntityUtil.getDeclaredField(field.getType(), foreginFieldName);
			String orderProperty = field.getName()+"."+foreginFieldName;
			System.out.println("----------------orderProperty: "+orderProperty);
			return orderProperty;
		}
		
		return orderBy;
	}

	private Class getModelClass() {
		 
		return BaseEntity.getModelClass(entityClass);
	}

	private Criterion restrictionLike(final String fieldName, Class<?> _class, Object value) {
		String extractedFieldName = fieldName;
		if (fieldName.contains(".") && fieldName.split("\\.").length == 2) {
			extractedFieldName = fieldName.split("\\.")[1];
		}
		System.out.println("fieldName: "+fieldName);
		Field field = EntityUtil.getDeclaredField(_class, extractedFieldName);
		System.out.println("_class: "+_class);
		
		if (field==null || field.getAnnotation(Transient.class)!=null) return null;
		
		boolean stringTypeField = field.getType().equals(String.class);
		Object validatedValue = validateFieldValue(field, value); 
		if (!stringTypeField) {

			return nonStringLikeExp(fieldName,  _class, validatedValue);
		}

		Criterion likeExp = Restrictions.ilike(fieldName, String.valueOf(validatedValue), MatchMode.ANYWHERE);

		return likeExp;

	}

	private Criterion nonStringLikeExp(String fieldName, Class<?> _class,Object value) {
		String extractedFieldName = fieldName;
		String tableName = THIS;
		if (fieldName.contains(".") && fieldName.split("\\.").length == 2) {
			tableName = getAlias(fieldName.split("\\.")[0]);
			extractedFieldName = fieldName.split("\\.")[1];
			System.out.println("tableName: "+tableName);
		}
		System.out.println("fieldName: "+fieldName);
		Field field = EntityUtil.getDeclaredField(_class, extractedFieldName);
		
		if (field.getAnnotation(Transient.class)!=null) return null;
		//TODO: change to Like if using mysql
		String columnName = QueryUtil.getColumnName(field);
//		Criterion sqlRestriction = Restrictions.sqlRestriction(tableName+"."+extractedFieldName + "::varchar(255) ILIKE '%" + value + "%'");
		Criterion sqlRestriction = Restrictions.sqlRestriction(tableName+"."+columnName + "::varchar(255) ILIKE '%" + value + "%'");

		return sqlRestriction;
	}

	private Criterion getDateFilter(String rawKey, String key ) {
		boolean dayFilter = rawKey.endsWith(QueryUtil.DAY_SUFFIX);
		boolean monthFilter = rawKey.endsWith(QueryUtil.MONTH_SUFFIX);
		boolean yearFilter = rawKey.endsWith(QueryUtil.YEAR_SUFFIX);

		if (dayFilter || monthFilter || yearFilter) {

			String fieldName = key;
			String mode = QueryUtil.FILTER_DATE_DAY;

			if (dayFilter) {
				fieldName = key.replace(QueryUtil.DAY_SUFFIX, "");
				mode = QueryUtil.FILTER_DATE_DAY;

			} else if (monthFilter) {
				fieldName = key.replace(QueryUtil.MONTH_SUFFIX, "");
				mode = QueryUtil.FILTER_DATE_MON1TH;

			} else if (yearFilter) {
				fieldName = key.replace(QueryUtil.YEAR_SUFFIX, "");
				mode = QueryUtil.FILTER_DATE_YEAR;

			}
			Field field = getDeclaredField(entityClass, fieldName);
			if (null ==field ) {
				return null;
			}
			Object value = fieldsFilter.get(key);
			String columnName = QueryUtil.getColumnName(field);
			log.info("mode: {}. value: {}", mode, value);
//			TODO: mysql
//			Criterion restriction = Restrictions.sqlRestriction(mode + "(" + columnName + ")=" + value);
//			TODO: postgres
			Criterion restriction = Restrictions.sqlRestriction("date_part('"+mode+"', " + getAlias(entityClass.getSimpleName())+"."+ columnName + ")=" + value);

			return restriction;
		}

		return null;
	}

	 

}
