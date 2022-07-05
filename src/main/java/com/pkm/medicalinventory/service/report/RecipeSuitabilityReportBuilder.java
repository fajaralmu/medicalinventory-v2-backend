package com.pkm.medicalinventory.service.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pkm.medicalinventory.constants.TransactionType;
import com.pkm.medicalinventory.entity.Product;
import com.pkm.medicalinventory.entity.ProductFlow;
import com.pkm.medicalinventory.entity.Transaction;
import com.pkm.medicalinventory.util.DateUtil;

public class RecipeSuitabilityReportBuilder extends ReportBuilder<XSSFWorkbook> {
    private final int month, year;
    private final List<Transaction> transactions;
    private final Map<String, List<Transaction>> mapped;
    private final XSSFSheet sheet;
    public RecipeSuitabilityReportBuilder(int month, int year, List<Transaction> transactions) {
        this.month = month;
        this.year = year;
        this.xwb = new XSSFWorkbook();
        this.transactions = transactions.stream().filter(t -> t.getType().equals(TransactionType.TRANS_OUT)).collect(Collectors.toList());
        this.transactions.sort((a, b) -> a.getTransactionDate().compareTo(b.getTransactionDate()));
        this.sheet = this.xwb.createSheet("Sheet-1");
        this.mapped = mapTransactionByDate();
    }
    private Map<String, List<Transaction>> mapTransactionByDate() {
        Map<String, List<Transaction>> map = new HashMap<>();
        for (Transaction transaction : transactions) {
            String date = dateToString(transaction.getTransactionDate());
            if (map.get(date) == null) {
                map.put(date, new ArrayList<>());
            }
            map.get(date).add(transaction);
        }
        return map;
    }
    @Override
    public XSSFWorkbook build() throws Exception {
        int row = 2;
        XSSFRow titleRow = sheet.createRow(++row);
        createTableHeaders(titleRow);
        for (String date : mapped.keySet()) {
            double number = 0;
            createCell(++row, 2, date);
            boolean firstTrxPassed = false;
            for (Transaction transaction : mapped.get(date)) {            
                createCell(firstTrxPassed ? ++row : row, 3, ++number);
                createCell(row, 4, transaction.getCustomer().getName());
                createCell(row, 5, transaction.getCustomer().getCode());
                if (!firstTrxPassed) {
                    firstTrxPassed = true;
                }

                Map<Long, Integer> mappedProduct = mapProductCount(transaction.getProductFlows());
                Set<Product> products = transaction.getProductFlows().stream().map(p -> p.getProduct()).collect(Collectors.toSet());
                boolean firstProductPassed = false;
                for (long id : mappedProduct.keySet()) {
                    Optional<Product> product = products.stream().filter(p -> p.getId().equals(id)).findFirst();
                    createCell(firstProductPassed ? ++row : row, 6, product.isPresent() ? product.get().getName() : "N/A");
                    createCell(row, 7, mappedProduct.get(id));
                    if (!firstProductPassed) {
                        firstProductPassed = true;
                    }
                }
            }
        }
        return xwb;
    }
    private void createTableHeaders(XSSFRow titleRow) {
        int col = 1;
        String[] headers = new String[] {
            "Tanggal", "No", "Nama Pasien", "No RM", "Nama Obat", "Jumlah Obat"
        };
        for (String header : headers) {
            createCell(titleRow.getRowNum(), ++col, header);
        }
    }
    private Map<Long, Integer> mapProductCount(List<ProductFlow> items) {
        Map<Long, Integer> map = new HashMap<>();
        for (ProductFlow item : items) {
            long id = item.getProduct().getId();
            if (map.get(id) == null) {
                map.put(id, 0);
            }
            map.put(id, map.get(id) + item.getCount());
        }
        return map;
    }
    private XSSFCell createCell(int rowNum, int index, String val) {
        if (sheet.getRow(rowNum) == null) {
            sheet.createRow(rowNum);
        }
        XSSFCell cell = sheet.getRow(rowNum).createCell(index);
        cell.setCellValue(val);
        return cell;
    }
    private XSSFCell createCell(int rowNum, int index, double val) {
        if (sheet.getRow(rowNum) == null) {
            sheet.createRow(rowNum);
        }
        XSSFCell cell = sheet.getRow(rowNum).createCell(index);
        cell.setCellValue(val);
        return cell;
    }
}
