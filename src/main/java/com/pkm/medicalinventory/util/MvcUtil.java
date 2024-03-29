package com.pkm.medicalinventory.util;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.pkm.medicalinventory.config.LogProxyFactory;
import com.pkm.medicalinventory.entity.setting.EntityProperty;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MvcUtil {

	public static String getHost(HttpServletRequest request) {
		StringBuffer url = request.getRequestURL();
		String uri = request.getRequestURI();
		String host = url.substring(0, url.indexOf(uri)); // result
		return host;
	}

	public static Model constructCommonModel(
		EntityProperty entityProperty,
		Model model,
		String title,String page
	) {
		return constructCommonModel(entityProperty, model, title, page, null);
	}

	public static Model constructCommonModel(
		EntityProperty entityProperty,
		Model model,
		String title,
		String page,
		String option
	) {
		boolean withOption = false;
		String optionJson = "null";

		if (null != option) {
			System.out.println("=========REQUEST_OPTION: " + option);
			String[] options = option.split("&");
			Map<String, Object> optionMap = new HashMap<String, Object>();
			for (String optionItem : options) {
				String[] optionKeyValue = optionItem.split("=");
				if (optionKeyValue == null || optionKeyValue.length != 2) {
					continue;
				}
				optionMap.put(optionKeyValue[0], optionKeyValue[1]);
			}
			if (optionMap.isEmpty() == false) {
				withOption = true;
//				optionJson = MyJsonUtil.mapToJson(optionMap);
				System.out.println("=========GENERATED_OPTION: " + optionMap);
				System.out.println("=========OPTION_JSON: " + optionJson);
			}
		}
		model.addAttribute("title", title);
		model.addAttribute("entityProperty", entityProperty);
		model.addAttribute("page", page);

		model.addAttribute("withOption", withOption);
		model.addAttribute("options", optionJson);
		model.addAttribute("singleRecord", false);
		return model;
	}

	public static List<Method> getRequesMappingMethods(Class<?> _class) {
		Method[] methods = _class.getMethods();

		List<Method> result = new ArrayList<Method>();

		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if (method.getAnnotation(RequestMapping.class) != null) {
				result.add(method);
			}
		}
		return result;
	}

	 
	
	private static String getPathVariables(Method method){
		List<String> pathVariables = getPathVariableList(method);
		String[] pathVariablesArray = pathVariables.toArray(new String[pathVariables.size()]);
		return String.join(",", pathVariablesArray);
	}
	
	public static void main(String[] args) throws NoSuchMethodException, SecurityException {
		 
		String[] arr = new String[9];
		System.out.println(arr[8]);
		
	}
	
	private static List<String> getPathVariableList(Method method){ 
		Parameter[] parameters = method.getParameters();
		String[] paramNames = getParameterNames(method);
		List<String> pathVariables = new ArrayList<>();
		if(null == parameters) {
			return pathVariables;
		}
		for (int i = 0; i < parameters.length; i++) {			 
			if(parameters[i].getAnnotation(PathVariable.class) != null) {				 
				String name = getPathVariableName(parameters[i], paramNames[i]);
				pathVariables.add(name);
			}
		}
		 
		return pathVariables;
	}
	
	private static String getPathVariableName(Parameter parameter, String paramName) {
		PathVariable pathVariable = parameter.getAnnotation(PathVariable.class);
		final String name;
		if(pathVariable.name().equals("")== false) {
			name = pathVariable.name();
		}else if(paramName != null) {
			name = paramName;
		 } else {
			 name = parameter.getName();
		 }
			
		return name;
	}
	
	public static String[] getParameterNames(Method method) {
		try {
			String[] params = LogProxyFactory.discoverer.getParameterNames(method); 
			return params;
		}catch ( Exception |  NoSuchMethodError e) {
			return new String[method.getParameters().length];
		}
		
	}
}
