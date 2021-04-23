
<%@page import="org.springframework.beans.factory.annotation.Autowired"%>
<%@ page language="java" contentType="text/html; charset=windows-1256"
	pageEncoding="windows-1256"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
	<a class="navbar-brand" href="#">${applicationHeaderLabel }</a>
	<button class="navbar-toggler" type="button" data-toggle="collapse"
		data-target="#navbarTogglerDemo03" aria-controls="navbarTogglerDemo03"
		aria-expanded="false" aria-label="Toggle navigation">
		<span class="navbar-toggler-icon"></span>
	</button>
	<div class="collapse navbar-collapse" id="navbarTogglerDemo03">
	 
		<ul id="navbar-top" class="navbar-nav mr-auto mt-2 mt-lg-0">
			<!--    <li class="nav-item active">
            <a class="nav-link" href="#" onclick="showView('app-view', this)">App</a>
          </li>  -->
			<c:forEach items="${navigationMenus }" var="menu">
				<li class="nav-item "><a style="margin-left: 10px"
					class="nav-link"
					href="<spring:url value="${menu.url }"></spring:url>"> <span><i
							class="${menu.iconClassName }"></i>&nbsp;${menu.label }</span>
				</a></li>
			</c:forEach>

			</li>
		</ul>
		<form class="form-inline my-2 my-lg-0">
			<c:if test="${isAuthenticated == true }">

				<a style="margin-right: 5px" class="btn btn-success my-2 my-sm-0"
					href='<spring:url value="/app/profile"></spring:url>'> <i
					class="fas fa-user-circle"></i>&nbsp;${loggedUser.displayName }
				</a>
				<a class="btn btn-danger my-2 my-sm-0"
					href='<spring:url value="/logout"></spring:url>'> <i
					class="fas fa-sign-out-alt"></i>&nbsp;Logout
				</a>

			</c:if>
			<c:if test="${isAuthenticated == false }">
			
				<a class="btn btn-info my-2 my-sm-0"
					href='<spring:url value="/login"></spring:url>'> <i
					class="fas fa-sign-in-alt"></i>&nbsp;Login
				</a>
			</c:if>
		</form>
	</div>
</nav>