package com.pkm.medicalinventory.dto;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.pkm.medicalinventory.annotation.CustomRequestInfo;
import com.pkm.medicalinventory.controller.BaseController;
import com.pkm.medicalinventory.controller.view.MvcMemberAreaController;
import com.pkm.medicalinventory.controller.view.MvcPublicController;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NavigationMenu implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5269567011649796790L;
	private static final String AUTH_PREFIX = "/app/";
	private static final String PUBLIC_PREFIX = "/public/";
	private String url;
	private boolean authenticated;
	private String iconClassName;
	private String label;

	public static NavigationMenu rawMenu(String url, String label, boolean authenticated) {
		return NavigationMenu.builder().authenticated(authenticated).url(url).iconClassName("fas fa-link").label(label)
				.build();
	}
	
	public static NavigationMenu fromHandlerMethod(Method method, String basePath, boolean authenticated) {
		GetMapping getMapping = method.getAnnotation(GetMapping.class);
		RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
		if (null == getMapping && null == requestMapping)
		{
			return null;
		}
		String getMappingPath = getMapping != null && notEmptyArray(getMapping.value()) ? getMapping.value()[0]
				: null;
		String requestMappingPath = requestMapping != null && notEmptyArray(requestMapping.value())
				? requestMapping.value()[0]
				: null;
		if (null == getMappingPath && null == requestMappingPath) {
			return null;
		}

		String menuPath = requestMappingPath != null ? requestMappingPath : getMappingPath;
		if (menuPath.startsWith("/")) {
			menuPath = StringUtils.removeFirst(menuPath.trim(), "[/]");
		}
		CustomRequestInfo customRequestInfo = method.getAnnotation(CustomRequestInfo.class);
		String title = customRequestInfo == null || customRequestInfo.title().isEmpty()? "Page Title" : customRequestInfo.title();
		NavigationMenu menu = NavigationMenu.rawMenu(basePath + "/" + menuPath, title, authenticated);
		return menu;
	}

	static List<NavigationMenu> menus = null;

	public static List<NavigationMenu> defaultMenus() {
		if (menus == null) {
			menus = new LinkedList<>();
		} else {
			return menus;
		}
		menus.addAll(memberMenus());
		menus.addAll(publicMenus());
		return menus;
	}

	private static Collection<? extends NavigationMenu> publicMenus() {

		return extractMenus(MvcPublicController.class, false);
	}

	private static Collection<? extends NavigationMenu> extractMenus(Class<? extends BaseController> _class,
			final boolean authenticated) {
		Controller controller = _class.getAnnotation(Controller.class);
		if (null == controller)
			return new LinkedList<>();
		RequestMapping baseRequestMapping = _class.getAnnotation(RequestMapping.class);
		String basePath = baseRequestMapping != null && notEmptyArray(baseRequestMapping.value())
				? baseRequestMapping.value()[0]
				: "";
		if (!basePath.isEmpty()) {
			basePath = "/" + basePath.replaceAll("[/]", "");
		}
		List<NavigationMenu> menus = new LinkedList<>();
		Method[] methods = _class.getDeclaredMethods();
		for (Method method : methods) {
			NavigationMenu menu = fromHandlerMethod(method, basePath, authenticated);
			if (null != menu) {
				menus.add(menu );
			}
		}
		return menus;
	}

	private static boolean notEmptyArray(String[] values) {
		return values != null && values.length > 0;
	}

	private static Collection<? extends NavigationMenu> memberMenus() {
		return extractMenus(MvcMemberAreaController.class, true);
	}
}
