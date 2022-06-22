package com.pkm.medicalinventory.service.inventory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.pkm.medicalinventory.constants.TransactionType;
import com.pkm.medicalinventory.dto.Filter;
import com.pkm.medicalinventory.entity.Product;
import com.pkm.medicalinventory.repository.ProductRepository;
import com.pkm.medicalinventory.util.DateUtil;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
public class ProductAvailabilityListRepository extends CommonRepository {

	@Autowired
	private ProductRepository productRepository;

	public List<Product> getAvailableProducts(boolean isMasterHealthCenter, Filter filter, Long locationId) {
		final boolean ignoreEmptyValue = filter.isIgnoreEmptyValue();
		final Integer expDaysWithin = filter.isFilterExpDate() ? filter.getDay() : null;
		List<Product> products = new ArrayList<Product>();

		if (ignoreEmptyValue) {
			boolean withExpDateFilter = expDaysWithin != null;
			Date expiredDateWithin = withExpDateFilter ? DateUtil.plusDay(new Date(), expDaysWithin) : null;
			if (isMasterHealthCenter) {
				products = findNotEmptyProductAtMasterWarehouse(expiredDateWithin, filter);
			} else {
				products = findNotEmptyProductAtBranchWarehouse(expiredDateWithin, filter, locationId);
			}
		} else {
			String nameLowerCased = getProductNameFilter(filter).toLowerCase();
			products = productRepository.findByNameLowerCaseLikeOrderByName(nameLowerCased , filter.getPageRequest());
		}
		return products;
	}

	private String getProductNameFilter(Filter filter) {
		return filter.getFieldsFilterValue("name") == null ?"":filter.getFieldsFilterValue("name").toString();
	}

	public List<Product> findNotEmptyProductAtBranchWarehouse(@Nullable Date expiredDateWithin, Filter filter,
			Long locationId) {

		if (expiredDateWithin != null) {
			boolean afterToday = DateUtil.afterToday(expiredDateWithin);
			if (afterToday) {
				findNotEmptyProductInSpecifiedWarehouseWithExpDateBeforeAfter(locationId, expiredDateWithin, new Date(),
						filter);
			}
			return findNotEmptyProductInSpecifiedWarehouseWithExpDateBefore(locationId, expiredDateWithin, filter);
		} else {
			return findNotEmptyProductInSpecifiedWarehouse(locationId, filter);
		}
	}

	public List<Product> findNotEmptyProductAtMasterWarehouse(@Nullable Date expDateBefore, Filter filter) {
		if (expDateBefore != null) {
			boolean afterToday = DateUtil.afterToday(expDateBefore);
			if (afterToday) {
				return findNotEmptyProductInMasterWarehouseWithExpDateBeforeAfter(expDateBefore, new Date(), filter);
			}
			return findNotEmptyProductInMasterWarehouseWithExpDateBefore(expDateBefore, filter);
		} else {
			return findNotEmptyProductInMasterWarehouse(filter);
		}
	}

	public List<Product> findNotEmptyProductAtAllLocation(@Nullable Date expiredDateWithin, Filter filter) {
		if (expiredDateWithin != null) {
			boolean afterToday = DateUtil.afterToday(expiredDateWithin);
			if (afterToday) {
				return findNotEmptyProductAllLocationWithExpDateBeforeAfter(expiredDateWithin, new Date(), filter);
			}
			return findNotEmptyProductAllLocationWithExpDateBefore(expiredDateWithin, filter);
		} else {
			return findNotEmptyProductAllLocation(filter);
		}
	}

	public List<Product> getAvailableProductsAllLocation(Filter filter) {
		List<Product> products = new ArrayList<>();
		
		final boolean ignoreEmptyValue = filter.isIgnoreEmptyValue();
		final Integer expDaysWithin = filter.isFilterExpDate() ? filter.getDay() : null;
		 
		if (ignoreEmptyValue) {
			boolean withExpDateFilter = expDaysWithin != null;
			Date expiredDateWithin = withExpDateFilter ? DateUtil.plusDay(new Date(), expDaysWithin) : null;
			products = findNotEmptyProductAtAllLocation(expiredDateWithin, filter);

		} else {
			String nameLowerCased = getProductNameFilter(filter).toLowerCase();
			products = productRepository.findByNameLowerCaseLikeOrderByName(nameLowerCased , filter.getPageRequest());
		}
		return products;
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
	
	 
	protected CriteriaWrapper commonCriteria(Long locationId, Filter filter, TransactionType... transactionTypes) {
		
		CriteriaWrapper wrapper = super.commonStockCriteria(locationId, transactionTypes);
		wrapper.getCriteria().add(Restrictions.ilike("product.name", "%"+getProductNameFilter(filter)+"%"));
		return wrapper;
	}

	private static Projection projections() {
		return Projections.projectionList().add(Projections.distinct(Projections.property("product.id")))
				.add(Projections.property("product.name"));
	}

	///////////////// BRANCH ///////////////////

	private List<Product> findNotEmptyProductInSpecifiedWarehouse(Long locationId, Filter filter) {

		try (CriteriaWrapper wrapper = commonCriteria(locationId, filter, TransactionType.TRANS_OUT_TO_WAREHOUSE)) {
			Criteria criteria = wrapper.getCriteria();

			setLimitOffset(criteria, filter.getPageRequest());
			List<Product> products = extractProducts(wrapper);

			return products;
		} catch (Exception e) {

			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	static void setLimitOffset(Criteria criteria, Pageable of) {

		criteria.setFirstResult(Long.valueOf(of.getOffset()).intValue());
		criteria.setMaxResults(of.getPageSize());
	}

	private List<Product> findNotEmptyProductInSpecifiedWarehouseWithExpDateBefore(Long locationId, Date expDateBefore,
			Filter filter) {

		try (CriteriaWrapper wrapper = commonCriteria(locationId, filter, TransactionType.TRANS_OUT_TO_WAREHOUSE)) {
			Criteria criteria = wrapper.getCriteria();
			criteria.add(Restrictions.lt("expiredDate", expDateBefore));

			setLimitOffset(criteria, filter.getPageRequest());

			List<Product> products = extractProducts(wrapper);

			return products;
		} catch (Exception e) {

			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	private List<Product> findNotEmptyProductInSpecifiedWarehouseWithExpDateBeforeAfter(Long locationId,
			Date expDateBefore, Date expDateAfter, Filter filter) {

		try (CriteriaWrapper wrapper = commonCriteria(locationId, filter, TransactionType.TRANS_OUT_TO_WAREHOUSE)) {
			Criteria criteria = wrapper.getCriteria();
			criteria.add(Restrictions.between("expiredDate", expDateAfter, expDateBefore));

			setLimitOffset(criteria, filter.getPageRequest());
			List<Product> products = extractProducts(wrapper);

			return products;
		} catch (Exception e) {

			e.printStackTrace();
			return new ArrayList<>();
		}

	}

	/////////////// MASTER /////////////////////

	private List<Product> findNotEmptyProductInMasterWarehouse(Filter filter) {

		try (CriteriaWrapper wrapper = commonCriteria(null, filter, TransactionType.TRANS_IN)) {
			setLimitOffset(wrapper.getCriteria(), filter.getPageRequest());
			List<Product> products = extractProducts(wrapper);

			return products;
		} catch (Exception e) {

			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	private List<Product> findNotEmptyProductInMasterWarehouseWithExpDateBefore(Date expDateBefore, Filter filter) {

		try (CriteriaWrapper wrapper = commonCriteria(null, filter, TransactionType.TRANS_IN)) {
			Criteria criteria = wrapper.getCriteria();
			criteria.add(Restrictions.lt("expiredDate", expDateBefore));
			setLimitOffset(criteria, filter.getPageRequest());
			List<Product> products = extractProducts(wrapper);
			return products;
		} catch (Exception e) {

			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	private List<Product> findNotEmptyProductInMasterWarehouseWithExpDateBeforeAfter(Date expDateBefore,
			Date expDateAfter, Filter filter) {
		try (CriteriaWrapper wrapper = commonCriteria(null, filter, TransactionType.TRANS_IN)) {
			Criteria criteria = wrapper.getCriteria();
			criteria.add(Restrictions.between("expiredDate", expDateAfter, expDateBefore));
			setLimitOffset(criteria, filter.getPageRequest());
			List<Product> products = extractProducts(wrapper);
			return products;
		} catch (Exception e) {

			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	////////////////////// ALL LOCATION //////////////////

	private List<Product> findNotEmptyProductAllLocation(Filter filter) {

		try (CriteriaWrapper wrapper = commonCriteria(null, filter,TransactionType.TRANS_IN,
				TransactionType.TRANS_OUT_TO_WAREHOUSE)) {
			Criteria criteria = wrapper.getCriteria();
			setLimitOffset(criteria, filter.getPageRequest());
			List<Product> products = extractProducts(wrapper);

			return products;
		} catch (Exception e) {

			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	private List<Product> findNotEmptyProductAllLocationWithExpDateBefore(Date expDateBefore, Filter filter) {

		try (CriteriaWrapper wrapper = commonCriteria(null, filter, TransactionType.TRANS_IN,
				TransactionType.TRANS_OUT_TO_WAREHOUSE)) {
			Criteria criteria = wrapper.getCriteria();
			criteria.add(Restrictions.lt("expiredDate", expDateBefore));
			setLimitOffset(criteria, filter.getPageRequest());
			List<Product> products = extractProducts(wrapper);

			return products;
		} catch (Exception e) {

			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	private List<Product> findNotEmptyProductAllLocationWithExpDateBeforeAfter(Date expDateBefore, Date expDateAfter,
			Filter filter) {

		try (CriteriaWrapper wrapper = commonCriteria(null, filter, TransactionType.TRANS_IN,
				TransactionType.TRANS_OUT_TO_WAREHOUSE)) {
			Criteria criteria = wrapper.getCriteria();
			criteria.add(Restrictions.between("expiredDate", expDateAfter, expDateBefore));
			setLimitOffset(criteria, filter.getPageRequest());

			List<Product> products = extractProducts(wrapper);
			return products;
		} catch (Exception e) {

			e.printStackTrace();
			return new ArrayList<>();
		}
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
