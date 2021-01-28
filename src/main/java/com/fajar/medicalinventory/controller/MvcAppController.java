package com.fajar.medicalinventory.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.medicalinventory.annotation.CustomRequestInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author fajar
 *
 */
@Slf4j
@RequestMapping("app")
@Controller 
public class MvcAppController extends BaseController{  
	
	public MvcAppController() {
		log.info("-----------------Mvc App Controller------------------");
	}
 
	@RequestMapping(value = { "/dashboard"})
	@CustomRequestInfo(pageUrl = "pages/app/dashboard", title = "Dashboard")
	public String wallPage(Model model, HttpServletRequest request, HttpServletResponse response)  { 
		return basePage;
	}
	@RequestMapping(value = { "/profile"})
	@CustomRequestInfo(pageUrl = "pages/app/profile", title="Profile")
	public String profile(Model model, HttpServletRequest request, HttpServletResponse response)  { 
		return basePage;
	}

}
