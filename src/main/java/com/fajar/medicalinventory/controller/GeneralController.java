package com.fajar.medicalinventory.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class GeneralController {

	@RequestMapping(value = { "/app/main"})
	public void application(Model model, HttpServletRequest request, HttpServletResponse response)  { 
		response.setStatus(HttpStatus.FOUND.value());
		response.setHeader("location", request.getContextPath()+"/member/application");
	}
}
