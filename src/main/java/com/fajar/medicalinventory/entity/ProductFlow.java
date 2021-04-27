/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fajar.medicalinventory.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import com.fajar.medicalinventory.annotation.CustomEntity;
import com.fajar.medicalinventory.dto.model.ProductFlowModel;
import com.fajar.medicalinventory.dto.model.TransactionModel;
import com.fajar.medicalinventory.exception.ApplicationException;
import com.fajar.medicalinventory.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @author fajar
 */
@CustomEntity
@Component
@Entity
@Table(name = "product_flow")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductFlow extends BaseEntity<ProductFlowModel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8839593046741372229L;

//	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "transaction_id", nullable = false) 
	private Transaction transaction;

	@ManyToOne
	@JoinColumn(name = "product_id", nullable = false) 
	private Product product;

	@Column(name="expired_date") 
	private Date expiredDate;
	@Column 
	private int count;
	@Column(name="used_count", nullable = false) 
	private int usedCount;
//	@Column(name="reference_flow_id", nullable = false) 
//	private Long refStockId;
	
	@Nullable
	@ManyToOne
	@JoinColumn(name = "reference_flow_id")
	@Setter(value = AccessLevel.NONE) 
	private ProductFlow referenceProductFlow;

	@Column
	@Default 
	private boolean suitable = true;
	@Column 
	private double price;
	@Column 
	private boolean generic;  
	 
	@Transient
	private List<ProductFlow> referencingItems;
	
	public void addUsedCount(int count) {
		 
		if (getStock() - count < 0) {
			throw new ApplicationException("Stock not enough: "+(getStock() - count));
		}
		setUsedCount(getUsedCount()+count);
	}
	
	
	public int getStock() { 
		return count - usedCount;
	}
	
	////////////

	public static int sumStockCount(List<ProductFlow> productFlows) {
		int sum = 0;
		for (ProductFlow productFlow : productFlows) {
			sum+=productFlow.getStock();
		}
		return sum;
	}
	public static int sumQtyCount(List<ProductFlow> productFlows) {
		int sum = 0;
		for (ProductFlow productFlow : productFlows) {
			sum+=productFlow.getCount();
		}
		return sum;
	}

	public void resetUsedCount() {
		setUsedCount(0);
	}
	public boolean productsEquals(Product p) {
		if (product == null) return false;
		return product.idEquals(p);
	} 
	
	
	/**
	 * make the expDate same as referenceProductFlow.expDate
	 */
	public void copyFromReferenceFlow() {
		if (null == referenceProductFlow) {
			return;
		}
		setExpiredDate(referenceProductFlow.getExpiredDate());
		setGeneric(referenceProductFlow.isGeneric());
		setPrice(referenceProductFlow.getPrice());
	}


	public void setReferenceProductFlow(ProductFlow referenceFlow) {
		if (null != referenceFlow && null != referenceFlow.getProduct()) {
			setProduct(referenceFlow.getProduct());
		}
		this.referenceProductFlow = referenceFlow;
		this.copyFromReferenceFlow();
	}
	
	@JsonIgnore
	public Long getTransactionId() {
		if (null == transaction) return null;
		return transaction.getId();
	}
	/**
	 * distributed to customer/to branch warehouse
	 * @return
	 */
	@JsonIgnore
	public boolean isDistributed  () {
		return null != referenceProductFlow;
	}
	
	/**
	 *  month starts at 1
	 * @return month starts at 1
	 */
	@JsonIgnore
	public int getTransactionMonth () {
		if (null == transaction) return 1;
		Date date = transaction.getTransactionDate();
		return DateUtil.getCalendarMonth(date) + 1;
	}
	public int getTransactionYear () {
		if (null == transaction) return 0;
		Date date = transaction.getTransactionDate();
		return DateUtil.getCalendarYear(date);
	}


	public static double sumQtyAndPrice(List<ProductFlow> list) {
		double result = 0;
		for (ProductFlow productFlow : list) {
			double priceAndCount = productFlow.getPrice() * productFlow.getCount();
			result += priceAndCount;
		}
		return result;
	}
	
	
	@Override
	public ProductFlowModel toModel() {
		ProductFlowModel model = super.toModel();
		if (referencingItems!=null) {
			List<ProductFlowModel> refItems = new ArrayList<>();
			for (ProductFlow productFlow : referencingItems) {
			refItems.add(productFlow.toModel());
			}
			model.setReferencingItems(refItems);
		}
		return model;
	}
	 
	
	public static void main(String[] args) {
		List<ProductFlow> items = new ArrayList<>();
		items.add(ProductFlow.builder().count(111).build());
		items.add(ProductFlow.builder().count(5).build());
		items.add(ProductFlow.builder().count(101).build());
		Transaction trx = Transaction.builder().code("123").build();
		ProductFlow pf = ProductFlow.builder()
				.referencingItems(items )
//				transaction(trx )
				.build();
		trx.addProductFlow(pf);
		TransactionModel model = trx.toModel();
		ProductFlowModel pfModel = model.getProductFlows().get(0);
		System.out.println(pfModel.getReferencingItems());
		
	}
 
}
