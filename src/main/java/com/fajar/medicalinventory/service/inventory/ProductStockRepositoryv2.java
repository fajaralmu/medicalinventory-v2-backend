package com.fajar.medicalinventory.service.inventory;

import static com.fajar.medicalinventory.constants.TransactionType.TRANS_OUT_TO_WAREHOUSE;
import static com.fajar.medicalinventory.util.DateUtil.clock00Midnight;
import static com.fajar.medicalinventory.util.DateUtil.clock24Midnight;
import static org.hibernate.criterion.Restrictions.between;
import static org.hibernate.criterion.Restrictions.lt;

import java.math.BigInteger;
import java.util.Date;

import org.hibernate.criterion.Projections;
import org.hibernate.type.BigIntegerType;
import org.hibernate.type.Type;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import com.fajar.medicalinventory.constants.TransactionType;
import com.fajar.medicalinventory.util.DateUtil;

@Service
public class ProductStockRepositoryv2 extends CommonRepository {

	private static final String FIELD_EXP_DATE = "expiredDate";
	
	@Override
	protected CriteriaWrapper commonStockCriteria(Long locationId) {
		
		CriteriaWrapper wrapper = super.commonStockCriteria(locationId);
		Type[] type = {new BigIntegerType()};
		wrapper.getCriteria().setProjection(Projections.sqlProjection("sum(count - used_count) as stock", new String[] { "stock" }, type));
		
		return wrapper;
	}

	////////////////// BRANCH WAREHOUSE ///////////////////

	public BigInteger getTotalItemsWillExpireAtBranchWarehouse(long locationId, @Nullable Integer expDaysWithin) {
		if (null != expDaysWithin) {
			Date expiredDateWithin = DateUtil.plusDay(new Date(), expDaysWithin + 1);
			if (expDaysWithin > 0) {
				Date tomorrow = DateUtil.plusDay(new Date(), 1);
				return getTotalItemsAtBranchWarehouseAndExpDateBeforeAfter(locationId,
						clock24Midnight(expiredDateWithin), clock00Midnight(tomorrow));
			}

			return getTotalItemsAtBranchWarehouseAndExpDateBefore(locationId, clock24Midnight(expiredDateWithin));
		}
		return getTotalItemsAtBranchWarehouse(locationId);
	}

	private CriteriaWrapper getTotalItemsAtBranchCriteria(long locationId) {
		return commonStockCriteria(locationId, TRANS_OUT_TO_WAREHOUSE);
	}

	private BigInteger getTotalItemsAtBranchWarehouse(long locationId) {

		CriteriaWrapper criteria = getTotalItemsAtBranchCriteria(locationId);
		return bigintResult(criteria);
	}

	private BigInteger getTotalItemsAtBranchWarehouseAndExpDateBefore(long locationId, Date expBefore) {

		CriteriaWrapper wrapper = getTotalItemsAtBranchCriteria(locationId);
		wrapper.getCriteria().add(lt(FIELD_EXP_DATE, expBefore));

		return bigintResult(wrapper);
	}

	private BigInteger getTotalItemsAtBranchWarehouseAndExpDateBeforeAfter(Long locationId, Date expBefore,
			Date expAfter) {

		CriteriaWrapper wrapper = getTotalItemsAtBranchCriteria(locationId);
		wrapper.getCriteria().add(between(FIELD_EXP_DATE, expBefore, expAfter));

		return bigintResult(wrapper);
	}

	////////////////// ALL WAREHOUSE ///////////////////

	public BigInteger getTotalItemsAllLocation(@Nullable Integer expDaysWithin) {
		if (null != expDaysWithin) {
			Date expiredDateWithin = DateUtil.plusDay(new Date(), expDaysWithin + 1);
			if (expDaysWithin > 0) {
				Date tomorrow = DateUtil.plusDay(new Date(), 1);
				return getTotalItemsAllLocationAndExpDateBeforeAndAfter(expiredDateWithin, tomorrow);
			}
			return getTotalItemsAllLocationAndExpDateBefore(expiredDateWithin);

		}
		return getTotalItemsAllLocation();
	}

	private CriteriaWrapper getTotalItemsAllLocationCriteria() {
		return commonStockCriteria(null, TransactionType.TRANS_IN, TransactionType.TRANS_OUT_TO_WAREHOUSE);
	}

	private BigInteger getTotalItemsAllLocation() {

		CriteriaWrapper wrapper = getTotalItemsAllLocationCriteria();
		return bigintResult(wrapper);
	}

	private BigInteger getTotalItemsAllLocationAndExpDateBeforeAndAfter(Date before, Date after) {

		CriteriaWrapper wrapper = getTotalItemsAllLocationCriteria();
		wrapper.getCriteria().add(between(FIELD_EXP_DATE, before, after));
		return bigintResult(wrapper);
	}

	private BigInteger getTotalItemsAllLocationAndExpDateBefore(Date before) {

		CriteriaWrapper wrapper = getTotalItemsAllLocationCriteria();
		wrapper.getCriteria().add(lt(FIELD_EXP_DATE, before));
		return bigintResult(wrapper);
	}

	////////////////// MASTER WAREHOUSE ///////////////////
	public BigInteger getTotalItemsWillExpireAtMasterWarehouse(@Nullable Integer expDaysWithin) {
		if (null != expDaysWithin) {
			Date expiredDateWithin = DateUtil.plusDay(new Date(), expDaysWithin + 1);
			if (expDaysWithin > 0) {
				Date tomorrow = DateUtil.plusDay(new Date(), 1);
				return getTotalItemsAtMasterWarehouseAndExpDateBeforeAndAfter(expiredDateWithin, tomorrow);
			}
			return getTotalItemsAtMasterWarehouseAndExpDateBefore(expiredDateWithin);

		}
		return getTotalItemsAtMasterWarehouse();
	}

	private CriteriaWrapper getTotalItemsAtMasterCriteria() {
		return commonStockCriteria(null, TransactionType.TRANS_IN);
	}

	private BigInteger getTotalItemsAtMasterWarehouse() {

		CriteriaWrapper wrapper = getTotalItemsAtMasterCriteria();
		return bigintResult(wrapper);
	}

	private BigInteger getTotalItemsAtMasterWarehouseAndExpDateBefore(Date before) {

		CriteriaWrapper wrapper = getTotalItemsAtMasterCriteria();
		wrapper.getCriteria().add(lt(FIELD_EXP_DATE, before));
		return bigintResult(wrapper);
	}

	private BigInteger getTotalItemsAtMasterWarehouseAndExpDateBeforeAndAfter(Date before, Date after) {

		CriteriaWrapper wrapper = getTotalItemsAtMasterCriteria();
		wrapper.getCriteria().add(between(FIELD_EXP_DATE, before, after));
		return bigintResult(wrapper);

	}
}
