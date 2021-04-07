package com.fajar.medicalinventory.service.inventory;

import static java.lang.Integer.MIN_VALUE;

import java.math.BigInteger;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import com.fajar.medicalinventory.constants.TransactionType;
import com.fajar.medicalinventory.entity.Product;
import com.fajar.medicalinventory.repository.ProductRepository;

@Service
public class ProductAvailabilityCountRepository extends CommonRepository {

	@Autowired
	private ProductRepository productRepository;

	/**
	 * 
	 * @param isMasterHealthCenter
	 * @param expDaysWithin
	 * @param locationId
	 * @return
	 */
	public BigInteger countNontEmptyProduct(boolean isMasterHealthCenter, @Nullable Integer expDaysWithin,
			Long locationId) {
		BigInteger totalData;
		boolean withExpDateFilter = expDaysWithin != null;
		int expDatAfter = expDaysWithin != null && expDaysWithin > 0 ? 0 : MIN_VALUE;
		if (isMasterHealthCenter) {
			if (withExpDateFilter) {
				totalData = countNotEmptyProductInMasterWareHouseWithExpDaysBeforeAfter(expDaysWithin + 1, expDatAfter);

			} else {
				totalData = countNotEmptyProductInMasterWareHouse();
			}
		} else {
			if (withExpDateFilter) {
				totalData = countNotEmptyProductInSpecifiedWareHouseWithExpDaysBeforeAfter(locationId,
						expDaysWithin + 1, expDatAfter);
			} else {
				totalData = countNotEmptyProductInSpecifiedWareHouse(locationId);
			}
		}

		return totalData;
	}
	public BigInteger countNontEmptyProductAllLocation(boolean isMasterHealthCenter,
			Integer expDaysWithin ) {
		BigInteger totalData;
		boolean withExpDateFilter = expDaysWithin != null;
		if (withExpDateFilter) {
			int expDatAfter = expDaysWithin   > 0 ? 0 : MIN_VALUE;
			totalData = countNotEmptyProductAllLocationWithExpDaysBeforeAfter(expDaysWithin + 1, expDatAfter);
			 
		} else {
			totalData = countNotEmptyProductAllLocation();
		}
		return totalData;
	}
	
	@Override
	protected CriteriaWrapper commonStockCriteria(Long locationId) {
		 
		CriteriaWrapper wrapper = super.commonStockCriteria(locationId);
		wrapper.getCriteria().setProjection(null);
		return wrapper;
	}

	private BigInteger countNotEmptyProductInMasterWareHouseWithExpDaysBeforeAfter(int expDayDiffBefore,
			int expDayDiffAfter) {
		CriteriaWrapper criteriaWrapper =  commonStockCriteria(null, TransactionType.TRANS_IN);
		Criteria criteria = criteriaWrapper.getCriteria();
		criteria.add(Restrictions.sqlRestriction("extract(day from expired_date - current_timestamp) < ?",
				expDayDiffBefore, IntegerType.INSTANCE));
		criteria.add(Restrictions.sqlRestriction("extract(day from expired_date - current_timestamp) > ?",
				expDayDiffAfter, IntegerType.INSTANCE));
		criteria.add(Property.forName("product").in(productDetachedCriteria()));
		criteria.setProjection(Projections.distinct(Projections.property("product.id")));

		List result = criteria.list();
		BigInteger count = BigInteger.valueOf(result.size());
		criteriaWrapper.closeSession();
		return count;
	}

	private BigInteger countNotEmptyProductInMasterWareHouse() {
 
		CriteriaWrapper criteriaWrapper =  commonStockCriteria(null, TransactionType.TRANS_IN);
		Criteria criteria = criteriaWrapper.getCriteria();
		criteria.add(Property.forName("product").in(productDetachedCriteria()));
		criteria.setProjection(Projections.distinct(Projections.property("product.id")));

		List result = criteria.list();
		BigInteger count = BigInteger.valueOf(result.size());
		criteriaWrapper.closeSession();
		return count;
	}
	
	private BigInteger countNotEmptyProductInSpecifiedWareHouse(Long locationId) {
		 
		CriteriaWrapper criteriaWrapper = commonStockCriteria(locationId, TransactionType.TRANS_OUT_TO_WAREHOUSE);
		Criteria criteria = criteriaWrapper.getCriteria();
		criteria.add(Property.forName("product").in(productDetachedCriteria()));
		criteria.setProjection(Projections.distinct(Projections.property("product.id")));
		
		List result = criteria.list(); 
		BigInteger count = BigInteger.valueOf(result.size()); 
		criteriaWrapper.closeSession();
		return count;
		
	}
	private BigInteger countNotEmptyProductInSpecifiedWareHouseWithExpDaysBeforeAfter(Long locationId, Integer expiredDaysDiffBefore, Integer expDayDiffAfter) {
		 
		
		CriteriaWrapper criteriaWrapper = commonStockCriteria(locationId, TransactionType.TRANS_OUT_TO_WAREHOUSE);
		Criteria criteria = criteriaWrapper.getCriteria();
		criteria.add(Restrictions.sqlRestriction(" extract(day from this_.expired_date - current_timestamp) between ? and ?",
				new Object[]{expDayDiffAfter, expiredDaysDiffBefore}, new Type[] {IntegerType.INSTANCE, IntegerType.INSTANCE}));
		criteria.add(Property.forName("product").in(productDetachedCriteria()));
		criteria.setProjection(Projections.distinct(Projections.property("product.id")));
//		criteria.setProjection(null);
		criteria.addOrder(Order.asc("product.id"));
		List  result = criteria.list();
		BigInteger count = BigInteger.valueOf(result.size()); 
		
		criteriaWrapper.closeSession();
		return count;
	}
	
	private BigInteger countNotEmptyProductAllLocationWithExpDaysBeforeAfter(Integer expDayDiffBefore, Integer expDayDiffAfter) {
		 
		CriteriaWrapper criteriaWrapper = commonStockCriteria(null, TransactionType.TRANS_OUT_TO_WAREHOUSE, TransactionType.TRANS_IN);
		Criteria criteria = criteriaWrapper.getCriteria();
		criteria.add(Restrictions.sqlRestriction(" extract(day from this_.expired_date - current_timestamp) between ? and ?",
				new Object[]{expDayDiffAfter, expDayDiffBefore}, new Type[] {IntegerType.INSTANCE, IntegerType.INSTANCE}));
		criteria.add(Property.forName("product").in(productDetachedCriteria()));
		criteria.setProjection(Projections.distinct(Projections.property("product.id")));
		List  result = criteria.list();
		
		BigInteger count = BigInteger.valueOf(result.size());
		
		criteriaWrapper.closeSession();
		return count;
	}
	
	private BigInteger countNotEmptyProductAllLocation() {
		
		CriteriaWrapper criteriaWrapper = commonStockCriteria(null, TransactionType.TRANS_OUT_TO_WAREHOUSE, TransactionType.TRANS_IN);
		Criteria criteria = criteriaWrapper.getCriteria();
		criteria.add(Property.forName("product").in(productDetachedCriteria()));
		criteria.setProjection(Projections.distinct(Projections.property("product.id")));
		List  result = criteria.list();
		
		BigInteger count = BigInteger.valueOf(result.size());
		criteriaWrapper.closeSession();
		return count;
	}
	
	private DetachedCriteria productDetachedCriteria() {
		DetachedCriteria ownerCriteria = DetachedCriteria.forClass(Product.class);
		ownerCriteria.setProjection(Property.forName("id"));
		return ownerCriteria;
	}

  
}
