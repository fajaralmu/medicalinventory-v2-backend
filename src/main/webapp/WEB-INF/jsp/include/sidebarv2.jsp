
<%@page import="org.springframework.beans.factory.annotation.Autowired"%>
<%@ page language="java" contentType="text/html; charset=windows-1256"
	pageEncoding="windows-1256"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!-- <nav class="navbar navbar-expand-lg "> -->
<div id="sidebar-wrapper" class="bg-dark col-lg-3">
	<ul class="sidebar-nav">
		
		<c:forEach items="${navigationMenus }" var="menu">
			<li><a href="<spring:url value="${menu.url }"></spring:url>">
					<span>${menu.label }</span>
			</a></li>
		</c:forEach>

	</ul>
</div>
