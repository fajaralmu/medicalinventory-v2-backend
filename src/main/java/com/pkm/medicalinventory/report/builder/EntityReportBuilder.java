package com.pkm.medicalinventory.report.builder;

import java.util.Date;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pkm.medicalinventory.dto.model.BaseModel;
import com.pkm.medicalinventory.entity.setting.EntityProperty;
import com.pkm.medicalinventory.report.ExcelReportUtil;
import com.pkm.medicalinventory.report.WritableReport;
import com.pkm.medicalinventory.service.ProgressNotifier;
import com.pkm.medicalinventory.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EntityReportBuilder extends BaseReportBuilder {
	protected XSSFSheet xsheet;
	protected static final String BLANK = "";
	protected static final String DATE_PATTERN = "ddMMyyyy'T'hhmmss-a";

	private final List<? extends BaseModel> entities;
	private final EntityProperty entityProperty;

	public EntityReportBuilder(EntityProperty entityProperty, List<? extends BaseModel> entities, ProgressNotifier progress, String fileName) {
		super(fileName);
		this.progressNotifier = progress;
		this.entityProperty = entityProperty;
		this.entities = entities;
	}

	public WritableReport build() {

		log.info("Writing entity report of: {}", entityProperty.getEntityName());

		String time = getDateTime();
		String sheetName = entityProperty.getEntityName();
		
		XSSFWorkbook xwb = new XSSFWorkbook();
		xsheet = xwb.createSheet(sheetName);
		
		createEntityTable();

		notifyProgress(1, 1, 10);

//		byte[] file = MyFileUtil.getFile(xwb, reportName);
		notifyProgress(1, 1, 10);
		return new XSSFPrintableReport(xwb, fileName);
	}

	private void createEntityTable() {
		try {
			Object[] entityValues = ExcelReportUtil.getEntitiesTableValues(entities, entityProperty);
			ExcelReportUtil.RowCreatedCallback rowCallback = new ExcelReportUtil.RowCreatedCallback() {

				@Override
				public void callback(int i, int totalRow) {
					notifyProgress(1, totalRow, 60.d);
				}
			};
			int col = entityProperty.getElements().size() + 1;
			ExcelReportUtil.createTable(xsheet, col, 2, 2, rowCallback, entityValues);

		} catch (Exception e) {
			log.error("Error creating entity excel table");
			e.printStackTrace();
		}
	}

	protected String getDateTime() {
		return DateUtil.formatDate(new Date(), DATE_PATTERN);
	}
}
