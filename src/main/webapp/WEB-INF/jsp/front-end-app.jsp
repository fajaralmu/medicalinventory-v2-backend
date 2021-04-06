<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
   
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8" />
    <link rel="icon" href="<c:url value="/res/react_resources/favicon.ico"/>" />
    <meta name="viewport" content="width=device-width,initial-scale=1" />
    <meta name="theme-color" content="#000000" />
    <meta
      name="description"
      content="Web site created using create-react-app"
    />
    
    <link rel="stylesheet" type="text/css" href="<c:url value="/res/fa/css/all.css" />" />
	<link rel="stylesheet" href="<c:url value="/res/css/bootstrap/bootstrap.min.css" />" />
	
	<script src="<c:url value="/res/js/jquery-3.3.1.slim.min.js" />"></script>
	<script src="<c:url value="/res/js/bootstrap/bootstrap.min.js"  />"></script>

    <link rel="stylesheet" href="<c:url value="/res/react_resources/resources/css/app.css"/>" />
    <link rel="apple-touch-icon" href="<c:url value="/res/react_resources/logo192.png"/>" />
    <link rel="manifest" href="<c:url value="/res/react_resources/manifest.json"/>" />
    <title>Medical Inventory</title>
    <link rel="stylesheet" href="<c:url value="/res/react_resources/static/css/main.9e1886b2.chunk.css"/>" />
  </head>
  <body>
    <noscript>You need to enable JavaScript to run this app.</noscript>
    <form>
      <input
        type="hidden"
        id="rootPath"
        value='<c:url value="/"></c:url>'
      />
      
    </form>
    <div id="root"></div>
    <script>
      !(function (e) {
        function r(r) {
          for (
            var n, i, l = r[0], a = r[1], f = r[2], p = 0, s = [];
            p < l.length;
            p++
          )
            (i = l[p]),
              Object.prototype.hasOwnProperty.call(o, i) &&
                o[i] &&
                s.push(o[i][0]),
              (o[i] = 0);
          for (n in a)
            Object.prototype.hasOwnProperty.call(a, n) && (e[n] = a[n]);
          for (c && c(r); s.length; ) s.shift()();
          return u.push.apply(u, f || []), t();
        }
        function t() {
          for (var e, r = 0; r < u.length; r++) {
            for (var t = u[r], n = !0, l = 1; l < t.length; l++) {
              var a = t[l];
              0 !== o[a] && (n = !1);
            }
            n && (u.splice(r--, 1), (e = i((i.s = t[0]))));
          }
          return e;
        }
        var n = {},
          o = { 1: 0 },
          u = [];
        function i(r) {
          if (n[r]) return n[r].exports;
          var t = (n[r] = { i: r, l: !1, exports: {} });
          return e[r].call(t.exports, t, t.exports, i), (t.l = !0), t.exports;
        }
        (i.m = e),
          (i.c = n),
          (i.d = function (e, r, t) {
            i.o(e, r) ||
              Object.defineProperty(e, r, { enumerable: !0, get: t });
          }),
          (i.r = function (e) {
            "undefined" != typeof Symbol &&
              Symbol.toStringTag &&
              Object.defineProperty(e, Symbol.toStringTag, { value: "Module" }),
              Object.defineProperty(e, "__esModule", { value: !0 });
          }),
          (i.t = function (e, r) {
            if ((1 & r && (e = i(e)), 8 & r)) return e;
            if (4 & r && "object" == typeof e && e && e.__esModule) return e;
            var t = Object.create(null);
            if (
              (i.r(t),
              Object.defineProperty(t, "default", { enumerable: !0, value: e }),
              2 & r && "string" != typeof e)
            )
              for (var n in e)
                i.d(
                  t,
                  n,
                  function (r) {
                    return e[r];
                  }.bind(null, n)
                );
            return t;
          }),
          (i.n = function (e) {
            var r =
              e && e.__esModule
                ? function () {
                    return e.default;
                  }
                : function () {
                    return e;
                  };
            return i.d(r, "a", r), r;
          }),
          (i.o = function (e, r) {
            return Object.prototype.hasOwnProperty.call(e, r);
          }),
          (i.p = "/");
        var l = (this["webpackJsonpmedical-inventory"] =
            this["webpackJsonpmedical-inventory"] || []),
          a = l.push.bind(l);
        (l.push = r), (l = l.slice());
        for (var f = 0; f < l.length; f++) r(l[f]);
        var c = a;
        t();
      })([]);
    </script>
    <script src="<c:url value="/res/react_resources/static/js/2.a4fddafe.chunk.js"/>"></script>
    <script src="<c:url value="/res/react_resources/static/js/main.6196a5eb.chunk.js"/>"></script>
  </body>
</html>
