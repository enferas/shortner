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

            <form method="post" action="${cp}/add" class="mar-auto">

                <div class="txt">
                    <label id="lbl_rurl">real URL:</label>
                    <c:choose>
                        <c:when test="${not empty LongURL}">
                            <input type="text" id="rurl" name="rurl" class="long" value="${LongURL}" />
                        </c:when>
                        <c:otherwise>
                            <input type="text" id="rurl" name="rurl" class="long"/>
                        </c:otherwise>
                    </c:choose>
                </div>

                <br />

                <div class="txt">
                    <label id="lbl_surl">short URL:</label>
                    <c:choose>
                        <c:when test="${not empty ShortURL}">
                            <input type="text" id="surl" name="surl" class="short line" value="${ShortURL}"/>
                        </c:when>
                        <c:otherwise>
                            <input type="text" id="surl" name="surl" class="short line"/>
                        </c:otherwise>
                    </c:choose>
                      <label>  (Optional)</label>
                </div>

                <br />
                <div>
                    <input type="submit" value="Add Shortner" class="button"/>
                </div>
            </form>

            <c:if test="${not empty LinkShortner}">
                <p class="text"> Link Shortner: ${LinkShortner} <p>
                </c:if>

                <c:if test="${not empty LinkDataShortner}">    
                <p class="text"> Link Statistics Shortner: ${LinkDataShortner} </p>
            </c:if>
                
                <c:if test="${not empty Error}">    
                <p class="text"> Error: ${Error} </p>
            </c:if>

        </div>
    </body>
</html>