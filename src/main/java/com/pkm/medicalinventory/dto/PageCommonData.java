package com.pkm.medicalinventory.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class PageCommonData implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = -1610349453413372289L;

	@Default
	private Map<String, String> cookieValues = new HashMap<>(); 
	 
	public void addPageCookies(String pageId, String cookie) {
		log.info("addPageCookies, pageId:{}, cookie:{}", pageId, cookie);
		cookieValues.put(pageId, cookie);
	}
	
	public boolean hasCookieWithRequestId(String pageID) {
		return cookieValues.get(pageID) != null;
	}
	public String getCookie(String pageID) {
		return cookieValues.get(pageID);
	}
	public boolean isCookieRegistered(String cookie) {
		return cookieValues.containsValue(cookie);
	}
	public String getCookieKey(String cookie) {
		for(Entry<String, String> e:cookieValues.entrySet()) {
			if(cookie.equals(e.getValue())) {
				return e.getKey();
			}
		}
		return null;
	}
	
}
