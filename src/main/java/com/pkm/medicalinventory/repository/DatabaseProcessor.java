package com.pkm.medicalinventory.repository;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pkm.medicalinventory.dto.Filter;
import com.pkm.medicalinventory.entity.BaseEntity;
import com.pkm.medicalinventory.exception.DataNotFoundException;
import com.pkm.medicalinventory.querybuilder.CriteriaBuilder;
import com.pkm.medicalinventory.querybuilder.QueryUtil;
import com.pkm.medicalinventory.util.CollectionUtil;
import com.pkm.medicalinventory.util.EntityUtil;
import com.pkm.medicalinventory.util.StringUtil;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatabaseProcessor {
 
	private Session hibernateSession;
	boolean removeTransactionAfterPersistence = true;
	private final SessionFactory sessionFactory;
	private String id = StringUtil.generateRandomNumber(5);

	public DatabaseProcessor(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		this.hibernateSession = sessionFactory.openSession();
	}

	private void checkSession() {
		if (null == hibernateSession || hibernateSession.isConnected() == false) {
			hibernateSession = sessionFactory.openSession();
		}
	}

	public long getRowCount(Class<? extends BaseEntity> _class, Filter filter) { 
		log.info("getRowCount {}", _class);
		try {
			checkSession();
			CriteriaBuilder criteriaBuilder = getCriteriaBuilder(_class, filter);
			Criteria criteria = criteriaBuilder.createRowCountCriteria();

			return (long) criteria.uniqueResult();
		} catch (Exception e) {
			return 0;
		} finally {
			refresh();
		}
	}
	
	private CriteriaBuilder getCriteriaBuilder(Class<? extends BaseEntity> _class, Filter filter) {
		return new CriteriaBuilder(hibernateSession, _class, filter);
	}

	public <T extends BaseEntity> List<T> filterAndSortv2(Class<T> _class, Filter filter) {
		log.info("filterAndSortv2 : {}", _class);
		checkSession();
		Transaction tx = hibernateSession.beginTransaction();
		try {
			
			CriteriaBuilder criteriaBuilder = getCriteriaBuilder(_class, filter);
			Criteria criteria = criteriaBuilder.createCriteria();
			List<T> resultList = criteria.list();

			if (null == resultList) {
				resultList = new ArrayList<>();
			}
			tx.commit();

			log.info("resultList length: {}", resultList.size());
			return resultList;
		} catch (Exception e) {
			log.error("Error filter and sort v2: {}", e);
			e.printStackTrace();
			if (null != tx) {
				tx.rollback();
			}
		} finally {
			refresh();
		}
		return CollectionUtil.emptyList();
	}

	public <T extends BaseEntity> T validateJoinColumns(T rawEntity) throws Exception {
		List<Field> joinColumnFields = QueryUtil.getJoinColumnFields(rawEntity.getClass());

		T entity = EntityUtil.cloneSerializable(rawEntity);

		if (0 == joinColumnFields.size()) {
			return entity;
		}
		for (Field field : joinColumnFields) {
			BaseEntity fieldValue = (BaseEntity) field.get(entity);
			log.info("check join column field: {}->value: {}", field.getName(), fieldValue);
			if (null == fieldValue)
				continue;
			Object dbValue = hibernateSession.get(fieldValue.getClass(), fieldValue.getId());

			if (null == dbValue)
				continue;

			field.set(entity, dbValue);
		}

		return entity;

	}

	private void refresh() {
		log.info("Refresh DB Processor with id: {}", id);
		try {
			if (null == hibernateSession)
				return;

			hibernateSession.clear();
			hibernateSession.flush();
			hibernateSession.close();
			hibernateSession = null;

		} catch (Exception e) {
			log.error("ERROR refresh session: {}", e.getMessage());
		}

	}

	/**
	 * insert new record or update existing record (IF ID exists)
	 * 
	 * @param <T>
	 * @param object
	 * @param hibernateSession
	 * @return
	 */
	public static <T extends BaseEntity> T save(T object, Session hibernateSession) {
		if (object.getId() != null) {
			return (T) hibernateSession.merge(object);
		}
		Serializable savedId = hibernateSession.save(object);
		object.setId(Long.valueOf(savedId.toString()));
		return object;
	}

	public boolean deleteObjectById(Class<? extends BaseEntity> entityClass, Long id2) {
		checkSession();
		Transaction transaction = hibernateSession.beginTransaction();
		try {
			Object object = hibernateSession.get(entityClass, id2);
			if (null == object) {
				throw new DataNotFoundException("Record not found");
			}
			hibernateSession.delete(object);
			transaction.commit();
			return true;
		} catch (Exception e) {
			if (null != transaction) {
				transaction.rollback();
			}
			throw e;
		} finally {

			refresh();
		} 
	}

	public <T> List<T> findByKeyAndValues(Class<T> entityClass, String key, Object... values) {
		checkSession();
		if (values == null) {
			log.error("break findByKeyAndValues >> VALUES IS NULL");
			return CollectionUtil.emptyList();
		}

		log.info("findByKeyAndValues, class: {}, key: {}, values.length: {}", entityClass, key, values.length);

		Criteria criteria = hibernateSession.createCriteria(entityClass);
		Criterion[] predictates = new Criterion[values.length];
		for (int i = 0; i < values.length; i++) {
			predictates[i] = (Restrictions.naturalId().set(key, values[i]));
		}

		criteria.add(Restrictions.or(predictates));
		List list = criteria.list();

		refresh();
		log.info("RESULT findByKeyAndValues:{}", list == null ? "NULL" : list.size());
		return list;
	}

	public <T extends BaseEntity> T saveObject(final T rawEntity) {

		if (null == rawEntity) {
			log.error("rawEntity IS NULL");
			return null;
		}
		String mode = rawEntity.getId() != null?  "update record" : "new record";
		T result, entity;

		try {
			entity = validateJoinColumns(rawEntity);
		} catch (Exception e) {
			entity = rawEntity;
			e.printStackTrace();
		}
		checkSession();
		Transaction transaction = hibernateSession.beginTransaction();
		try {
			if (entity.getId() == null) {
				log.debug("Will save new entity ");
				rawEntity.setCreatedDate(new Date()); 
				Long newId = (Long) hibernateSession.save(entity);
				result = entity;
				result.setId(newId);

				log.debug("success add new record of {} with new ID: {}", entity.getClass(), newId);
			} else {
				log.debug("Will update entity :{}", entity.getId());
				entity.setModifiedDate(new Date()); 
				result = (T) hibernateSession.merge(entity); 
				log.debug("success update record of {}  ", entity.getClass());
			}

			transaction.commit();
			if (null != result) {
				log.info("SaveObjectSUCCESS {} Object: {}, id: {}", mode, result.getClass(), result.getId());
			}

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			if (null != transaction) {
				transaction.rollback();
			}
			throw e;
		} finally {
			refresh();
		}
	}

}
