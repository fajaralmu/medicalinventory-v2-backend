package com.pkm.medicalinventory.config.requestfilter;

import static com.pkm.medicalinventory.util.HttpRequestUtil.getPageRequestId;

import java.util.Date;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.pkm.medicalinventory.annotation.Authenticated;
import com.pkm.medicalinventory.annotation.CustomRequestInfo;
import com.pkm.medicalinventory.controller.BaseController;
import com.pkm.medicalinventory.service.ProgressService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InterceptorProcessor {

	@Autowired
	private ApplicationContext appContext;
	@Autowired
	private ProgressService progressService;

	public InterceptorProcessor() {

		log.info(" //////////// InterceptorProcessor ///////////// ");
	}

	public boolean interceptApiRequest(
		HttpServletRequest request,
		HttpServletResponse response,
		HandlerMethod handlerMethod
	) {

		log.info("intercept api handler: {}", request.getRequestURI());

		initProgress(handlerMethod, request);
		return true;
	}

	public boolean interceptWebPageRequest(
		HttpServletRequest request,
		HttpServletResponse response,
		HandlerMethod handlerMethod
	) {

		String path = request.getRequestURI().substring(request.getContextPath().length());
		log.info("intercept webpage request handler: {}, path info: {}", request.getRequestURI(), path);

		initProgress(handlerMethod, request);

		return true;
	}

	private void initProgress(
		HandlerMethod handlerMethod,
		HttpServletRequest request
	) {
		CustomRequestInfo customRequestInfo = getCustomRequestInfoAnnotation(handlerMethod);

		if (null != customRequestInfo) {
			if (customRequestInfo.withRealtimeProgress()) {
				progressService.init(getPageRequestId());
			}
		}
	}

	public static void main(String[] args) throws Exception {

	}

	public static Authenticated getAuthAnnotation(HandlerMethod handlerMethod) {
		return getHandlerAnnotation(handlerMethod, Authenticated.class);
	}

	public static CustomRequestInfo getCustomRequestInfoAnnotation(HandlerMethod handlerMethod) {
		return getHandlerAnnotation(handlerMethod, CustomRequestInfo.class);
	}

	public static <T> T getHandlerAnnotation(HandlerMethod handlerMethod, Class annotation) {
		log.debug("Get annotation: {}", annotation);
		T annotationObject = null;
		boolean found = false;
		
		try {
			annotationObject = (T) handlerMethod.getMethod().getAnnotation(annotation);
			found = annotationObject != null;
		} catch (Exception e) {
			log.error("Error get annotation ({}) from method", annotation);
			e.printStackTrace();
		}
		
		try {
			if (!found) {
				// log.debug("handlerMethod.getBeanType(): {}", handlerMethod.getBeanType());
				annotationObject = (T) handlerMethod.getBeanType().getAnnotation(annotation);
			}
		} catch (Exception e) {
			log.error("Error get annotation ({}) from class", annotation);
			e.printStackTrace();
		}

		return annotationObject;
	}
	
	//// https://stackoverflow.com/questions/45595203/how-i-get-the-handlermethod-matchs-a-httpservletrequest-in-a-filter
	public HandlerMethod getHandlerMethod(HttpServletRequest request) {
		HandlerMethod handlerMethod = null;

		try {
			RequestMappingHandlerMapping req2HandlerMapping = (RequestMappingHandlerMapping) appContext
					.getBean("org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping");

			HandlerExecutionChain handlerExeChain = req2HandlerMapping.getHandler(request);
			if (Objects.nonNull(handlerExeChain)) {
				handlerMethod = (HandlerMethod) handlerExeChain.getHandler();

				log.debug("[handler method] {}", handlerMethod.getClass());
				return handlerMethod;
			}
		} catch (Exception e) {
			// log.warn("Lookup the handler method ERROR", e);
		} finally {
			log.debug("URI = " + request.getRequestURI() + ", handlerMethod = " + handlerMethod);
		}

		return null;
	}

	public boolean isApi(HandlerMethod handlerMethod) {
		if (null == handlerMethod) {
			return false;
		}
		boolean hasRestController = handlerMethod.getBeanType().getAnnotation(RestController.class) != null;
		boolean hasPostMapping = handlerMethod.getMethod().getAnnotation(PostMapping.class) != null;

		return hasRestController || hasPostMapping;
	}

	public void postHandle(
		HttpServletRequest request,
		HttpServletResponse response,
		HandlerMethod handler,
		ModelAndView modelAndView
	) {

		CustomRequestInfo resourcePath = getCustomRequestInfoAnnotation(handler);

		if (null != modelAndView) {
			log.debug("Add resourcePaths to Web Page");

			if (null == resourcePath) {
				log.debug("{} does not have resourcePath", request.getRequestURI());
				return;
			}
			BaseController.addJavaScriptResourcePaths(modelAndView, resourcePath.scriptPaths());
			BaseController.addStylePaths(modelAndView, resourcePath.stylePaths());
			BaseController.addTitle(modelAndView, resourcePath.title());
			BaseController.addPageUrl(modelAndView, resourcePath.pageUrl());
		} else {
			//
		}

		if (null != resourcePath && resourcePath.withRealtimeProgress()) {
			progressService.sendComplete();
		}

	}

	public static void validateStylePaths(String[] paths) {
		if (null == paths)
			return;
		for (int i = 0; i < paths.length; i++) {
			if (paths[i].toString().toLowerCase().endsWith(".css") == false) {
				paths[i] += ".css?version="+new Date().getTime();
			}
		}
	}

	public static void validateScriptPaths(String[] paths) {
		if (null == paths)
			return;
		for (int i = 0; i < paths.length; i++) {
			if (paths[i].toString().toLowerCase().endsWith(".js") == false) {
				paths[i] += ".js?v="+new Date().getTime();
			}
		}
	}
}
