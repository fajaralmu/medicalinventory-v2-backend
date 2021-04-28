<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div>
	<h2>Dashboard</h2>
	<div>
		<div class="alert alert-primary" role="alert">
		${greeting }
		<hr/>
		</div>
		<p>
			<a class="btn btn-dark" href="<c:url value="${applicationURL }"></c:url>">
				Application Page
			</a>
		</p>
	</div>
</div>