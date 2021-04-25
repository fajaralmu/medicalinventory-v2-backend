<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ct" uri="customTag"%>
<div class="text-center">


	<c:if test="${not empty errorMessge}">
		<ct:warning message="${errorMessge}"/>
	</c:if>
	<form:form id="form-signin" cssClass="form-signin" method="POST" name="login" 
	 action="${pageContext.request.contextPath}/login"
	>
	 
		<h2><i class="fas fa-user-circle"></i></h2>
		<h1 class="h3 mb-3 font-weight-normal">Please sign in</h1>
		<label for="username" class="sr-only">Username</label>
		<input name="username" type="text" id="username" class="form-control"
			placeholder="Username" required autofocus>
			
		<label for="inputPassword" class="sr-only">Password</label>
		<input name="password" type="password" id="inputPassword" class="form-control"
			placeholder="Password" required>
		<div class="checkbox mb-3">
			<label> <input type="checkbox" value="remember-me"> Remember me </label>
		</div>
		<button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
		 <input type="hidden" name="${_csrf.parameterName}"
			value="${_csrf.token}" />
		<input name="transport_type" type="hidden" value="web">
	</form:form>
 
</div>

