package com.fajar.medicalinventory.service.report;

import java.util.Date;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fajar.medicalinventory.dto.model.BaseModel;
import com.fajar.medicalinventory.entity.setting.EntityProperty;
import com.fajar.medicalinventory.service.ProgressService;
import com.fajar.medicalinventory.util.DateUtil;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class EntityReportBuilder {// extends ReportBuilder {
	protected XSSFSheet xsheet;
	protected XSSFWorkbook xssfWorkbook;
	protected static final String BLANK = "";
	protected static final String DATE_PATTERN = "ddMMyyyy'T'hhmmss-a";
//	protected final ReportData reportData;
	protected String reportName;
	// optional
	protected ProgressService progressService;
	private List<? extends BaseModel> entities;
	private EntityProperty entityProperty;
	private final String requestId;

	public EntityReportBuilder(EntityProperty entityProperty, List<? extends BaseModel> entities, String reqId) {
		 
		this.requestId = reqId;
		this.entityProperty = entityProperty;
		this.entities = entities;
	}

	public CustomWorkbook buildReport() {

		log.info("Writing entity report of: {}", entityProperty.getEntityName());

		String time = getDateTime();
		String sheetName = entityProperty.getEntityName();
//		 webConfigService.getReportPath() + "/" 
		String reportName = sheetName + "_" + time + "_" + requestId + ".xlsx";
		CustomWorkbook xwb = new CustomWorkbook();
		xsheet = xwb.createSheet(sheetName);
		xwb.setFileName(reportName);
		createEntityTable();

		sendProgress(1, 1, 10);

//		byte[] file = MyFileUtil.getFile(xwb, reportName);
		sendProgress(1, 1, 10);
		return xwb;
	}

	private void createEntityTable() {
		try {
			Object[] entityValues = ExcelReportUtil.getEntitiesTableValues(entities, entityProperty);
			ExcelReportUtil.RowCreatedCallback rowCallback = new ExcelReportUtil.RowCreatedCallback() {

				@Override
				public void callback(int i, int totalRow) {
					progressService.sendProgress(1, totalRow, 60, requestId);
				}
			};
			ExcelReportUtil.createTable(xsheet, entityProperty.getElements().size() + 1, 2, 2, rowCallback,
					entityValues);

		} catch (Exception e) {
			log.error("Error creating entity excel table");
			e.printStackTrace();
		}
	}
	
	protected String getDateTime() {
		return DateUtil.formatDate(new Date(), DATE_PATTERN);
	}

	protected void sendProgress(double taskProportion, double taskSize, double totalTaskProportion) {
		if (null == progressService)
			return;
		progressService.sendProgress(taskProportion, taskSize, totalTaskProportion, false,  getRequestId());
	}

}
