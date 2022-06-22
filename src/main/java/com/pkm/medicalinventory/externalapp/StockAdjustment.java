package com.pkm.medicalinventory.externalapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pkm.medicalinventory.constants.TransactionType;
import com.pkm.medicalinventory.entity.ProductFlow;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

public class StockAdjustment {

	static Session session;
	static Map<Long, ProductFlow> productFlowMap = new HashMap<Long, ProductFlow>();

	public static void main(String[] args) {
		recalculateStock();
	}
	public static void recalculateStock(){

		session = HibernateSessions.setSession();
		Transaction tx = null;
		try {

			tx = session.beginTransaction();
			Query query = session.createQuery("select pf from ProductFlow pf left join pf.transaction tx "
					+ " where tx.type = ? or tx.type = ? ");
			
			query.setString(0, TransactionType.TRANS_IN.toString());
			query.setString(1, TransactionType.TRANS_OUT_TO_WAREHOUSE.toString());
			 
			List productSupplyFlows = query.list();
			System.out.println("productSupplyFlows: "+productSupplyFlows.size());
			for (Object productSupplyFlow : productSupplyFlows) {
				ProductFlow pf = (ProductFlow) productSupplyFlow;
				pf.resetUsedCount();
				productFlowMap.put(pf.getId(), pf);
			}

			Criteria criteriaUsed = session.createCriteria(ProductFlow.class);
			criteriaUsed.add(Restrictions.isNotNull("referenceProductFlow"));
			List productUsedFlows = criteriaUsed.list();
			 
			for (Object object : productUsedFlows) {
				ProductFlow pf = (ProductFlow) object;
				productFlowMap.get(pf.getReferenceProductFlow().getId()).addUsedCount(pf.getCount());

			}
			System.out.println("SUPPLY: "+productFlowMap.keySet().size());
			for (Long id : productFlowMap.keySet()) {
				session.merge(productFlowMap.get(id));
				System.out.println("update: "+ id);
			}
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			if (tx != null) {
				System.out.println("ROLLING BACK");
				tx.rollback();
			}
		} finally {
			session.close();
			System.exit(0);
		}
	}
	/**
	 * adjust transaction dates
	 * 
	 */
	public static void main2(String[] args) {
		String id_raw = "5357\r\n" + 
				"5829";
		session = HibernateSessions.setSession();
		Transaction tx = session.beginTransaction();
		String[] flowids = id_raw.split("\r\n");
		List<ProductFlow> flows = new ArrayList<>();
		for (int i = 0; i < flowids.length; i++) {
			ProductFlow pf = (ProductFlow) session.get(ProductFlow.class, Long.valueOf(flowids[i]));
			flows.add(pf);
		}
		Map<Long, com.pkm.medicalinventory.entity.Transaction> editedTranMap = new HashMap<>();
		for (ProductFlow productFlow : flows) {
			com.pkm.medicalinventory.entity.Transaction refTransactionSupply = productFlow.getReferenceProductFlow().getTransaction();
			com.pkm.medicalinventory.entity.Transaction refTransaction = productFlow .getTransaction();
			System.out.println("refTransactionSupply:"+refTransactionSupply.getId()+" at "+refTransactionSupply.getTransactionDate());
			System.out.println("refTransaction: "+refTransaction.getId()+" at "+refTransaction.getTransactionDate());
			boolean usedBeforeSupply = refTransaction.getTransactionDate().before(refTransactionSupply.getTransactionDate());
			System.out.println("usedBeforeSupply: "+usedBeforeSupply);
			System.out.println("");
			if (null == editedTranMap.get(refTransactionSupply.getId()) ) {
				editedTranMap.put(refTransactionSupply.getId(), refTransactionSupply);
//				session.merge(refTransactionSupply);
			}
			if (editedTranMap.get(refTransactionSupply.getId()).getTransactionDate().after( refTransaction.getTransactionDate())) {
				System.out.println("WILL UPDATE");
				editedTranMap.get(refTransactionSupply.getId()).setTransactionDate(refTransaction.getTransactionDate());
			} else {
				System.out.println("##NO UPDATE");
			}
			
		}
		tx.commit();
		session.close();
		System.exit(0);
	}
	/**
	 * mismatch productFlow.product_id and productFlowReference.product_id
	 * select  pf.id flow_id, pf_ref.id ref_id,p.id, p.name _NAME,p_ref.id , p_ref.name REF_NAME from product_flow pf
left join product p on p.id = pf.product_id
left join "transaction" tx on tx.id = pf.transaction_id
left join product_flow pf_ref on pf_ref.id = pf.reference_flow_id
left join "transaction" tx_ref on pf_ref.transaction_id = tx_ref.id
left join product p_ref on p_ref.id = pf_ref.product_id
where p_ref.id != p.id
	 */
	/**
	 * GET DIFF DATE between supply and used
	 * select 
tx.transaction_date as date_used, tx_ref.transaction_date as date_supply, tx_ref.id as supply_id, pf.id as used_flow_id 
from product_flow pf 
left join product_flow pf_ref on pf_ref.id = pf.reference_flow_id
left join "transaction" tx on tx.id = pf.transaction_id
left join "transaction" tx_ref on tx_ref.id = pf_ref.transaction_id
where tx_ref.transaction_date > tx.transaction_date and pf.reference_flow_id is not null
order by tx_ref.id, tx.transaction_date 
	 */
	static final String FLOW_IDS = "5492\r\n" + 
			"5507\r\n" + 
			"5471\r\n" + 
			"5571\r\n" + 
			"5570\r\n" + 
			"5578\r\n" + 
			"5753\r\n" + 
			"5511\r\n" + 
			"5501\r\n" + 
			"5727\r\n" + 
			"5799\r\n" + 
			"5490\r\n" + 
			"5751\r\n" + 
			"5843\r\n" + 
			"5844\r\n" + 
			"5845\r\n" + 
			"5494\r\n" + 
			"5529\r\n" + 
			"5754\r\n" + 
			"5432\r\n" + 
			"1468\r\n" + 
			"1469\r\n" + 
			"5433\r\n" + 
			"5491\r\n" + 
			"5495\r\n" + 
			"5508\r\n" + 
			"5435\r\n" + 
			"5434\r\n" + 
			"5553\r\n" + 
			"5552\r\n" + 
			"5568\r\n" + 
			"5745\r\n" + 
			"5461\r\n" + 
			"5456\r\n" + 
			"5732\r\n" + 
			"5500\r\n" + 
			"5506\r\n" + 
			"5532\r\n" + 
			"5484\r\n" + 
			"5493\r\n" + 
			"5739\r\n" + 
			"5583\r\n" + 
			"5431\r\n" + 
			"5738\r\n" + 
			"5441\r\n" + 
			"5581\r\n" + 
			"5771\r\n" + 
			"5569\r\n" + 
			"5725\r\n" + 
			"5604\r\n" + 
			"5804\r\n" + 
			"1467";
	
	
}
