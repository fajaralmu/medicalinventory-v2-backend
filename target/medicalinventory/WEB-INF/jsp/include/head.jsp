
<%@page import="org.springframework.beans.factory.annotation.Autowired"%>
<%@ page language="java" contentType="text/html; charset=windows-1256"
	pageEncoding="windows-1256"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div class="header" style="height: auto">

	<div class="page-header">
		<div class="row">
			<div class="col-1" style="text-align: center;">
				<img width="50" height="50" src="<c:url value="/res/img/NC.png" />" />
			</div>
			<div class="col-11">
				<h1 style="margin-left: 7px">${applicationHeaderLabel }</h1>
			</div>
			<div class="col-12">
				<p>${applicationDescription}</p>
			</div>
		</div>

		<!-- <nav class="navbar navbar-expand-lg "> -->
		<nav class="navbar-custom">
			<div>
				<ul class="navbar-nav">
					<c:forEach items="${navigationMenus }" var="menu">
						<li class="nav-item "><a
							href="<spring:url value="${menu.url }"></spring:url>"> <span><i
									class="${menu.iconClassName }"></i>&nbsp;${menu.label }</span>
						</a></li>
					</c:forEach>
				</ul>
			</div>
		</nav>
	</div>

	<div></div>
</div>
