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
@RequestMapping("member")
@Controller 
public class MvcMemberAreaController extends BaseController{  
	
	public MvcMemberAreaController() {
		log.info("-----------------Mvc App Controller------------------");
	}
 
	@RequestMapping(value = { "/dashboard"})
	@CustomRequestInfo(pageUrl = "pages/member/dashboard", title = "Dashboard")
	public String wallPage(Model model, HttpServletRequest request, HttpServletResponse response)  { 
		model.addAttribute("applicationURL", "/member/application");
		return basePage;
	}
	@RequestMapping(value = { "/application"})
	@CustomRequestInfo(title="Application")
	public String application(Model model, HttpServletRequest request, HttpServletResponse response)  { 
		return "front-end-app";
	}
	@RequestMapping(value = { "/profile"})
	@CustomRequestInfo(pageUrl = "pages/member/profile", title="Profile")
	public String profile(Model model, HttpServletRequest request, HttpServletResponse response)  { 
		return basePage;
	}
	
	

}
