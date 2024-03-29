/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pkm.medicalinventory.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pkm.medicalinventory.annotation.CustomEntity;
import com.pkm.medicalinventory.constants.TransactionType;
import com.pkm.medicalinventory.dto.model.TransactionModel;
import com.pkm.medicalinventory.exception.ApplicationException;
import com.pkm.medicalinventory.util.DateUtil;
import com.pkm.medicalinventory.util.StringUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author fajar
 */
@JsonInclude(value = Include.NON_NULL)
@CustomEntity
@Entity
@Table(name = "transaction")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class Transaction extends BaseEntity<TransactionModel> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1055517081803635273L;
	@Column(unique = true) 
	private String code;
	@Column(name="transaction_date") 
	@Default
	private Date transactionDate = new Date();
	@Column(columnDefinition = "TEXT") 
	private String description;

	@Column 
	@Enumerated(EnumType.STRING)
	private TransactionType type;
	@ManyToOne 
	@JoinColumn(name = "user_id")
	private User user;
	@ManyToOne 
	@JoinColumn(name = "supplier_id")
	@Nullable
	private Supplier supplier;
	@ManyToOne 
	@JoinColumn(name = "customer_id")
	@Nullable
	private Customer customer;
	@ManyToOne // dibutuhkan di stok obat
	@JoinColumn(name = "health_center_destination_id") 
	@Nullable
	private HealthCenter healthCenterDestination;
	
	/**
	 * health center where transaction is performed
	 */
	@ManyToOne
	@JoinColumn(name = "health_center_location_id") 
	private HealthCenter healthCenterLocation;

	@Transient
	@Default
	private List<ProductFlow> productFlows = new ArrayList<>();

	private void generateUniqueCode(TransactionType type) {
		int year = DateUtil.getCalendarYear(transactionDate);
		int month = DateUtil.getCalendarMonth(transactionDate);
		int day = DateUtil.getCalendarDayOfMonth(transactionDate);
		String dateCode = year + StringUtil.twoDigits(month+1) + StringUtil.twoDigits(day);
		this.code = dateCode + type.ordinal()+"-" + StringUtil.generateRandomNumber(6);
	}
	
	/**
	 * determine type 
	 */
	public void setTypeAndCode() {
		TransactionType type;
		if (supplier != null) {
			type = TransactionType.TRANS_IN;
		} else if (customer != null) {
			type = TransactionType.TRANS_OUT;
		} else if (healthCenterDestination != null) {
			type = TransactionType.TRANS_OUT_TO_WAREHOUSE;
		} else {
			throw ApplicationException.fromMessage("Missing transaction data!");
		}
		log.info("Transaction type: {}", type);
		setType(type);
		generateUniqueCode(type);
		
	}
	
	public int getTotalProductFlowCount() {
		if (null == productFlows) return 0;
		int count = 0;
		for (ProductFlow productFlow : productFlows) {
			count += productFlow.getCount();
		}
		return count;
	}

	public void addProductFlow(ProductFlow productFlow) {
		productFlows.add(productFlow);
	}
	
	public int getProductCount(Product product) {
		if (null == productFlows) return 0;
		int count = 0;
		for (ProductFlow productFlow : productFlows) {
			if (product.idEquals(productFlow.getProduct())) {
				count += productFlow.getCount();
			}
		}
		return count;
	}

	public void setProductFlowsTransactionNull() {
		if (null == productFlows) return;
		productFlows.forEach(p->p.setTransaction(null));
	}
	@Override
	public TransactionModel toModel() {
		TransactionModel model = new TransactionModel();
		if (null != productFlows) {
			productFlows.forEach(p-> {
				model.addProductFlow(p.toModel());
			});
		}
		return copy(model, "productFlows");
	}

    public int getProductKindCount() {
		if (null == productFlows) {
			return 0;
		}
        return productFlows.stream().map(p -> p.getProduct().getId()).collect(Collectors.toSet()).size();
    }
	 

}
