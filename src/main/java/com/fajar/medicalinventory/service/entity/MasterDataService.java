package com.fajar.medicalinventory.service.entity;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import com.fajar.medicalinventory.dto.Filter;
import com.fajar.medicalinventory.dto.WebRequest;
import com.fajar.medicalinventory.dto.WebResponse;
import com.fajar.medicalinventory.dto.model.BaseModel;
import com.fajar.medicalinventory.entity.BaseEntity;
import com.fajar.medicalinventory.entity.setting.EntityManagementConfig;
import com.fajar.medicalinventory.entity.setting.EntityProperty;
import com.fajar.medicalinventory.exception.ApplicationException;
import com.fajar.medicalinventory.repository.CustomRepositoryImpl;
import com.fajar.medicalinventory.repository.DatabaseProcessor;
import com.fajar.medicalinventory.repository.EntityRepository;
import com.fajar.medicalinventory.service.LogProxyFactory;
import com.fajar.medicalinventory.util.CollectionUtil;
import com.fajar.medicalinventory.util.EntityUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MasterDataService {

	public static final String ORIGINAL_PREFFIX = "{ORIGINAL>>";

	@Autowired
	private CustomRepositoryImpl customRepository;
	@Autowired
	private EntityRepository entityRepository; 
	@Autowired
	private EntityManagementPageService entityManagementPageService;   
	
	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this); 
	}

	private EntityManagementConfig getEntityManagementConfig(String key) {
		return entityRepository.getConfiguration(key);
	}

	/**
	 * add & update entity
	 * 
	 * @param request
	 * @param servletRequest
	 * @param newRecord
	 * @return
	 */
	public WebResponse saveEntity(WebRequest request, HttpServletRequest servletRequest, boolean newRecord) {

			final String key = request.getEntity().toLowerCase();
			EntityManagementConfig entityConfig = getEntityManagementConfig(key);
			BaseEntityUpdateService updateService = entityConfig.getEntityUpdateService();
			String fieldName = entityConfig.getFieldName();
			BaseModel entityValue = null;

			try {
				Field entityField = EntityUtil.getDeclaredField(WebRequest.class, fieldName);
				entityValue = (BaseModel) entityField.get(request);

				log.info("save {}", entityField.getName());
				log.info("newRecord: {}", newRecord);
				
				if (entityValue != null) {
					 
					BaseEntity savedEntity = updateService.saveEntity(entityValue.toEntity(), newRecord, servletRequest); 
					 
					return WebResponse.builder().entity(savedEntity.toModel()).build();
				} else {
					return WebResponse.failed();
				}

			} catch (Exception e) {
				throw new ApplicationException(e.getMessage());
			}

		 
	}
 
	/**
	 * get list of entities filtered
	 * 
	 * @param request
	 * @return
	 */
	public WebResponse filter(WebRequest request, HttpServletRequest httpRequest) {
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
			EntityManagementConfig config = getEntityManagementConfig(entityName);
			log.info("entityName: {}, config: {}", entityName, config);
			if (null == config) {
				throw new Exception("Invalid entity:"+entityName);
			}
			entityClass = config.getEntityClass();
			EntityResult entityResult = filterEntities(filter, entityClass);
			return WebResponse.builder()
					.entities(BaseModel.toModels(entityResult.entities))
					.totalData(entityResult.count).filter(request.getFilter()).build();

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ApplicationException(ex);
		}
	}

	public <T extends BaseEntity> EntityResult filterEntities(Filter filter, Class<T> entityClass) {
		final List<T> entities = new ArrayList<>();
		final Map<String, Long> count = new HashMap<>();
		DatabaseProcessor filterDatabaseProcessor = customRepository.createDatabaseProcessor(); 
		try {
			
			List<T> resultList = filterDatabaseProcessor.filterAndSortv2(entityClass, filter);
			entities.addAll(resultList); 
			long resultCount = filterDatabaseProcessor.getRowCount(entityClass, filter);
			count.put("value", resultCount);
		} catch (Exception e) {
			log.error("Error filterEntities: {}", e.getCause());
			count.put("value", 0L);
			e.printStackTrace();
		}
		 
		return EntityResult.builder().entities(CollectionUtil.convertList(entities))
				.count(count.get("value").intValue()).build();
	}

	/**
	 * delete entity
	 * 
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	public WebResponse delete(WebRequest request, HttpServletRequest httpRequest) throws Exception {
		DatabaseProcessor filterDatabaseProcessor = customRepository.createDatabaseProcessor();
		try {
			Map<String, Object> filter = request.getFilter().getFieldsFilter();
			Long id = Long.parseLong(filter.get("id").toString());
			String entityName = request.getEntity().toLowerCase();
			EntityManagementConfig entityConfig = getEntityManagementConfig(entityName);
			BaseEntityUpdateService updateService = entityConfig.getEntityUpdateService();
			return updateService.deleteEntity(id, entityConfig.getEntityClass(), httpRequest);
		} catch (Exception e) {
			throw new ApplicationException(e.getMessage());
		} finally {
			 
		}
	}
 

	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class EntityResult implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7627112916142073122L;
		List<BaseEntity> entities;
		int count;
	}
 

	public <T extends BaseEntity> List<T> findAll(Class<T> _class) {
		List<T> resultList = entityRepository.findAll(_class);

		if (null == resultList) {
			resultList = new ArrayList<T>();
		}

		return resultList;
	}

	public EntityProperty getConfig(WebRequest request, HttpServletRequest httpRequest) {
		try {
			final String key = request.getEntity().toLowerCase();
			Model model = entityManagementPageService.setModel(httpRequest, new ConcurrentModel(), key); 
			 
			return (EntityProperty) ((ConcurrentModel)model).get("entityProperty");
		}catch (Exception e) {
			
			return null;
		}
	}

}
