<%--
  Created by IntelliJ IDEA.
  User: kevin.reichmuth
  Date: 09.07.22
  Time: 9:13 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.sql.*"%>
<%@ page import="persistance.SqlTable" %>
<html>
<head>
    <title>Title</title>
</head>
<body>

<%
    //Variables
    ResultSet rs = null;
    SqlTable objSqlTable = new SqlTable();

    try {
        String query = "select * from instruments";
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://127.0.0.1:3306/instrumentDB?useSSL=false&allowPublicKeyRetrieval=true";
        String user = "root";
        String password = "Blue_22!";
        Connection conn = DriverManager.getConnection(url, user, password);
        Statement stm = conn.createStatement();
        rs = stm.executeQuery(query);
    }catch (Exception ex){
        ex.printStackTrace();
        out.println("Error "+ex.getMessage());
    }
%>


    <table>

        <tr>
            <th>Ticker</th>
        </tr>
        <tr>
            <td>
                <select name=instrument05 class="form-control" style="width: 250px;">
                    <option inst1="-1">Select Instrument</option>

                    <%
                        while (rs.next()){
                    %>
                    <option><%=rs.getString("name")%></option>
                    <%
                        }
                    %>
                </select>
            </td>
            <td>
                <%
                    String instrument;
                    try {
                        instrument = request.getParameter("instrument05").toString();
                    }catch (Exception ex){
                        instrument = "-";
                    }

                    System.out.println(instrument);
                    String name1 = objSqlTable.getInstrumentData("ticker","name","ABB Ltd");
                %>
                <input readonly type="text" name = "price01" value=<%=name1%>>
            </td>

        </tr>








    </table>










</body>
</html>
