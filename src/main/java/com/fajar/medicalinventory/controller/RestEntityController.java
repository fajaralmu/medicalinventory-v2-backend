package com.fajar.medicalinventory.controller;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fajar.medicalinventory.annotation.CustomRequestInfo;
import com.fajar.medicalinventory.dto.WebRequest;
import com.fajar.medicalinventory.dto.WebResponse;
import com.fajar.medicalinventory.entity.setting.EntityProperty;
import com.fajar.medicalinventory.service.LogProxyFactory;
import com.fajar.medicalinventory.service.entity.EntityManagementPageService;
import com.fajar.medicalinventory.service.entity.EntityService;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@RestController
@RequestMapping("/api/app/entity")
@Slf4j
public class RestEntityController extends BaseController {

	@Autowired
	private EntityService entityService;
	@Autowired
	private EntityManagementPageService entityManagementPageService;

	public RestEntityController() {
		log.info("------------------Rest Entity Controller-----------------");
	}

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	@PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CustomRequestInfo(withRealtimeProgress = true)
	public WebResponse add(@RequestBody WebRequest request, HttpServletRequest httpRequest) {
		log.info("add entity {}", request.getEntity());
		return entityService.saveEntity(request, httpRequest, true);
	}

	@PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CustomRequestInfo(withRealtimeProgress = true)
	public WebResponse update(@RequestBody WebRequest request, HttpServletRequest httpRequest) {
		log.info("register update {}", request.getEntity());
		return entityService.saveEntity(request, httpRequest, false);

	}

	@PostMapping(value = "/get", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse get(@RequestBody WebRequest request, HttpServletRequest httpRequest) {
		log.info("get entity {}", request);
		return entityService.filter(request, httpRequest);

	}

	@PostMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse delete(@RequestBody WebRequest request, HttpServletRequest httpRequest) throws Exception {
		log.info("delete entity {}", request);
		return entityService.delete(request);
	}

	@PostMapping(value = "/config", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public EntityProperty config(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {
		log.info("get entity config {}", request);
		EntityProperty entityProperty = entityService.getConfig(request, httpRequest);
		if (null == entityProperty) {
			httpResponse.setStatus(HttpStatus.NOT_FOUND.value());
		}
		return entityProperty;

	}
	@PostMapping(value = "/configv2", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse configv2(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {
		return WebResponse.builder().entityProperty(config(request, httpRequest, httpResponse)).build();

	}

	@PostMapping(value = "/managementpages", produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse managementpages() {
		log.info("get managementpages");
		return entityManagementPageService.getManagementPages();

	}

}
