<?xml version="1.0" encoding="UTF-8" ?>
<%@ page import="app.Person" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:f="http://java.sun.com/jsf/core"
      xmlns:h="http://java.sun.com/jsf/html">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>Управление номером телефона</title>
</head>
<body>
<%
    HashMap<String, String> jsp_parameters = new HashMap<String, String>();
    Person person = new Person();
    String idPhone = "";
    String submitButtonText;
    String error_message = "";
    if (request.getAttribute("jsp_parameters") != null) {
        jsp_parameters = (HashMap<String, String>) request.getAttribute("jsp_parameters");
    }
    if (request.getAttribute("person") != null) {
        person = (Person) request.getAttribute("person");
    }
    if (request.getAttribute("idPhone") != null) {
        idPhone = (String) request.getAttribute("idPhone");
        submitButtonText = "Сохранить номер";
    } else {
        submitButtonText = "Добавить номер";
    }
    error_message = jsp_parameters.get("error_message");
%>
<table align="center" border="1" cellpadding="2" width="50%">
    <%
        if ((error_message != null) && (!error_message.equals(""))) {
    %>
    <tr>
        <td colspan="2" align="center"><span style="color:red"><%=error_message%></span></td>
    </tr>
    <%
        }
    %>
    <tr>
        <td colspan="2">Информация о телефоне владельца:
            <%=" " + person.getSurname() + " " + person.getName() + " " + person.getMiddlename()%>
        </td>
    </tr>
    <tr>
        <td>Номер:</td>
        <form action="${pageContext.request.contextPath}/ManagePhone/" method="post">
                <%
            if (!idPhone.isEmpty()) {
        %>
            <td>
                <input type="text" name="phone" value="<%=person.getPhones().get(idPhone)%>"/>
                <input type="text" name="idPhone" hidden="true" value="<%=idPhone%>"/>
                <input type="text" name="editPhoneGo" hidden="true" value="edit"/>
            </td>
                <%
            } else {
                %>
            <td><input type="text" name="phone" value=""/></td>
            <input type="text" name="addPhoneGo" hidden="true" value="add"/>
                <%
            }
        %>
    </tr>
    <tr>
        <td colspan="2" align="center">
            <input type="text" name="id" hidden="true" value="<%=person.getId()%>"/>
            <input type="submit" value="<%=submitButtonText%>"/><br/>
            <a href="${pageContext.request.contextPath}/ManagePerson/?action=edit&id=<%=person.getId()%>&idPhone=<%=idPhone%>">Вернуться к
                данным о
                человеке</a>
        </td>
    </tr>
</table>
</form>
</body>
</html>