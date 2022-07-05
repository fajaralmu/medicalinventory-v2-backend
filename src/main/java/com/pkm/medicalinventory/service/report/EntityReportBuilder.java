package com.pkm.medicalinventory.service.report;

import java.util.Date;
import java.util.List;

import com.pkm.medicalinventory.dto.model.BaseModel;
import com.pkm.medicalinventory.entity.setting.EntityProperty;
import com.pkm.medicalinventory.util.DateUtil;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class EntityReportBuilder extends ReportBuilder<XSSFWorkbook> {
	protected XSSFSheet xsheet;
	protected static final String BLANK = "";
	protected static final String DATE_PATTERN = "ddMMyyyy'T'hhmmss-a";
//	protected final ReportData reportData;
	protected String reportName;
	private final List<? extends BaseModel> entities;
	private final EntityProperty entityProperty;
	private final String requestId;

	public EntityReportBuilder(EntityProperty entityProperty, List<? extends BaseModel> entities, String reqId) {
		 
		this.requestId = reqId;
		this.entityProperty = entityProperty;
		this.entities = entities;
	}

	public CustomWorkbook build() {

		log.info("Writing entity report of: {}", entityProperty.getEntityName());

		String time = getDateTime();
		String sheetName = entityProperty.getEntityName();
//		 webConfigService.getReportPath() + "/" 
		String reportName = sheetName + "_" + time + "_" + requestId + ".xlsx";
		CustomWorkbook xwb = new CustomWorkbook();
		xsheet = xwb.createSheet(sheetName);
		xwb.setFileName(reportName);
		createEntityTable();

		notifyProgress(1, 1, 10);

//		byte[] file = MyFileUtil.getFile(xwb, reportName);
		notifyProgress(1, 1, 10);
		return xwb;
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
