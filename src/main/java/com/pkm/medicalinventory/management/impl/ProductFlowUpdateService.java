package com.pkm.medicalinventory.management.impl;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pkm.medicalinventory.dto.WebResponse;
import com.pkm.medicalinventory.entity.ProductFlow;
import com.pkm.medicalinventory.exception.ApplicationException;
import com.pkm.medicalinventory.exception.DataNotFoundException;
import com.pkm.medicalinventory.inventory.InventoryService;
import com.pkm.medicalinventory.repository.readonly.ProductFlowRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProductFlowUpdateService extends BaseEntityUpdateService<ProductFlow> {

	@Autowired
	private ProductFlowRepository productFlowRepository;
	@Autowired
	private InventoryService inventoryService;
	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public ProductFlow saveEntity(ProductFlow object, boolean newRecord) {
		log.info("Save product flow");
		Optional<ProductFlow> existingObjectOpt = productFlowRepository.findById(object.getId());
		if (existingObjectOpt.isPresent() == false) {
			throw new ApplicationException(new Exception("existing product flow not found"));
		}
		ProductFlow existingObject = existingObjectOpt.get();
		existingObject.setCount(object.getCount());
		existingObject.setPrice(object.getPrice());
		existingObject.setSuitable(object.isSuitable());
		
		if (false == existingObject.isDistributed ()) {
			existingObject.setExpiredDate(object.getExpiredDate());
			existingObject.setGeneric(object.isGeneric());
		}
		
		ProductFlow saved = entityRepository.save(existingObject);
		inventoryService.adjustStock();
		return saved;
	}
	
	@Override
	public ProductFlow deleteEntity(Long id, Class<ProductFlow> _class) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		try {
			ProductFlow existing = (ProductFlow) session.get(ProductFlow.class, id);
			if (existing == null) {
				throw new DataNotFoundException("Record not found");
			}
			session.delete(existing);
			tx.commit();
			inventoryService.adjustStock();
			return existing;
		} catch (Exception e) {
			
			if (tx != null)
				tx.rollback();
			throw new ApplicationException(e);
		} finally {
			session.close();
		}
		 
	}
}

