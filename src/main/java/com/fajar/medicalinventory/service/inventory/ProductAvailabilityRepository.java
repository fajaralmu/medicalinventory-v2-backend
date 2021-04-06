package com.fajar.medicalinventory.service.inventory;

import static java.lang.Integer.MIN_VALUE;

import java.math.BigInteger;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
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
public class ProductAvailabilityRepository extends CommonRepository {

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
	protected Criteria commonStockCriteria(Long locationId) {
		 
		Criteria c = super.commonStockCriteria(locationId);
		c.setProjection(null);
		return c;
	}

	private BigInteger countNotEmptyProductInMasterWareHouseWithExpDaysBeforeAfter(int expDayDiffBefore,
			int expDayDiffAfter) {
		Criteria criteria = commonStockCriteria(null, TransactionType.TRANS_IN);
		
		criteria.add(Restrictions.sqlRestriction("extract(day from expired_date - current_timestamp) < ?",
				expDayDiffBefore, IntegerType.INSTANCE));
		criteria.add(Restrictions.sqlRestriction("extract(day from expired_date - current_timestamp) > ?",
				expDayDiffAfter, IntegerType.INSTANCE));
		criteria.add(Property.forName("product").in(productDetachedCriteria()));
		criteria.setProjection(Projections.distinct(Projections.property("product.id")));

		List result = criteria.list();
		BigInteger count = BigInteger.valueOf(result.size());
		return count;
	}

	private BigInteger countNotEmptyProductInMasterWareHouse() {
 
		Criteria criteria = commonStockCriteria(null, TransactionType.TRANS_IN);
		criteria.add(Property.forName("product").in(productDetachedCriteria()));
		criteria.setProjection(Projections.distinct(Projections.property("product.id")));

		List result = criteria.list();
		BigInteger count = BigInteger.valueOf(result.size());
		return count;
	}
	
	private BigInteger countNotEmptyProductInSpecifiedWareHouse(Long locationId) {
		 
		Criteria criteria = commonStockCriteria(locationId, TransactionType.TRANS_OUT_TO_WAREHOUSE);
		criteria.add(Property.forName("product").in(productDetachedCriteria()));
		criteria.setProjection(Projections.distinct(Projections.property("product.id")));
		
		List result = criteria.list(); 
		BigInteger count = BigInteger.valueOf(result.size()); 
		return count;
		
	}
	private BigInteger countNotEmptyProductInSpecifiedWareHouseWithExpDaysBeforeAfter(Long locationId, Integer expiredDaysDiffBefore, Integer expDayDiffAfter) {
		 
		
		Criteria criteria = commonStockCriteria(locationId, TransactionType.TRANS_OUT_TO_WAREHOUSE);
		criteria.add(Restrictions.sqlRestriction(" extract(day from this_.expired_date - current_timestamp) between ? and ?",
				new Object[]{expDayDiffAfter, expiredDaysDiffBefore}, new Type[] {IntegerType.INSTANCE, IntegerType.INSTANCE}));
		criteria.add(Property.forName("product").in(productDetachedCriteria()));
		criteria.setProjection(Projections.distinct(Projections.property("product.id")));
//		criteria.setProjection(null);
		criteria.addOrder(Order.asc("product.id"));
		List  result = criteria.list();
		BigInteger count = BigInteger.valueOf(result.size()); 
		return count;
	}
	
	private BigInteger countNotEmptyProductAllLocationWithExpDaysBeforeAfter(Integer expDayDiffBefore, Integer expDayDiffAfter) {
		 
		Criteria criteria = commonStockCriteria(null, TransactionType.TRANS_OUT_TO_WAREHOUSE, TransactionType.TRANS_IN);
		criteria.add(Restrictions.sqlRestriction(" extract(day from this_.expired_date - current_timestamp) between ? and ?",
				new Object[]{expDayDiffAfter, expDayDiffBefore}, new Type[] {IntegerType.INSTANCE, IntegerType.INSTANCE}));
		criteria.add(Property.forName("product").in(productDetachedCriteria()));
		criteria.setProjection(Projections.distinct(Projections.property("product.id")));
		List  result = criteria.list();
		
		BigInteger count = BigInteger.valueOf(result.size());
		return count;
	}
	
	private BigInteger countNotEmptyProductAllLocation() {
		
		Criteria criteria = commonStockCriteria(null, TransactionType.TRANS_OUT_TO_WAREHOUSE, TransactionType.TRANS_IN);
		criteria.add(Property.forName("product").in(productDetachedCriteria()));
		criteria.setProjection(Projections.distinct(Projections.property("product.id")));
		List  result = criteria.list();
		
		BigInteger count = BigInteger.valueOf(result.size());
		return count;
	}
	
	private DetachedCriteria productDetachedCriteria() {
		DetachedCriteria ownerCriteria = DetachedCriteria.forClass(Product.class);
		ownerCriteria.setProjection(Property.forName("id"));
		return ownerCriteria;
	}

//	/**
//	 * 
//	 * @param isMasterHealthCenter
//	 * @param expDaysWithin
//	 * @return
//	 */
//	public BigInteger countNontEmptyProductAllLocation(boolean isMasterHealthCenter,
//			Integer expDaysWithin ) {
//		BigInteger totalData;
//		boolean withExpDateFilter = expDaysWithin != null;
//		if (withExpDateFilter) {
//			int expDatAfter = expDaysWithin   > 0 ? 0 : MIN_VALUE;
//			totalData = countNotEmptyProductAllLocationWithExpDaysBeforeAfter(expDaysWithin + 1, expDatAfter);
//			 
//		} else {
//			totalData = countNotEmptyProductAllLocation();
//		}
//		return totalData;
//	}
//
//	public List<Product> getAvailableProducts(boolean isMasterHealthCenter, Filter filter, Long locationId) {
//		final boolean ignoreEmptyValue = filter.isIgnoreEmptyValue();
//		final Integer expDaysWithin = filter.isFilterExpDate() ? filter.getDay() : null;
//		final PageRequest pageable = filter.getPageRequest();
//		List<Product> products = new ArrayList<Product>();
//
//		if (ignoreEmptyValue) {
//			boolean withExpDateFilter = expDaysWithin != null;
//			Date expiredDateWithin = withExpDateFilter ? DateUtil.plusDay(new Date(), expDaysWithin  ) : null;
//			if (isMasterHealthCenter) {
//				products = findNotEmptyProductAtMasterWarehouse(expiredDateWithin, pageable);
//			} else {
//				products = findNotEmptyProductAtBranchWarehouse(expiredDateWithin, pageable, locationId);
//			}
//		} else {
//			products = findByOrderByName(pageable);
//		}
//		return products;
//	}
//
//	public List<Product> findNotEmptyProductAtBranchWarehouse(@Nullable Date expiredDateWithin, PageRequest pageable,
//			Long locationId) {
//
//		if (expiredDateWithin != null) {
//			boolean afterToday = DateUtil.afterToday(expiredDateWithin);
//			if (afterToday) {
//				findNotEmptyProductInSpecifiedWarehouseWithExpDateBeforeAfter(locationId, expiredDateWithin, new Date(), pageable);
//			}
//			return findNotEmptyProductInSpecifiedWarehouseWithExpDateBefore(locationId, expiredDateWithin, pageable);
//		} else {
//			return findNotEmptyProductInSpecifiedWarehouse(locationId, pageable);
//		}
//	}
//
//	public List<Product> findNotEmptyProductAtMasterWarehouse(@Nullable Date expDateBefore, PageRequest pageable) {
//		if (expDateBefore != null) {
//			boolean afterToday = DateUtil.afterToday(expDateBefore);
//			if (afterToday) {
//				return findNotEmptyProductInMasterWarehouseWithExpDateBeforeAfter(expDateBefore, new Date(), pageable);
//			}
//			return findNotEmptyProductInMasterWarehouseWithExpDateBefore(expDateBefore, pageable);
//		} else {
//			return findNotEmptyProductInMasterWarehouse(pageable);
//		}
//	}
//	public List<Product> findNotEmptyProductAtAllLocation(@Nullable Date expiredDateWithin, PageRequest pageable) {
//		if (expiredDateWithin != null) {
//			boolean afterToday = DateUtil.afterToday(expiredDateWithin);
//			if (afterToday) {
//				return findNotEmptyProductAllLocationWithExpDateBeforeAfter(expiredDateWithin, new Date(), pageable);
//			}
//			return findNotEmptyProductAllLocationWithExpDateBefore(expiredDateWithin, pageable);
//		} else {
//			return findNotEmptyProductAllLocation(pageable);
//		}
//	}
//
//	public List<Product> getAvailableProductsAllLocation(Filter filter) {
//		List<Product> products = new ArrayList<>();
//		final boolean ignoreEmptyValue = filter.isIgnoreEmptyValue();
//		final Integer expDaysWithin = filter.isFilterExpDate() ? filter.getDay() : null;
//		final PageRequest pageable = filter.getPageRequest();
//
//		if (ignoreEmptyValue) {
//			boolean withExpDateFilter = expDaysWithin != null;
//			Date expiredDateWithin = withExpDateFilter ? DateUtil.plusDay(new Date(), expDaysWithin  ) : null;
//			products = findNotEmptyProductAtAllLocation(expiredDateWithin, pageable);
//			 
//		} else {
//			products = productRepository.findByOrderByName(pageable);
//		}
//		return products ;
//	}

	/////////////////////////// privates /////////////////////
}
