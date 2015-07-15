<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="cp" value="${pageContext.request.servletContext.contextPath}" scope="request" />

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Shortner</title>
        <link type="text/css" rel="stylesheet" href="${cp}/resources/css/site.css" />
        <script type="text/javascript" src="${cp}/resources/js/script.js"></script>
    </head>
    <body>
       
        
        <img src="${cp}/resources/images/images.jpg" class="bg">

        <div class="pos">
	<div class="container-title otto">  Shortner  </div>
        
        <form method="post" action="${cp}/add" >
            
            <div class="txt">
                <label id="lbl_rurl">real URL:</label>
                <input type="text" id="rurl" name="rurl" class="long"/>
            </div>
            
            <br />
            
            <div class="txt">
                <label id="lbl_surl">short URL:</label>
                <input type="text" id="surl" name="surl" class="short"/>
            </div>
            
            <br />
            <div class="mar-auto">
            <input type="submit" value="Add Shortner" class="button"/>
            </div>
        </form>
        </div>
    </body>
</html>