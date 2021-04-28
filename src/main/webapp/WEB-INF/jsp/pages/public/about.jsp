<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div>
	<h2>About</h2>
	<hr/>
	<div class="row">
		<div class="col-md-3">Name</div>
		<div class="col-md-9">${profile.name }</div>
		<div class="col-md-3">Description</div>
		<div class="col-md-9">${profile.shortDescription }</div>
		<div class="col-md-3">Contact</div>
		<div class="col-md-9">${profile.contact }</div>
		<div class="col-md-3">Website</div>
		<div class="col-md-9">${profile.website }</div>
	</div>
</div>