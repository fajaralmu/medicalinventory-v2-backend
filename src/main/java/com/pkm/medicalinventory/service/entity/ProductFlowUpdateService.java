package com.pkm.medicalinventory.service.entity;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.pkm.medicalinventory.dto.WebResponse;
import com.pkm.medicalinventory.entity.ProductFlow;
import com.pkm.medicalinventory.exception.ApplicationException;
import com.pkm.medicalinventory.exception.DataNotFoundException;
import com.pkm.medicalinventory.repository.ProductFlowRepository;
import com.pkm.medicalinventory.service.inventory.InventoryService;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	public ProductFlow saveEntity(ProductFlow object, boolean newRecord, HttpServletRequest httpServletRequest)
			throws Exception {
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
		inventoryService.adjustStock(httpServletRequest);
		return saved;
	}
	
	@Override
	public WebResponse deleteEntity(Long id, Class _class, HttpServletRequest httpServletRequest) throws Exception {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		try {
			ProductFlow existing = (ProductFlow) session.get(ProductFlow.class, id);
			if (existing == null) {
				throw new DataNotFoundException("Record not found");
			}
			session.delete(existing);
			tx.commit();
			inventoryService.adjustStock(httpServletRequest);
			return new WebResponse();
		} catch (Exception e) {
			
			if (tx != null)
				tx.rollback();
			throw new ApplicationException(e);
		} finally {
			session.close();
		}
		 
	}
}
