package com.fajar.medicalinventory.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class GeneralController {

	@RequestMapping(value = { "/app/main"})
	public String application(Model model, HttpServletRequest request, HttpServletResponse response)  { 
		return "front-end-app";
	}
}
