<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html lang="ru">
<head>
    <title>Meal</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<section>
    <form method="post" action="meals" enctype="application/x-www-form-urlencoded">
        <jsp:useBean id="meal" type="ru.javawebinar.topjava.model.Meal" scope="request"/>
        <c:choose>
            <c:when test="${meal.id == null}">
                <h2>Add meal</h2>
            </c:when>
            <c:otherwise>
                <h2>Edit meal</h2>
            </c:otherwise>
        </c:choose>
        <input type="hidden" name="id" value="${meal.id}">
        <table>
            <tr>
                <td>DateTime</td>
                <td><input type="datetime-local" name="dateTime" value="${meal.dateTime}"></td>
            </tr>
            <tr>
                <td>Description</td>
                <td><input type="text" name="description" size=30 value="${meal.description}"></td>
            </tr>
            <tr>
                <td>Calories</td>
                <td><input type="text" name="calories" size=30 value="${meal.calories}"></td>
            </tr>
        </table>
        <button type="submit">Save</button>
        <button onclick="window.history.back()" type="button">Cancel</button>
    </form>
</section>
</body>
</html>