package com.fajar.medicalinventory.service.inventory;

import static com.fajar.medicalinventory.constants.TransactionType.TRANS_OUT_TO_WAREHOUSE;
import static com.fajar.medicalinventory.util.DateUtil.clock00Midnight;
import static com.fajar.medicalinventory.util.DateUtil.clock24Midnight;
import static org.hibernate.criterion.Restrictions.between;
import static org.hibernate.criterion.Restrictions.lt;

import java.math.BigInteger;
import java.util.Date;

import org.hibernate.Criteria;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import com.fajar.medicalinventory.constants.TransactionType;
import com.fajar.medicalinventory.util.DateUtil;

@Service
public class ProductStockRepositoryv2 extends CommonRepository {

	private static final String FIELD_EXP_DATE = "expiredDate";

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

	private Criteria getTotalItemsAtBranchCriteria(long locationId) {
		return commonStockCriteria(locationId, TRANS_OUT_TO_WAREHOUSE);
	}

	private BigInteger getTotalItemsAtBranchWarehouse(long locationId) {

		Criteria criteria = getTotalItemsAtBranchCriteria(locationId);
		return bigintResult(criteria);
	}

	private BigInteger getTotalItemsAtBranchWarehouseAndExpDateBefore(long locationId, Date expBefore) {

		Criteria criteria = getTotalItemsAtBranchCriteria(locationId).add(lt(FIELD_EXP_DATE, expBefore));

		return bigintResult(criteria);
	}

	private BigInteger getTotalItemsAtBranchWarehouseAndExpDateBeforeAfter(Long locationId, Date expBefore,
			Date expAfter) {

		Criteria criteria = getTotalItemsAtBranchCriteria(locationId)
				.add(between(FIELD_EXP_DATE, expBefore, expAfter));

		return bigintResult(criteria);
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

	private Criteria getTotalItemsAllLocationCriteria() {
		return commonStockCriteria(null, TransactionType.TRANS_IN, TransactionType.TRANS_OUT_TO_WAREHOUSE);
	}

	private BigInteger getTotalItemsAllLocation() {

		Criteria criteria = getTotalItemsAllLocationCriteria();
		return bigintResult(criteria);
	}

	private BigInteger getTotalItemsAllLocationAndExpDateBeforeAndAfter(Date before, Date after) {

		Criteria criteria = getTotalItemsAllLocationCriteria().add(between(FIELD_EXP_DATE, before, after));
		return bigintResult(criteria);
	}

	private BigInteger getTotalItemsAllLocationAndExpDateBefore(Date before) {

		Criteria criteria = getTotalItemsAllLocationCriteria().add(lt(FIELD_EXP_DATE, before));
		return bigintResult(criteria);
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

	private Criteria getTotalItemsAtMasterCriteria() {
		return commonStockCriteria(null, TransactionType.TRANS_IN);
	}

	private BigInteger getTotalItemsAtMasterWarehouse() {

		Criteria criteria = getTotalItemsAtMasterCriteria();
		return bigintResult(criteria);
	}

	private BigInteger getTotalItemsAtMasterWarehouseAndExpDateBefore(Date before) {

		Criteria criteria = getTotalItemsAtMasterCriteria().add(lt(FIELD_EXP_DATE, before));
		return bigintResult(criteria);
	}

	private BigInteger getTotalItemsAtMasterWarehouseAndExpDateBeforeAndAfter(Date before, Date after) {

		Criteria criteria = getTotalItemsAtMasterCriteria().add(between(FIELD_EXP_DATE, before, after));
		return bigintResult(criteria);

	}
}
