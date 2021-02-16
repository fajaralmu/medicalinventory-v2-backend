package com.fajar.medicalinventory.service.entity;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.medicalinventory.dto.model.BaseModel;
import com.fajar.medicalinventory.entity.BaseEntity;
import com.fajar.medicalinventory.entity.User;
import com.fajar.medicalinventory.entity.setting.EntityProperty;
import com.fajar.medicalinventory.service.ProgressService;
import com.fajar.medicalinventory.service.SessionValidationService;
import com.fajar.medicalinventory.service.report.CustomWorkbook;
import com.fajar.medicalinventory.service.report.EntityReportBuilder;
import com.fajar.medicalinventory.util.EntityPropertyBuilder;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EntityReportService {
 
	@Autowired
	private ProgressService progressService;
	@Autowired
	private SessionValidationService sessionValidationService;

	public CustomWorkbook getEntityReport(List<? extends BaseModel> entities, Class<? extends BaseEntity> entityClass,
			HttpServletRequest httpRequest) throws Exception {
		log.info("Generate entity report: {}", entityClass); 
		User currentUser = sessionValidationService.getLoggedUser(httpRequest);
		String requestId = currentUser.getRequestId();
		Class modelClass = BaseEntity.getTypeArgumentOfGenericSuperClass(entityClass);
		
		EntityProperty entityProperty = EntityPropertyBuilder.createEntityProperty(modelClass, null);
//		ReportData reportData = ReportData.builder().entities(entities).entityProperty(entityProperty).requestId(requestId).build(); 
	
		EntityReportBuilder reportBuilder = new EntityReportBuilder( entityProperty, entities, requestId);
		reportBuilder.setProgressService(progressService);
		
		progressService.sendProgress(1, 1, 10, false, httpRequest);

		CustomWorkbook file = reportBuilder.buildReport(); 
		
		log.info("Entity Report generated");

		return file;
	}

}
