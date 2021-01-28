package com.fajar.medicalinventory.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
	
	
	public static NavigationMenu authenticatedMenu(String url, String label) {
		return NavigationMenu.builder().authenticated(true).url(AUTH_PREFIX+url)
				.iconClassName("fas fa-folder").label(label).build();
	}
	public static NavigationMenu publicMenu(String url, String label) {
		return NavigationMenu.builder().authenticated(false).url(PUBLIC_PREFIX+url)
				.iconClassName("fas fa-link").label(label).build();
	}
	public static NavigationMenu rawMenu(String url, String label, boolean authenticated) {
		return NavigationMenu.builder().authenticated(authenticated).url(url)
				.iconClassName("fas fa-link").label(label).build();
	}
	static List<NavigationMenu> menus = null;
	public static List<NavigationMenu> defaultMenus(){ 
		if (menus == null) {
			menus = new ArrayList<>();
		} else {
			return menus;
		}
		menus.add(publicMenu("main", "Main"));
		menus.add(publicMenu("about", "About"));
		
//		menus.add(authenticatedMenu("home", "Home"));
		menus.add(authenticatedMenu("dashboard", "Dashboard"));
		menus.add(authenticatedMenu("transaction", "Transaction"));
		menus.add(authenticatedMenu("masterdata", "Master Data"));
		menus.add(authenticatedMenu("profile", "Profile"));
		return menus ;
	}
}
