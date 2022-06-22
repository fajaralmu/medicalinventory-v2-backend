package com.pkm.medicalinventory.service.entity;

import static com.pkm.medicalinventory.util.MvcUtil.constructCommonModel;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.pkm.medicalinventory.dto.WebResponse;
import com.pkm.medicalinventory.dto.model.BaseModel;
import com.pkm.medicalinventory.entity.BaseEntity;
import com.pkm.medicalinventory.entity.Customer;
import com.pkm.medicalinventory.entity.HealthCenter;
import com.pkm.medicalinventory.entity.Product;
import com.pkm.medicalinventory.entity.ProductFlow;
import com.pkm.medicalinventory.entity.Supplier;
import com.pkm.medicalinventory.entity.Transaction;
import com.pkm.medicalinventory.entity.Unit;
import com.pkm.medicalinventory.entity.setting.EntityManagementConfig;
import com.pkm.medicalinventory.entity.setting.EntityProperty;
import com.pkm.medicalinventory.exception.ApplicationException;
import com.pkm.medicalinventory.repository.EntityRepository;
import com.pkm.medicalinventory.util.CollectionUtil;
import com.pkm.medicalinventory.util.EntityPropertyBuilder;
import com.pkm.medicalinventory.util.EntityUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EntityManagementPageService {

	@Autowired
	private EntityRepository entityRepository;
	 
	public Model setModel(HttpServletRequest request, Model model, String key) throws Exception {

		EntityManagementConfig entityConfig = entityRepository.getConfig(key);

		if (null == entityConfig) {
			throw new IllegalArgumentException("Invalid entity key (" + key + ")!");
		}

		HashMap<String, List<?>> additionalListObject = getFixedListObjects(entityConfig.getEntityClass());
		EntityProperty entityProperty = EntityPropertyBuilder.createEntityProperty(entityConfig.getModelClass(),
				additionalListObject);
		model = constructCommonModel(request, entityProperty, model, entityConfig.getEntityClass().getSimpleName(),
				"management"); 
		 
		return model;
	}

	private HashMap<String, List<?>> getFixedListObjects(Class<? extends BaseEntity> entityClass) {
		HashMap<String, List<?>> listObject = new HashMap<>();
		try {
			List<Field> fixedListFields = EntityUtil.getFixedListFields(entityClass);

			for (int i = 0; i < fixedListFields.size(); i++) {
				Field field = fixedListFields.get(i);
				Class<? extends BaseEntity> type;

				if (CollectionUtil.isCollectionOfBaseEntity(field)) {
					Type classType = CollectionUtil.getGenericTypes(field)[0];
					type = (Class<? extends BaseEntity>) classType;

				} else {
					type = (Class<? extends BaseEntity>) field.getType();
				}
				log.info("(populating fixed list values) findALL FOR type: {}", type);
				List<? extends BaseEntity> list = entityRepository.findAll(type);
				
				
				listObject.put(field.getName(), BaseModel.toModels(list));
//				listObject.put(field.getName(), CollectionUtil.convertList(list));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listObject;
	}

	 
	public WebResponse getManagementPages() {
		
		List<Object> result = new ArrayList<>(); 
		
		addConfig(result, Customer.class, "fas fa-user");
		addConfig(result, HealthCenter.class, "fas fa-landmark");
		addConfig(result, Product.class, "fas fa-box");
		addConfig(result, Supplier.class, "fas fa-truck");
		addConfig(result, Unit.class, "fas fa-tags");
		addConfig(result, Transaction.class, "fas fa-book");
		addConfig(result, ProductFlow.class, "fas fa-box");
		
		return WebResponse.builder().generalList(result).build();
	}
	  void addConfig(List<Object> result, Class<?> _class, String iconClassName) {
		  try {
			  result.add(entityRepository.getConfig(_class.getSimpleName().toLowerCase()).setIconClassName(iconClassName));
		  }catch (Exception e) {
			  log.error("Error getting config for : {}",_class );
			  e.printStackTrace();
		}
	}

}
