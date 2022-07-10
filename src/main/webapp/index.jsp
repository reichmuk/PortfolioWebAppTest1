<%--
  Created by IntelliJ IDEA.
  User: kevin.reichmuth
  Date: 19.05.22
  Time: 11:20 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.sql.*"%>
<html>
<head>
    <link rel="stylesheet" href="styles.css">
    <title>PortfolioWebApp</title>
    <title>Portfolio Web Application</title>
    <div class="header">
        <h1 style="display: inline-block" >Portfolio</h1>
        <h2 style="display: inline-block" >Analyse Tool</h2>
    </div>
</head>
<body>

<%
    //Variables
    ResultSet rs = null;
    Statement stm = null;
    String query = null;

    try {
        query = "select * from instruments";
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://127.0.0.1:3306/instrumentDB?useSSL=false&allowPublicKeyRetrieval=true";
        String user = "root";
        String password = "Blue_22!";
        Connection conn = DriverManager.getConnection(url, user, password);
        stm = conn.createStatement();
        rs = stm.executeQuery(query);
    }catch (Exception ex){
        ex.printStackTrace();
        out.println("Error "+ex.getMessage());
    }
%>

    <p>Initialize-Servlet <a href="MyServlet">here</a></p>
    <p>Reset-Servlet <a href="ResetServlet">here</a></p>
    <p>Run-Servlet <a href="RunServlet">here</a></p>
    <p>Result-Page <a href="result.jsp">here</a></p>


    <form action="MyServlet" method="post">

        <label style="margin-right: 10px">Instrument 1:</label>

        <select name=instrument1 class="form-control" style="width: 250px;">
            <option inst1="-1">Select Instrument</option>
            <%
                rs = stm.executeQuery(query);
                while (rs.next()){
            %>
            <option><%=rs.getString("name")%></option>
            <%
                }
            %>
        </select>

        Quantity: <input type = "text" name = "quantity1">

        <br/>
        <br/>

        <label style="margin-right: 10px">Instrument 2:</label>
        <select name=instrument2 class="form-control" style="width: 250px;">
            <option inst2="-1">Select Instrument</option>

            <%
                rs = stm.executeQuery(query);
                while (rs.next()){
            %>
            <option><%=rs.getString("name")%></option>
            <%
                }
            %>
        </select>

        Quantity: <input type = "text" name = "quantity2">

        <br/>
        <br/>

        <label style="margin-right: 10px">Instrument 3:</label>
        <select name=instrument3 class="form-control" style="width: 250px;">
            <option inst3="-1">Select Instrument</option>
            <%
                rs = stm.executeQuery(query);
                while (rs.next()){
            %>
            <option><%=rs.getString("name")%></option>
            <%
                }
            %>
        </select>
        Quantity: <input type = "text" name = "quantity3">

        <br/>
        <br/>

        <button type="submit">Submit</button>
    </form>

</body>

</html>
