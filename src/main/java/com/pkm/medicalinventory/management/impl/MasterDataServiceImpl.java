package com.pkm.medicalinventory.management.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import com.pkm.medicalinventory.dto.Filter;
import com.pkm.medicalinventory.dto.WebRequest;
import com.pkm.medicalinventory.dto.WebResponse;
import com.pkm.medicalinventory.dto.model.BaseModel;
import com.pkm.medicalinventory.entity.BaseEntity;
import com.pkm.medicalinventory.entity.setting.EntityManagementConfig;
import com.pkm.medicalinventory.entity.setting.EntityProperty;
import com.pkm.medicalinventory.exception.ApplicationException;
import com.pkm.medicalinventory.management.IManagementService;
import com.pkm.medicalinventory.management.MasterDataManagementPageService;
import com.pkm.medicalinventory.management.MasterDataService;
import com.pkm.medicalinventory.repository.main.CustomRepositoryImpl;
import com.pkm.medicalinventory.repository.main.DatabaseProcessor;
import com.pkm.medicalinventory.repository.main.EntityRepository;
import com.pkm.medicalinventory.util.EntityUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MasterDataServiceImpl implements MasterDataService {
	@Autowired
	private CustomRepositoryImpl customRepository;
	@Autowired
	private EntityRepository entityRepository;
	@Autowired
	private MasterDataManagementPageService entityManagementPageService;

	private EntityManagementConfig getEntityManagementConfig(String key) {
		return entityRepository.getConfig(key);
	}
	
	public BaseModel saveEntity(WebRequest request, boolean newRecord) {
		final String key = request.getEntity().toLowerCase();
		EntityManagementConfig entityConfig = getEntityManagementConfig(key);
		IManagementService updateService = entityConfig.getEntityUpdateService();
		String fieldName = entityConfig.getFieldName();
		BaseModel entityValue = null;

		try {
			Field entityField = EntityUtil.getDeclaredField(WebRequest.class, fieldName);
			entityValue = (BaseModel) entityField.get(request);

			log.info("save {}", entityField.getName());
			log.info("newRecord: {}", newRecord);

			if (entityValue != null) {
				log.info("updateService: {}", updateService.getClass().getSimpleName());
				BaseEntity saved = updateService.saveEntity(entityValue.toEntity(), newRecord);
				return saved.toModel();
			} else {
				throw new IllegalArgumentException("invalid argument");
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new ApplicationException(e);
		}

	}

	/**
	 * get list of entities filtered
	 * 
	 * @param request
	 * @return
	 */
	public WebResponse filter(WebRequest request) {
		Class<? extends BaseEntity> entityClass = null;

		Filter filter = EntityUtil.cloneSerializable(request.getFilter());

		if (filter == null) {
			filter = new Filter();
		}
		if (filter.getFieldsFilter() == null) {
			filter.setFieldsFilter(new HashMap<String, Object>());
		}

		try {

			String entityName = request.getEntity().toLowerCase();
			EntityManagementConfig entityConfig = getEntityManagementConfig(entityName);
			log.info("entityName: {}, config: {}", entityName, entityConfig);
			if (null == entityConfig) {
				throw new Exception("Invalid entity:" + entityName);
			}
			IManagementService updateService = entityConfig.getEntityUpdateService();
			entityClass = entityConfig.getEntityClass();
			CommonFilterResult entityResult = filterEntities(filter, entityClass);
			log.info("Sart post filter: {}", entityName);
			updateService.postFilter(entityResult.getEntities());
			log.info("Post filter finished: {}", entityName);
			return new WebResponse()
					.withEntityClass(entityClass)
					.withEntities(BaseModel.toModels(entityResult.getEntities()))
					.withTotalData(entityResult.getCount())
					.withFilter(request.getFilter());

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ApplicationException(ex);
		}
	}

	public <T extends BaseEntity> CommonFilterResult<T> filterEntities(Filter filter, Class<T> entityClass) {
		final List<T> entities = new ArrayList<>();
		final Map<String, Long> count = new HashMap<>();
		DatabaseProcessor filterDatabaseProcessor = customRepository.createDatabaseProcessor();
		try {
			List<T> resultList = filterDatabaseProcessor.filter(entityClass, filter);
			entities.addAll(resultList);
			long resultCount = filterDatabaseProcessor.getRowCount(entityClass, filter);
			count.put("value", resultCount);
		} catch (Exception e) {
			log.error("Error filterEntities: {}", e.getCause());
			count.put("value", 0L);
			e.printStackTrace();
		}
		CommonFilterResult<T> result = new CommonFilterResult<>();
		result.setEntities(entities);
		result.setCount(count.get("value").intValue());
		return result;
//		return EntityResult.builder().entities(CollectionUtil.convertList(entities))
//				.count(count.get("value").intValue()).build();
	}

	/**
	 * delete entity
	 * 
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	public BaseModel delete(WebRequest request)  {
		DatabaseProcessor filterDatabaseProcessor = customRepository.createDatabaseProcessor();
		try {
			Map<String, Object> filter = request.getFilter().getFieldsFilter();
			Long id = Long.parseLong(filter.get("id").toString());
			String entityName = request.getEntity().toLowerCase();
			EntityManagementConfig entityConfig = getEntityManagementConfig(entityName);
			IManagementService updateService = entityConfig.getEntityUpdateService();
			return updateService.deleteEntity(id, entityConfig.getEntityClass()).toModel();
		} catch (Exception e) {
			throw new ApplicationException(e);
		} finally {
			 //
		}
	}

	public <T extends BaseEntity> List<T> findAll(Class<T> _class) {
		List<T> resultList = entityRepository.findAll(_class);

		if (null == resultList) {
			resultList = new ArrayList<T>();
		}

		return resultList;
	}

	public EntityProperty getConfig(WebRequest request) {
		try {
			final String key = request.getEntity().toLowerCase();
			Model model = entityManagementPageService.setModel(new ConcurrentModel(), key);

			return (EntityProperty) ((ConcurrentModel) model).get("entityProperty");
		} catch (Exception e) {

			return null;
		}
	}
}
