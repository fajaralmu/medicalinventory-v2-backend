package com.fajar.medicalinventory.service.inventory;

import static com.fajar.medicalinventory.constants.TransactionType.TRANS_IN;
import static com.fajar.medicalinventory.constants.TransactionType.TRANS_OUT_TO_WAREHOUSE;
import static com.fajar.medicalinventory.util.DateUtil.clock00Midnight;
import static com.fajar.medicalinventory.util.DateUtil.clock24Midnight;
import static org.hibernate.criterion.Restrictions.between;
import static org.hibernate.criterion.Restrictions.lt;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.BigIntegerType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import com.fajar.medicalinventory.constants.TransactionType;
import com.fajar.medicalinventory.dto.Filter;
import com.fajar.medicalinventory.entity.ProductFlow;
import com.fajar.medicalinventory.querybuilder.CriteriaBuilder;
import com.fajar.medicalinventory.service.entity.CommonFilterResult;
import com.fajar.medicalinventory.util.DateUtil;

@Service
public class ProductStockRepositoryv2 extends CommonRepository {

	private static final String FIELD_EXP_DATE = "expiredDate";

	@Override
	protected CriteriaWrapper commonStockCriteria(Long locationId) {

		CriteriaWrapper wrapper = super.commonStockCriteria(locationId);
		Type[] type = { new BigIntegerType() };
		wrapper.getCriteria().setProjection(
				Projections.sqlProjection("sum(count - used_count) as stock", new String[] { "stock" }, type));

		return wrapper;
	}
	
	private static void createAlias(String path, String alias, Criteria...criterias) {
		for (int i = 0; i < criterias.length; i++) {
			criterias[i].createAlias(path, alias);
		}
	}
	
	public CommonFilterResult<ProductFlow> filter(Filter filter) {
		
		Session session = getSession(); 
		Criteria listCriteria =  new CriteriaBuilder(session, ProductFlow.class, filter).createCriteria();
		Criteria countCriteria =  new CriteriaBuilder(session, ProductFlow.class, filter).createRowCountCriteria();
		
		createAlias("transaction.healthCenterLocation", "healthCenterLocation", listCriteria, countCriteria);
		createAlias("transaction.healthCenterDestination", "healthCenterDestination", listCriteria, countCriteria);
		
		
		Criterion transTypeFilter = Restrictions.in("transaction.type", new TransactionType[] {
				TRANS_IN,TRANS_OUT_TO_WAREHOUSE
		});
		
		Criterion stockFilter = Restrictions.sqlRestriction("(this_.count - this_.used_count) > ?", 0 , IntegerType.INSTANCE);
		
		if(filter.getFieldsFilterValue("location")!=null) {
			String location = filter.getFieldsFilterValue("location").toString();
			Criterion transInLocation = Restrictions.and(
					Restrictions.eq("transaction.type", TRANS_IN),
					Restrictions.ilike("healthCenterLocation.name", location, MatchMode.ANYWHERE)
					);
			Criterion transOutLocation = Restrictions.and(
					Restrictions.eq("transaction.type", TRANS_OUT_TO_WAREHOUSE),
					Restrictions.ilike("healthCenterDestination.name", location, MatchMode.ANYWHERE)
					);
			Criterion locationFilter = Restrictions.or(transInLocation, transOutLocation);
			listCriteria.add(locationFilter);
			countCriteria.add(locationFilter);
		}
		listCriteria.add(stockFilter);
		listCriteria.add(transTypeFilter);
		countCriteria.add(stockFilter);
		countCriteria.add(transTypeFilter);
		
		BigInteger count = bigint(countCriteria.uniqueResult());
		List list = listCriteria.list();
		
		session.close();
		
		return CommonFilterResult.listAndCount(list, count.intValue());
		
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

		try (CriteriaWrapper criteria = getTotalItemsAtBranchCriteria(locationId)) {
			return bigintResult(criteria);
		} catch (Exception e) {
			e.printStackTrace();
			return BigInteger.ZERO;
		}
	}

	private BigInteger getTotalItemsAtBranchWarehouseAndExpDateBefore(long locationId, Date expBefore) {

		try (CriteriaWrapper wrapper = getTotalItemsAtBranchCriteria(locationId)) {
			wrapper.getCriteria().add(lt(FIELD_EXP_DATE, expBefore));
			return bigintResult(wrapper);
			
		} catch (Exception e) {
			e.printStackTrace();
			return BigInteger.ZERO;
		}
	}

	private BigInteger getTotalItemsAtBranchWarehouseAndExpDateBeforeAfter(Long locationId, Date expBefore,
			Date expAfter) {

		try (CriteriaWrapper wrapper = getTotalItemsAtBranchCriteria(locationId)) {
			wrapper.getCriteria().add(between(FIELD_EXP_DATE, expBefore, expAfter));
			return bigintResult(wrapper);
			
		} catch (Exception e) {
			e.printStackTrace();
			return BigInteger.ZERO;
		}
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
		return commonStockCriteria(null, TRANS_IN, TransactionType.TRANS_OUT_TO_WAREHOUSE);
	}

	private BigInteger getTotalItemsAllLocation() {

		try (CriteriaWrapper wrapper = getTotalItemsAllLocationCriteria()) {
			return bigintResult(wrapper);
		} catch (Exception e) {
			e.printStackTrace();
			return BigInteger.ZERO;
		}
	}

	private BigInteger getTotalItemsAllLocationAndExpDateBeforeAndAfter(Date before, Date after) {

		try (CriteriaWrapper wrapper = getTotalItemsAllLocationCriteria()) {
			wrapper.getCriteria().add(between(FIELD_EXP_DATE, before, after));
			return bigintResult(wrapper);
		} catch (Exception e) {
			e.printStackTrace();
			return BigInteger.ZERO;
		}
	}

	private BigInteger getTotalItemsAllLocationAndExpDateBefore(Date before) {

		try (CriteriaWrapper wrapper = getTotalItemsAllLocationCriteria()) {
			wrapper.getCriteria().add(lt(FIELD_EXP_DATE, before));
			return bigintResult(wrapper);
		} catch (Exception e) {
			e.printStackTrace();
			return BigInteger.ZERO;
		}
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
		return commonStockCriteria(null, TRANS_IN);
	}

	private BigInteger getTotalItemsAtMasterWarehouse() {

		try (CriteriaWrapper wrapper = getTotalItemsAtMasterCriteria()) {
			return bigintResult(wrapper);
		} catch (Exception e) {
			e.printStackTrace();
			return BigInteger.ZERO;
		}
	}

	private BigInteger getTotalItemsAtMasterWarehouseAndExpDateBefore(Date before) {

		try (CriteriaWrapper wrapper = getTotalItemsAtMasterCriteria()) {
			wrapper.getCriteria().add(lt(FIELD_EXP_DATE, before));
			return bigintResult(wrapper);
		} catch (Exception e) {
			e.printStackTrace();
			return BigInteger.ZERO;
		}
	}

	private BigInteger getTotalItemsAtMasterWarehouseAndExpDateBeforeAndAfter(Date before, Date after) {

		try (CriteriaWrapper wrapper = getTotalItemsAtMasterCriteria()) {
			wrapper.getCriteria().add(between(FIELD_EXP_DATE, before, after));
			return bigintResult(wrapper);
		} catch (Exception e) {
			e.printStackTrace();
			return BigInteger.ZERO;
		}

	}
}
