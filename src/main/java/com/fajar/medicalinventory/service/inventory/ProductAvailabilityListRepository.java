package com.fajar.medicalinventory.service.inventory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import com.fajar.medicalinventory.constants.TransactionType;
import com.fajar.medicalinventory.dto.Filter;
import com.fajar.medicalinventory.entity.Product;
import com.fajar.medicalinventory.repository.ProductRepository;
import com.fajar.medicalinventory.util.DateUtil;

@Service
public class ProductAvailabilityListRepository extends CommonRepository {
	
	@Autowired
	private ProductRepository productRepository;
	
	public List<Product> getAvailableProducts(boolean isMasterHealthCenter, Filter filter, Long locationId) {
		final boolean ignoreEmptyValue = filter.isIgnoreEmptyValue();
		final Integer expDaysWithin = filter.isFilterExpDate() ? filter.getDay() : null;
		final PageRequest pageable = filter.getPageRequest();
		List<Product> products = new ArrayList<Product>();

		if (ignoreEmptyValue) {
			boolean withExpDateFilter = expDaysWithin != null;
			Date expiredDateWithin = withExpDateFilter ? DateUtil.plusDay(new Date(), expDaysWithin  ) : null;
			if (isMasterHealthCenter) {
				products = findNotEmptyProductAtMasterWarehouse(expiredDateWithin, pageable);
			} else {
				products = findNotEmptyProductAtBranchWarehouse(expiredDateWithin, pageable, locationId);
			}
		} else {
			products = productRepository.findByOrderByName(pageable);
		}
		return products;
	}

	public List<Product> findNotEmptyProductAtBranchWarehouse(@Nullable Date expiredDateWithin, PageRequest pageable,
			Long locationId) {

		if (expiredDateWithin != null) {
			boolean afterToday = DateUtil.afterToday(expiredDateWithin);
			if (afterToday) {
				findNotEmptyProductInSpecifiedWarehouseWithExpDateBeforeAfter(locationId, expiredDateWithin, new Date(), pageable);
			}
			return findNotEmptyProductInSpecifiedWarehouseWithExpDateBefore(locationId, expiredDateWithin, pageable);
		} else {
			return findNotEmptyProductInSpecifiedWarehouse(locationId, pageable);
		}
	}

	public List<Product> findNotEmptyProductAtMasterWarehouse(@Nullable Date expDateBefore, PageRequest pageable) {
		if (expDateBefore != null) {
			boolean afterToday = DateUtil.afterToday(expDateBefore);
			if (afterToday) {
				return findNotEmptyProductInMasterWarehouseWithExpDateBeforeAfter(expDateBefore, new Date(), pageable);
			}
			return findNotEmptyProductInMasterWarehouseWithExpDateBefore(expDateBefore, pageable);
		} else {
			return findNotEmptyProductInMasterWarehouse(pageable);
		}
	}
	public List<Product> findNotEmptyProductAtAllLocation(@Nullable Date expiredDateWithin, PageRequest pageable) {
		if (expiredDateWithin != null) {
			boolean afterToday = DateUtil.afterToday(expiredDateWithin);
			if (afterToday) {
				return findNotEmptyProductAllLocationWithExpDateBeforeAfter(expiredDateWithin, new Date(), pageable);
			}
			return findNotEmptyProductAllLocationWithExpDateBefore(expiredDateWithin, pageable);
		} else {
			return findNotEmptyProductAllLocation(pageable);
		}
	}

	public List<Product> getAvailableProductsAllLocation(Filter filter) {
		List<Product> products = new ArrayList<>();
		final boolean ignoreEmptyValue = filter.isIgnoreEmptyValue();
		final Integer expDaysWithin = filter.isFilterExpDate() ? filter.getDay() : null;
		final PageRequest pageable = filter.getPageRequest();

		if (ignoreEmptyValue) {
			boolean withExpDateFilter = expDaysWithin != null;
			Date expiredDateWithin = withExpDateFilter ? DateUtil.plusDay(new Date(), expDaysWithin  ) : null;
			products = findNotEmptyProductAtAllLocation(expiredDateWithin, pageable);
			 
		} else {
			products = productRepository.findByOrderByName(pageable);
		}
		return products ;
	} 
	
	
	////////////////////////////////////////////////////

	@Override
	protected CriteriaWrapper commonStockCriteria(Long locationId) {

		CriteriaWrapper wrapper = super.commonStockCriteria(locationId);
		wrapper.getCriteria().setProjection(null);
		wrapper.getCriteria().createAlias("product", "product");
		wrapper.getCriteria().setProjection(projections());
		wrapper.getCriteria().addOrder(Order.asc("product.name"));
		return wrapper;
	}

	private static Projection projections() {
		return Projections.projectionList().add(Projections.distinct(Projections.property("product.id")))
				.add(Projections.property("product.name"));
	}

	///////////////// BRANCH ///////////////////

 
	private List<Product> findNotEmptyProductInSpecifiedWarehouse(Long locationId, Pageable of) {

		CriteriaWrapper wrapper = commonStockCriteria(locationId, TransactionType.TRANS_OUT_TO_WAREHOUSE);

		Criteria criteria = wrapper.getCriteria();

		setLimitOffset(criteria, of);
		List<Product> products = extractProducts(wrapper);
		wrapper.closeSession();
		return products;
	}

	static void setLimitOffset(Criteria criteria, Pageable of) {

		criteria.setFirstResult(Long.valueOf(of.getOffset()).intValue());
		criteria.setMaxResults(of.getPageSize());
	}

	private List<Product> findNotEmptyProductInSpecifiedWarehouseWithExpDateBefore(Long locationId, Date expDateBefore,
			Pageable of) {

		CriteriaWrapper wrapper = commonStockCriteria(locationId, TransactionType.TRANS_OUT_TO_WAREHOUSE);
		Criteria criteria = wrapper.getCriteria();
		criteria.add(Restrictions.lt("expiredDate", expDateBefore));

		setLimitOffset(criteria, of);

		List<Product> products = extractProducts(wrapper);
		wrapper.closeSession();
		return products;
	}

	private List<Product> findNotEmptyProductInSpecifiedWarehouseWithExpDateBeforeAfter(Long locationId,
			Date expDateBefore, Date expDateAfter, Pageable of) {

		CriteriaWrapper wrapper = commonStockCriteria(locationId, TransactionType.TRANS_OUT_TO_WAREHOUSE);
		Criteria criteria = wrapper.getCriteria();
		criteria.add(Restrictions.between("expiredDate", expDateAfter, expDateBefore));

		setLimitOffset(criteria, of);
		List<Product> products = extractProducts(wrapper);
		wrapper.closeSession();
		System.out.println(products);
		return products;

	}

	/////////////// MASTER /////////////////////
	 

	private List<Product> findNotEmptyProductInMasterWarehouse(Pageable of) {

		CriteriaWrapper wrapper = commonStockCriteria(null, TransactionType.TRANS_IN);
		setLimitOffset(wrapper.getCriteria(), of);
		List<Product> products = extractProducts(wrapper);
		System.out.println(products);
		return products;
	}

	private List<Product> findNotEmptyProductInMasterWarehouseWithExpDateBefore(Date expDateBefore, Pageable of) {

		CriteriaWrapper wrapper = commonStockCriteria(null, TransactionType.TRANS_IN);
		Criteria criteria = wrapper.getCriteria();
		criteria.add(Restrictions.lt("expiredDate", expDateBefore));
		setLimitOffset(criteria, of);
		List<Product> products = extractProducts(wrapper);
		return products;
	}

	private List<Product> findNotEmptyProductInMasterWarehouseWithExpDateBeforeAfter(Date expDateBefore,
			Date expDateAfter, Pageable of) {
		CriteriaWrapper wrapper = commonStockCriteria(null, TransactionType.TRANS_IN);
		Criteria criteria = wrapper.getCriteria();
		criteria.add(Restrictions.between("expiredDate", expDateAfter, expDateBefore));
		setLimitOffset(criteria, of);
		List<Product> products = extractProducts(wrapper);
		return products;
	}

	////////////////////// ALL LOCATION ////////////////// 
	 
	private List<Product> findNotEmptyProductAllLocation(Pageable of) {
		 
		CriteriaWrapper wrapper = commonStockCriteria(null, TransactionType.TRANS_IN, TransactionType.TRANS_OUT_TO_WAREHOUSE);
		Criteria criteria = wrapper.getCriteria();
		setLimitOffset(criteria, of);
		List<Product> products = extractProducts(wrapper);
		wrapper.closeSession();
		return products;
	}
	
	private List<Product> findNotEmptyProductAllLocationWithExpDateBefore(Date expDateBefore, Pageable of) {
		 
		CriteriaWrapper wrapper = commonStockCriteria(null, TransactionType.TRANS_IN, TransactionType.TRANS_OUT_TO_WAREHOUSE);
		Criteria criteria = wrapper.getCriteria();
		criteria.add(Restrictions.lt("expiredDate", expDateBefore));
		setLimitOffset(criteria, of);
		List<Product> products = extractProducts(wrapper);
		wrapper.closeSession();
		return products;
	}
	
	private List<Product> findNotEmptyProductAllLocationWithExpDateBeforeAfter(Date expDateBefore, Date expDateAfter,
			Pageable of) {
		 
		CriteriaWrapper wrapper = commonStockCriteria(null, TransactionType.TRANS_IN, TransactionType.TRANS_OUT_TO_WAREHOUSE);
		Criteria criteria = wrapper.getCriteria();
		criteria.add(Restrictions.between("expiredDate", expDateAfter, expDateBefore));
		setLimitOffset(criteria, of);

		List<Product> products = extractProducts(wrapper);
		wrapper.closeSession();
		return products;
	}

	
	////////////////////////////////////////////////

	private List<Product> extractProducts(CriteriaWrapper wrapper) {
		List result = wrapper.getCriteria().list();
		List ids = new ArrayList<>();
		for (Object object : result) {
			Object[] o = (Object[]) object;
			ids.add(o[0]);
		}
		List<Product> products = getProductWhereIdIn(wrapper.getSession(), ids);
		return products;
	}

	private List<Product> getProductWhereIdIn(Session session, List ids) {
		if (ids == null || ids.isEmpty()) {
			return new ArrayList<>();
		}
		Criteria criteria = session.createCriteria(Product.class);
		criteria.add(Restrictions.in("id", ids));
		List<Product> products = criteria.list();
		products.sort(comp());
		return products;
	}

	private static Comparator<Product> comp() {
		return new Comparator<Product>() {
			@Override
			public int compare(Product o1, Product o2) {
				return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
			}
		};
	}
}
