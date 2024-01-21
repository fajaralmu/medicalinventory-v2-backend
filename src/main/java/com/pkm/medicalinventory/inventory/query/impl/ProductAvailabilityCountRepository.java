package com.pkm.medicalinventory.inventory.query.impl;

import static com.pkm.medicalinventory.constants.TransactionType.TRANS_IN;
import static com.pkm.medicalinventory.constants.TransactionType.TRANS_OUT_TO_WAREHOUSE;
import static java.lang.Integer.MIN_VALUE;

import java.math.BigInteger;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import com.pkm.medicalinventory.constants.TransactionType;
import com.pkm.medicalinventory.dto.Filter;
import com.pkm.medicalinventory.entity.Product;
import com.pkm.medicalinventory.inventory.query.CriteriaWrapper;
import com.pkm.medicalinventory.inventory.query.InventoryRepository; 

@Service
public class ProductAvailabilityCountRepository extends InventoryRepository {

	
	private String getProductNameFilter(Filter filter) {
		return filter.getFieldsFilterValue("name") == null ?"":filter.getFieldsFilterValue("name").toString();
	}
	
	/**
	 * 
	 * @param isMasterHealthCenter
	 * @param expDaysWithin
	 * @param locationId
	 * @return
	 */
	public BigInteger countNontEmptyProduct(boolean isMasterHealthCenter, @Nullable Integer expDaysWithin, Filter filter,
			Long locationId) {
		BigInteger totalData;
		boolean withExpDateFilter = expDaysWithin != null;
		int expDatAfter = expDaysWithin != null && expDaysWithin > 0 ? 0 : MIN_VALUE;
		if (isMasterHealthCenter) {
			if (withExpDateFilter) {
				totalData = countNotEmptyProductInMasterWareHouseWithExpDaysBeforeAfter(expDaysWithin + 1, expDatAfter, filter);

			} else {
				totalData = countNotEmptyProductInMasterWareHouse(filter);
			}
		} else {
			if (withExpDateFilter) {
				totalData = countNotEmptyProductInSpecifiedWareHouseWithExpDaysBeforeAfter(locationId,
						expDaysWithin + 1, expDatAfter, filter);
			} else {
				totalData = countNotEmptyProductInSpecifiedWareHouse(locationId, filter);
			}
		}

		return totalData;
	}

	public BigInteger countNontEmptyProductAllLocation(boolean isMasterHealthCenter, Integer expDaysWithin, Filter filter) {
		BigInteger totalData;
		boolean withExpDateFilter = expDaysWithin != null;
		if (withExpDateFilter) {
			int expDatAfter = expDaysWithin > 0 ? 0 : MIN_VALUE;
			totalData = countNotEmptyProductAllLocationWithExpDaysBeforeAfter(expDaysWithin + 1, expDatAfter, filter);

		} else {
			totalData = countNotEmptyProductAllLocation(filter);
		}
		return totalData;
	}

	@Override
	protected CriteriaWrapper commonStockCriteria(Long locationId) {

		CriteriaWrapper wrapper = super.commonStockCriteria(locationId);
		wrapper.getCriteria().setProjection(null);
		wrapper.getCriteria().createAlias("product", "product");
		wrapper.getCriteria().add(Property.forName("product").in(productDetachedCriteria()));
		wrapper.getCriteria().setProjection(Projections.distinct(Projections.property("product.id")));
		return wrapper;
	}
	
	private CriteriaWrapper commonCriteria(@Nullable Long locationId, Filter filter,TransactionType...transactionTypes) {
		CriteriaWrapper wrapper = super.commonStockCriteria(locationId, transactionTypes);
		wrapper.getCriteria().add(Restrictions.ilike("product.name", "%"+getProductNameFilter(filter)+"%"));
		return wrapper;
	}

	private BigInteger countNotEmptyProductInMasterWareHouseWithExpDaysBeforeAfter(int expDayDiffBefore,
			int expDayDiffAfter, Filter filter) {
		try (CriteriaWrapper wrapper = commonCriteria(null, filter,  TRANS_IN)) {
			Criteria criteria = wrapper.getCriteria();
			criteria.add(
					Restrictions.sqlRestriction("extract(day from expired_date - current_timestamp) between ? and ?",
							new Object[] { expDayDiffAfter, expDayDiffBefore },
							new Type[] { IntegerType.INSTANCE, IntegerType.INSTANCE }));

			List result = criteria.list();
			BigInteger count = BigInteger.valueOf(result.size());

			return count;
		} catch (Exception e) {

			e.printStackTrace();
			return BigInteger.ZERO;
		}
	}

	private BigInteger countNotEmptyProductInMasterWareHouse(Filter filter) {

		try (CriteriaWrapper wrapper = commonCriteria(null, filter,  TRANS_IN)) {
			Criteria criteria = wrapper.getCriteria();

			List result = criteria.list();
			BigInteger count = BigInteger.valueOf(result.size());
			return count;
		} catch (Exception e) {

			e.printStackTrace();
			return BigInteger.ZERO;
		}
	}

	private BigInteger countNotEmptyProductInSpecifiedWareHouse(Long locationId, Filter filter) {

		try (CriteriaWrapper wrapper = commonCriteria(locationId,filter, TRANS_OUT_TO_WAREHOUSE)) {
			Criteria criteria = wrapper.getCriteria();

			List result = criteria.list();
			BigInteger count = BigInteger.valueOf(result.size());

			return count;
		} catch (Exception e) {

			e.printStackTrace();
			return BigInteger.ZERO;
		}

	}

	private BigInteger countNotEmptyProductInSpecifiedWareHouseWithExpDaysBeforeAfter(Long locationId,
			Integer expiredDaysDiffBefore, Integer expDayDiffAfter, Filter filter) {

		try (CriteriaWrapper wrapper = commonCriteria(locationId,filter, TRANS_OUT_TO_WAREHOUSE)) {
			Criteria criteria = wrapper.getCriteria();
			criteria.add(Restrictions.sqlRestriction(
					" extract(day from this_.expired_date - current_timestamp) between ? and ?",
					new Object[] { expDayDiffAfter, expiredDaysDiffBefore },
					new Type[] { IntegerType.INSTANCE, IntegerType.INSTANCE }));
			criteria.add(Property.forName("product").in(productDetachedCriteria()));
			List result = criteria.list();
			BigInteger count = BigInteger.valueOf(result.size());

			return count;
		} catch (Exception e) {

			e.printStackTrace();
			return BigInteger.ZERO;
		}
	}

	private BigInteger countNotEmptyProductAllLocationWithExpDaysBeforeAfter(Integer expDayDiffBefore,
			Integer expDayDiffAfter, Filter filter) {

		try (CriteriaWrapper wrapper = commonCriteria(null, filter,  TRANS_OUT_TO_WAREHOUSE, TRANS_IN)) {
			Criteria criteria = wrapper.getCriteria();
			criteria.add(Restrictions.sqlRestriction(
					" extract(day from this_.expired_date - current_timestamp) between ? and ?",
					new Object[] { expDayDiffAfter, expDayDiffBefore },
					new Type[] { IntegerType.INSTANCE, IntegerType.INSTANCE }));
			List result = criteria.list();

			BigInteger count = BigInteger.valueOf(result.size());
			return count;
		} catch (Exception e) {

			e.printStackTrace();
			return BigInteger.ZERO;
		}
	}

	private BigInteger countNotEmptyProductAllLocation(Filter filter) {

		try (CriteriaWrapper wrapper = commonCriteria(null, filter, TRANS_OUT_TO_WAREHOUSE, TRANS_IN)) {
			Criteria criteria = wrapper.getCriteria();
			List result = criteria.list();

			BigInteger count = BigInteger.valueOf(result.size());
			return count;
		} catch (Exception e) {

			e.printStackTrace();
			return BigInteger.ZERO;
		}
	}

	private DetachedCriteria productDetachedCriteria() {
		DetachedCriteria ownerCriteria = DetachedCriteria.forClass(Product.class);
		ownerCriteria.setProjection(Property.forName("id"));
		return ownerCriteria;
	}

}
