package com.fajar.medicalinventory.externalapp;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.fajar.medicalinventory.entity.Product;
import com.fajar.medicalinventory.entity.ProductFlow;
import com.fajar.medicalinventory.entity.Transaction;

public class ProductFlowsData {

	//id;kodetransaksi;kodeobat;kadaluarsa;jumlah;harga;kodestokobat;sesuai;generik
	static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/mm/yyyy");
	static final String FILE_PATH = "C:\\Users\\Republic Of Gamers\\Documents\\ProductFlowsRawData.txt";
	static Map<String, List<ProductFlow>> map = new HashMap<>();
	public static void main(String[] args) throws  Exception {
		setMap();
	}
	static void setMap() throws  Exception{
		if (map.isEmpty() == false) {
			return;
		}
		List lines = FileUtils.readLines(new File(FILE_PATH));
		for (int row = 0; row < lines.size(); row++) {
			Object line = lines.get(row);
			System.out.println(row+" . "+line);
			String[] properties = line.toString().split(";");
			Long id = Long.valueOf(properties[0]);
			String trxCode = properties[1];
			String productCode = properties[2];
			Date expDate = simpleDateFormat.parse(properties[3]);
			int count = Integer.valueOf(properties[4]);
			long price = Long.valueOf(properties[5]);
			 
			boolean suitable = "1".equals(properties[7]);
			boolean generic = "1".equals(properties[8]);
			
			ProductFlow reference =null;
			if ("".equals(properties[6].trim()) == false) {
				reference = new ProductFlow();
				Long referenceStockId = Long.valueOf(properties[6]);
				reference.setId(referenceStockId);
			} 
			ProductFlow pf = new ProductFlow(
					Transaction.builder().code(trxCode).build(), 
					Product.builder().code(productCode).build(), 
					expDate, count, 0, reference , suitable, price, 
					generic);
			pf.setId(id);
			if (map.get(trxCode) == null) {
				map.put(trxCode, new ArrayList<ProductFlow>());
			}
			map.get(trxCode).add(pf);
			System.out.println(pf);
		}
	}
	
	static List<ProductFlow> getProductFlows(String transactionCode) {
		try {
			setMap();
			return map.get(transactionCode);
		} catch (Exception e) {
			return null;
		}
		
	}
}
