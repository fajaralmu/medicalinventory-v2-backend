package com.pkm.medicalinventory.controller.api;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pkm.medicalinventory.annotation.CustomRequestInfo;
import com.pkm.medicalinventory.dto.WebRequest;
import com.pkm.medicalinventory.dto.WebResponse;
import com.pkm.medicalinventory.dto.model.BaseModel;
import com.pkm.medicalinventory.entity.setting.EntityProperty;
import com.pkm.medicalinventory.management.MasterDataManagementPageService;
import com.pkm.medicalinventory.management.MasterDataService;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@RestController
@RequestMapping("/api/app/entity")
@Slf4j
public class ManagementController {

	@Autowired
	private MasterDataService entityService;
	@Autowired
	private MasterDataManagementPageService entityManagementPageService;

	public ManagementController() {
		log.info("------------------Rest Entity Controller-----------------");
	} 

	@PostMapping(
		value = "/add",
		consumes = MediaType.APPLICATION_JSON_VALUE,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@CustomRequestInfo(withRealtimeProgress = true)
	public WebResponse add(@RequestBody WebRequest request) {
		log.info("add entity {}", request.getEntity());
		BaseModel resp = entityService.saveEntity(request, true);
		return new WebResponse().withEntity(resp);
	}

	@PostMapping(
		value = "/update",
		consumes = MediaType.APPLICATION_JSON_VALUE,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	@CustomRequestInfo(withRealtimeProgress = true)
	public WebResponse update(@RequestBody WebRequest request) {
		log.info("register update {}", request.getEntity());
		BaseModel resp = entityService.saveEntity(request, false);
		return new WebResponse().withEntity(resp);
	}

	@PostMapping(
		value = "/get",
		consumes = MediaType.APPLICATION_JSON_VALUE,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public WebResponse get(@RequestBody WebRequest request) {
		log.info("get entity {}", request);
		return entityService.filter(request);

	}

	@PostMapping(
		value = "/delete",
		consumes = MediaType.APPLICATION_JSON_VALUE,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public WebResponse delete(
		@RequestBody WebRequest request
	) throws Exception {
		log.info("delete entity {}", request);
		BaseModel resp = entityService.delete(request);
		return new WebResponse().withEntity(resp);
	}

	@PostMapping(
		value = "/config",
		consumes = MediaType.APPLICATION_JSON_VALUE,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public EntityProperty config(
		@RequestBody WebRequest request,
		HttpServletResponse httpResponse
	) {
		log.info("get entity config {}", request);
		EntityProperty entityProperty = entityService.getConfig(request);
		if (null == entityProperty) {
			httpResponse.setStatus(HttpStatus.NOT_FOUND.value());
		}
		return entityProperty;

	}
	@PostMapping(
		value = "/configv2",
		consumes = MediaType.APPLICATION_JSON_VALUE,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public WebResponse configv2(
		@RequestBody WebRequest request,
		HttpServletResponse httpResponse
	) {
		return new WebResponse().withEntityProperty(config(request, httpResponse));

	}

	@PostMapping(
		value = "/managementpages",
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public WebResponse managementpages() {
		log.info("get managementpages");
		return entityManagementPageService.getManagementPages();

	}

}
