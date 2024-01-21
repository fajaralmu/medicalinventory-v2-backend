package com.pkm.medicalinventory.controller.view;

import com.pkm.medicalinventory.annotation.CustomRequestInfo;
import com.pkm.medicalinventory.controller.BaseController;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author fajar
 *
 */
@Slf4j
@RequestMapping("public")
@Controller 
public class MvcPublicController extends BaseController{
	
	public MvcPublicController() {
		log.info("-----------------Mvc App Controller------------------");
	}
	@RequestMapping(value = { "/main"})
	@CustomRequestInfo(pageUrl = "pages/main-menu", title="Home")
	public String index(Model model)  {
		model.addAttribute("title", bindedValues.getApplicationHeaderLabel());
		return basePage;
	}
	@RequestMapping(value = { "/about"})
	@CustomRequestInfo(pageUrl = "pages/public/about", title = "About")
	public String about(Model model) {
		model.addAttribute("profile", appProfileService.getApplicationProfile());
		return basePage;
	}
	 
	
	
	
	 

}
