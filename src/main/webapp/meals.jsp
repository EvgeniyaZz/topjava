<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html lang="ru">
<head>
    <title>Meal list</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Meals</h2>
<h4><a href="meals?id=&action=edit">Add meal</a></h4>
<table border="1" cellpadding="8" cellspacing="0">
    <tr>
        <th>Date</th>
        <th>Description</th>
        <th>Calories</th>
        <th></th>
        <th></th>
    </tr>
    <c:forEach items="${mealsTo}" var="mealTo">
        <jsp:useBean id="mealTo" type="ru.javawebinar.topjava.model.MealTo"/>
        <tr style="color: ${(mealTo.excess ? 'red' : 'green')}">
            <td>
                <fmt:parseDate value="${mealTo.dateTime}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDateTime"
                               type="both"/>
                <fmt:formatDate pattern="yyyy-MM-dd HH:mm" value="${parsedDateTime}"/>
            </td>
            <td>${mealTo.description}</td>
            <td>${mealTo.calories}</td>
            <td><a href="meals?id=${mealTo.id}&action=edit">Update</a></td>
            <td><a href="meals?id=${mealTo.id}&action=delete">Delete</a></td>
        </tr>
    </c:forEach>
</table>
</body>
</html>