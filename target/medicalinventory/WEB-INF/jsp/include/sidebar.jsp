
<%@page import="org.springframework.beans.factory.annotation.Autowired"%>
<%@ page language="java" contentType="text/html; charset=windows-1256"
	pageEncoding="windows-1256"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!-- <nav class="navbar navbar-expand-lg "> -->
<nav class="navbar">
	<div>
		<c:if test="${isAuthenticated==true }">
			<div style="text-align: center; padding-top: 10px; padding-bottom: 10px">
				<h3><i class="fas fa-user-circle"></i></h3>
				<h4>${userPrincipal.getUsername()}</h4>
			</div>
		</c:if>
		
		<ul class="list-group">
			<c:forEach items="${navigationMenus }" var="menu">
				<li class="list-group-item">
					<a href="<spring:url value="${menu.url }"></spring:url>">
						<span><i class="${menu.iconClassName }"></i>&nbsp;${menu.label }</span>
					</a>
				</li>
			</c:forEach>
		</ul>
	</div>
</nav>
