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
    <title>PortfolioWebApp</title>
</head>
<body>
    <h1>Portfolio Web App!!</h1>
    <p>Initialize-Servlet <a href="MyServlet">here</a></p>
    <p>Reset-Servlet <a href="ResetServlet">here</a></p>
    <p>Run-Servlet <a href="RunServlet">here</a></p>


    <form action="MyServlet" method="post">
        <label style="margin-right: 10px">Instrument 1:</label>
        <select name=instrument1 class="form-control" style="width: 250px;">
            <option inst1="-1">Select Instruments</option>
            <%
            try{
                String query ="select * from instruments";
                Class.forName("com.mysql.cj.jdbc.Driver");
                String url = "jdbc:mysql://127.0.0.1:3306/instrumentDB?useSSL=false&allowPublicKeyRetrieval=true";
                String user = "root";
                String password = "Blue_22!";
                Connection conn = DriverManager.getConnection(url,user,password);
                Statement stm = conn.createStatement();
                ResultSet rs = stm.executeQuery(query);
                while (rs.next()){
                %>
                <option inst1="<%=rs.getString("ticker")%>"><%=rs.getString("name")%></option>
                <%
                }
            }catch (Exception ex){
                ex.printStackTrace();
                out.println("Error "+ex.getMessage());
            }
            %>
        </select>

        <br/>
        <br/>

        <label style="margin-right: 10px">Instrument 2:</label>
        <select name=instrument2 class="form-control" style="width: 250px;">
            <option inst2="-1">Select Instruments</option>
            <%
                try{
                    String query ="select * from instruments";
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    String url = "jdbc:mysql://127.0.0.1:3306/instrumentDB?useSSL=false&allowPublicKeyRetrieval=true";
                    String user = "root";
                    String password = "Blue_22!";
                    Connection conn = DriverManager.getConnection(url,user,password);
                    Statement stm = conn.createStatement();
                    ResultSet rs = stm.executeQuery(query);
                    while (rs.next()){
            %>
            <option inst2="<%=rs.getString("ticker")%>"><%=rs.getString("name")%></option>
            <%
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                    out.println("Error "+ex.getMessage());
                }
            %>
        </select>

        <br/>
        <br/>

        <label style="margin-right: 10px">Instrument 3:</label>
        <select name=instrument3 class="form-control" style="width: 250px;">
            <option inst3="-1">Select Instruments</option>
            <%
                try{
                    String query ="select * from instruments";
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    String url = "jdbc:mysql://127.0.0.1:3306/instrumentDB?useSSL=false&allowPublicKeyRetrieval=true";
                    String user = "root";
                    String password = "Blue_22!";
                    Connection conn = DriverManager.getConnection(url,user,password);
                    Statement stm = conn.createStatement();
                    ResultSet rs = stm.executeQuery(query);
                    while (rs.next()){
            %>
            <option inst3="<%=rs.getString("ticker")%>"><%=rs.getString("name")%></option>
            <%
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                    out.println("Error "+ex.getMessage());
                }
            %>
        </select>

        <br/>
        <br/>

        <button type="submit">Submit</button>
    </form>


</body>

</html>
