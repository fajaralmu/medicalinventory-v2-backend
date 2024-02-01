package com.pkm.medicalinventory.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.pkm.medicalinventory.auth.SessionValidationService;
import com.pkm.medicalinventory.auth.UserSessionService;
import com.pkm.medicalinventory.component.BindedValues;
import com.pkm.medicalinventory.config.ApplicationProfileService;
import com.pkm.medicalinventory.dto.KeyValue;
import com.pkm.medicalinventory.dto.NavigationMenu;
import com.pkm.medicalinventory.entity.User;
import com.pkm.medicalinventory.util.DateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class BaseController {

	private static final String MODEL_ATTR_TITLE = "title";
	private static final String MODEL_ATTR_PAGE_URL = "pageUrl";

	protected String basePage = "BASE_PAGE";
	@Autowired
	protected SessionValidationService sessionValidationService;
	@Autowired
	protected UserSessionService userSessionService;

	@Autowired
	protected BindedValues bindedValues;
	@Autowired
	protected ApplicationProfileService appProfileService;

	@ModelAttribute("applicationHeaderLabel")
	public String applicationHeaderLabel(HttpServletRequest request) {
		return appProfileService.getApplicationProfile().getName();
	}

	@ModelAttribute("applicationDescription")
	public String applicationDescription(HttpServletRequest request) {
		return appProfileService.getApplicationProfile().getShortDescription();
	}

	@ModelAttribute("applicationFooterLabel")
	public String applicationFooterLabel(HttpServletRequest request) {
		return applicationHeaderLabel(request);
	}

	@ModelAttribute("isPhone")
	public boolean isPhone(HttpServletRequest request) {
		try {
			return request.getHeader("user-agent").toLowerCase().contains("android");
		} catch (Exception e) {
			return false;
		}
	}

	@ModelAttribute("contextPath")
	public String getContextPath(HttpServletRequest request) {
		return request.getContextPath();
	}

	@ModelAttribute("year")
	public int getCurrentYear(HttpServletRequest request) {
		return DateUtil.getCalendarItem(new Date(), Calendar.YEAR);
	}

	@ModelAttribute("isAuthenticated")
	public boolean isAuthenticated(HttpServletRequest request) {
		return validatePrinciple(request.getUserPrincipal());
	}

	@ModelAttribute("loggedUser")
	public User loggedUser() {
		return sessionValidationService.getLoggedUser();
	}

	@ModelAttribute("userPrincipal")
	public Object getUserPrincipal() {
		return sessionValidationService.getUserPrincipal();
	}

	@ModelAttribute("greeting")
	public String greeting() {
		User user = loggedUser();
		String name = user == null ? "" : user.getDisplayName();
		return "Good " + DateUtil.getTimeGreeting() + ", " + name;

	}

	@ModelAttribute("navigationMenus")
	public List<NavigationMenu> navigationMenus(HttpServletRequest request) {
		// boolean authenticated = validatePrinciple(request.getUserPrincipal());
		// List<NavigationMenu> displayedMenus = NavigationMenu.defaultMenus().stream()
		// 		.filter(predicateNavigationMenu(authenticated))
		// 		.collect(Collectors.toList());
		// return displayedMenus;
		return new ArrayList<>();
	}

	private Predicate<? super NavigationMenu> predicateNavigationMenu(boolean authenticated) {

		return (NavigationMenu t) -> {
			if (!authenticated && t.isAuthenticated()) {
				return false;
			}
			return true;
		};
	}

	@ModelAttribute("ipAndPort")
	public String getIpAddressAndPort(HttpServletRequest request) {

		String remoteAddress = request.getRemoteAddr();
		int port = request.getServerPort();

		return remoteAddress + ":" + port;
	}

	protected static void setTitle(Model model, String title) {
		model.addAttribute(MODEL_ATTR_TITLE, title);
	}

	protected static void setPageUrl(Model model, String pageUrl) {
		model.addAttribute(MODEL_ATTR_PAGE_URL, pageUrl);
	}

	protected boolean validatePrinciple(Object principal) {
		return sessionValidationService.validatePrinciple(principal);
	}

	/**
	 * ====================================================== Statics
	 * ======================================================
	 * 
	 */

	private static void addResourcePaths(ModelAndView modelAndView, String resourceName, String... paths) {
		List<KeyValue<String, String>> resoucePaths = new ArrayList<>();
		for (int i = 0; i < paths.length; i++) {
			KeyValue<String, String> keyValue = new KeyValue<String, String>();
			keyValue.setValue(paths[i]);

			resoucePaths.add(keyValue);
			log.info("{}. Add {} to {} , value: {}", i, resourceName, modelAndView.getViewName(), paths[i]);
		}
		setModelAttribute(modelAndView, resourceName, resoucePaths);
	}

	private static void setModelAttribute(ModelAndView modelAndView, String attrName, Object attrValue) {
		if (null == attrValue) {
			return;
		}
		modelAndView.getModel().put(attrName, attrValue);
	}

	public static void addStylePaths(ModelAndView modelAndView, String... paths) {
		if (null == paths) {
			return;
		}
		addResourcePaths(modelAndView, "additionalStylePaths", paths);
	}

	public static void addJavaScriptResourcePaths(ModelAndView modelAndView, String... paths) {
		if (null == paths) {
			return;
		}
		addResourcePaths(modelAndView, "additionalScriptPaths", paths);
	}

	public static void addTitle(ModelAndView modelAndView, String title) {
		if (null == title || title.isEmpty()) {
			return;
		}
		setModelAttribute(modelAndView, "title", title);
	}

	public static void addPageUrl(ModelAndView modelAndView, String pageUrl) {
		if (null == pageUrl || pageUrl.isEmpty()) {
			return;
		}
		setModelAttribute(modelAndView, "pageUrl", pageUrl);
	}
}
