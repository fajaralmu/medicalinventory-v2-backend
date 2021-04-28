
<%@page import="org.springframework.beans.factory.annotation.Autowired"%>
<%@ page language="java" contentType="text/html; charset=windows-1256"
	pageEncoding="windows-1256"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!-- <nav class="navbar navbar-expand-lg "> -->
<div style="position: absolute;">
<div id="sidebar-wrapper" style="position: relative;" class="bg-dark">
	<ul class="sidebar-nav">
		<c:if test="${isAuthenticated==true }">
			<li class="sidebar-brand text-center"><div
					style="margin-bottom: 10px">
					<h3 class="text-light">
						<i class="fas fa-user-circle"></i>
					</h3>
					<h4 class="text-light">${loggedUser.displayName }</h4>
				</div></li>
		</c:if>
		<c:forEach items="${navigationMenus }" var="menu">
			<li><a href="<spring:url value="${menu.url }"></spring:url>">
					<span><i class="${menu.iconClassName }"></i>&nbsp;${menu.label }</span>
			</a></li>
		</c:forEach>

	</ul>
</div>
</div>