package com.fajar.medicalinventory.querybuilder;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.JoinColumn;

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

	private Criterion restrictionEquals(Class<?> entityClass, String keyName, Object fieldValue) {
		String entityName = entityClass.getSimpleName();
		String columnName;
		boolean multiKey = keyName.contains(",");
		Field field;

		if (multiKey) {
			Field foreignField = EntityUtil.getDeclaredField(entityClass, keyName.split(",")[0]);
			field = EntityUtil.getDeclaredField(foreignField.getType(), keyName.split(",")[1]);
			String alias = getAlias(foreignField.getName()) + "." + QueryUtil.getColumnName(field);
			return Restrictions.sqlRestriction(alias + "='" + fieldValue + "'");
		} else {
			field = EntityUtil.getDeclaredField(entityClass, keyName);
			KeyValue joinColumnResult = QueryUtil.checkIfJoinColumn(keyName, field, true);
			
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
			if (fieldType.equals(Long.class)) {
				long value = Long.parseLong(fieldValue.toString());

				return value;
			}
		}
		return fieldValue;
	}

	/**
	 * 
	 * @param aliasName must match fieldName of entityClass
	 */
	private void setCurrentAlias(String aliasName) {
		if (null == aliasName || aliases.get(aliasName) != null)
			return;

		if (aliasName.equals(THIS)) {
			// this.currentAlias = "this_";
		} else {

			Field correspondingField = EntityUtil.getDeclaredField(entityClass, aliasName);
			if (null == correspondingField) {
				return;
			}
			aliases.put(aliasName, joinIndex);
			criteria.createAlias(entityClass.getSimpleName() + "." + aliasName, aliasName, JoinType.LEFT_OUTER_JOIN);
			if (aliasName.length() > 10) {
				aliasName = aliasName.substring(0, 10);
			}

			// this.currentAlias = aliasName.toLowerCase() + joinIndex + '_';

			joinIndex++;
		}
	}

	private String getAlias(String aliasName) {
		if (THIS.equals(aliasName)) {
			return "this_";
		}
		return aliasName.toLowerCase() + aliases.get(aliasName) + "_";
	}

	private void setJoinColumnAliases() {
		List<Field> joinColumns = QueryUtil.getJoinColumnFields(entityClass);
		for (int i = 0; i < joinColumns.size(); i++) {
			setCurrentAlias(joinColumns.get(i).getName());
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

		setCurrentAlias(THIS);

		for (final String rawKey : fieldsFilter.keySet()) {
			setCurrentAlias(THIS);

			log.info("##" + rawKey + ":" + fieldsFilter.get(rawKey));

			if (fieldsFilter.get(rawKey) == null)
				continue;

			String currentKey = rawKey;
			boolean itemExacts = allItemExactSearch;
			String finalNameAfterExactChecking = currentItemExact(rawKey);

			if (null != finalNameAfterExactChecking) {

				currentKey = finalNameAfterExactChecking;
				itemExacts = true;
				boolean multiKey = currentKey.contains(",");

				if (multiKey) {
					setCurrentAlias(currentKey.split(",")[0]);
				}

				criteria.add(restrictionEquals(entityClass, currentKey, fieldsFilter.get(rawKey)));
				continue;
			}

			log.info("Raw key: {} Now KEY: {}", rawKey, currentKey);

			// check if date
			Criterion dateFilterSql = getDateFilter(rawKey, currentKey, entityDeclaredFields, fieldsFilter);

			if (null != dateFilterSql) {
				log.info(" {} is date ", rawKey);
				criteria.add(dateFilterSql);
				continue;
			}

			boolean multiKey = rawKey.contains(",");

			Field field;
			if (multiKey) {
				Field hisField = EntityUtil.getDeclaredField(entityClass, rawKey.split(",")[0]);
				field = EntityUtil.getDeclaredField(hisField.getType(), rawKey.split(",")[1]);
			} else {
				field = QueryUtil.getFieldByName(currentKey, entityDeclaredFields);
			}

			if (field == null) {
				log.warn("Field Not Found :" + currentKey + " !");
				continue;
			}

			String fieldName = field.getName();
			KeyValue joinColumnResult = QueryUtil.checkIfJoinColumn(currentKey, field, false);

			if (null != joinColumnResult) {
				if (joinColumnResult.isValid()) {

					criteria.add(restrictionLike(fieldName + "." + joinColumnResult.getValue(), field.getType(),
							fieldsFilter.get(rawKey)));
				} else {
					continue;
				}
			} else {
				if (itemExacts) {
					criteria.add(restrictionEquals(entityClass, currentKey, fieldsFilter.get(rawKey)));
				} else {
					criteria.add(restrictionLike(entityName + "." + currentKey, entityClass, fieldsFilter.get(rawKey)));
				}
			}

		}

		if (onlyRowCount) {
			return criteria;
		}

		try {
//			setCurrentAlias(THIS);
			addOrderOffsetLimit(filter);
		} catch (Exception e) {
			log.error("Error adding order/offset/limit");
			e.printStackTrace();
		}

		return criteria;

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
			if (filter.getOrderType().toLowerCase().equals("desc")) {
				order = Order.desc(orderBy);
			} else {
				order = Order.asc(orderBy);
			}

			criteria.addOrder(order);
		}

	}

	private String extractOrderByKey(Filter filter) {
		String orderBy = filter.getOrderBy();
		Field field = EntityUtil.getDeclaredField(entityClass, orderBy);
		if (null == field) {
			log.info("{} is not {} field", orderBy, entityClass);
			return null;
		}
		if (field.getAnnotation(JoinColumn.class) != null) {
			FormField formField = field.getAnnotation(FormField.class);
			String foreginFieldName = formField.optionItemName();
			Field foreignField = EntityUtil.getDeclaredField(field.getType(), foreginFieldName);
//			return getAlias(orderBy) + "." +QueryUtil.getColumnName(foreignField);
			return orderBy+"."+foreginFieldName;
		}
		return orderBy;
	}

	private Criterion restrictionLike(final String fieldName, Class<?> _class, Object value) {
		String extractedFieldName = fieldName;
		if (fieldName.contains(".") && fieldName.split("\\.").length == 2) {
			extractedFieldName = fieldName.split("\\.")[1];
		}
		Field field = EntityUtil.getDeclaredField(_class, extractedFieldName);
		boolean stringTypeField = field.getType().equals(String.class);
		Object validatedValue = validateFieldValue(field, value);

		if (!stringTypeField) {

			return nonStringLikeExp(field, _class, validatedValue);
		}

		Criterion likeExp = Restrictions.ilike(fieldName, String.valueOf(validatedValue), MatchMode.ANYWHERE);

		return likeExp;

	}

	private Criterion nonStringLikeExp(Field field, Class<?> _class, Object value) {

		String columnName = field.getName();// QueryUtil.getColumnName(field);
		String tableName = _class.getName();// QueryUtil.getTableName(_class); NOW USING ALIAS

		boolean isJoinColumn = field.getAnnotation(JoinColumn.class) != null;
		String alias = isJoinColumn ? getAlias(field.getName()) : getAlias(THIS);
		
		//TODO: change to Like if using mysql
		Criterion sqlRestriction = Restrictions.sqlRestriction(alias + "." + columnName + " ILIKE '%" + value + "%'");

		return sqlRestriction;
	}

	private Criterion getDateFilter(String rawKey, String key, List<Field> entityDeclaredFields,
			Map<String, Object> filter) {
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
			Field field = EntityUtil.getObjectFromListByFieldName("name", fieldName, entityDeclaredFields);
			Object value = filter.get(key);
			String columnName = QueryUtil.getColumnName(field);
			log.info("mode: {}. value: {}", mode, value);
//			TODO: mysql
//			Criterion restriction = Restrictions.sqlRestriction(mode + "(" + columnName + ")=" + value);
//			TODO: postgres
			Criterion restriction = Restrictions.sqlRestriction("date_part('"+mode+"', " + columnName + ")=" + value);

			return restriction;
		}

		return null;
	}

	private String currentItemExact(String rawKey) {
		if (rawKey.endsWith("[EXACTS]")) {
			String finalKey = rawKey.split("\\[EXACTS\\]")[0];
			log.info("{} exact search", finalKey);
			return finalKey;
		}
		return null;
	}

}
