<%@ page language="java" contentType="text/html; charset=windows-1256"
	pageEncoding="windows-1256"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type"
	content="text/html; charset=windows-1256">
<meta name="description" content="${applicationDescription }">
<meta property="og:title" content="${applicationHeaderLabel }">
<meta property="og:description" content="${applicationDescription }">
<meta property="og:site_name" content="${applicationHeaderLabel }">
<meta property="og:image" itemprop="image"
	content="https://realtime-videocall.herokuapp.com/res/img/Flag_of_Indonesia_200.png">
<meta property="og:type" content="website">

<title>${title}</title>
<link rel="icon" href="<c:url value="/res/img/javaEE.ico"></c:url >"
	type="image/x-icon">

<link rel="stylesheet" type="text/css"
	href="<c:url value="/res/css/app.css?version=1"></c:url>" />
<link rel="stylesheet" type="text/css"
	href="<c:url value="/res/css/sidebar.css?version=1"></c:url>" />
<link rel="stylesheet" type="text/css"
	href="<c:url value="/res/fa/css/all.css" />" />
<link rel="stylesheet"
	href="<c:url value="/res/css/bootstrap/bootstrap.min.css" />" />
<script src="<c:url value="/res/js/jquery-3.3.1.slim.min.js" />"></script>
<script src="<c:url value="/res/js/popper.min.js" />"></script>
<script src="<c:url value="/res/js/axios.min.js" />"></script>
<script src="<c:url value="/res/js/bootstrap/bootstrap.min.js"  />"></script>
<script src="<c:url value="/res/js/sockjs-0.3.2.min.js"></c:url >"></script>
<script src="<c:url value="/res/js/stomp.js"></c:url >"></script>
<script src="<c:url value="/res/js/websocket-util.js"></c:url >"></script>
<script src="<c:url value="/res/js/ajax.js?v=1"></c:url >"></script>
<script src="<c:url value="/res/js/util.js?v=1"></c:url >"></script>
<script src="<c:url value="/res/js/dialog.js?v=1"></c:url >"></script>

<script src="<c:url value="/res/fa/js/all.js?v=1"></c:url >"></script>

<c:forEach var="stylePath" items="${additionalStylePaths }">
	<link rel="stylesheet"
		href="<c:url value="/res/css/pages/${ stylePath.value}.css?version=1"></c:url >" />
</c:forEach>
<c:forEach var="scriptPath" items="${additionalScriptPaths }">
	<script
		src="<c:url value="/res/js/pages/${scriptPath.value }.js?v=1"></c:url >"></script>
</c:forEach>
</head>
<body>
	<div id="progress-bar-wrapper" onclick="hide('progress-bar-wrapper');"
		class="box-shadow"
		style="display: none; height: 50px; padding: 10px; background-color: white; margin: auto; position: fixed; width: 100%">
		<div class="progress">
			<div id="progress-bar"
				class="progress-bar progress-bar-striped bg-info" role="progressbar"
				aria-valuenow="0" aria-valuemin="0" aria-valuemax="100"></div>
		</div>
	</div>
	<div id="loading-div"></div>
	<jsp:include page="include/headerv2.jsp"></jsp:include>

	<jsp:include page="include/sidebarv2.jsp"></jsp:include>

	<div id="page-content" class="container-fluid" style="min-height: 80vh">
		<jsp:include page="${pageUrl == null? 'error/notfound': pageUrl}.jsp"></jsp:include>
	</div>

	<jsp:include page="include/foot.jsp"></jsp:include>
</body>
</html>