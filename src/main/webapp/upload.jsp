<%--
  Created by IntelliJ IDEA.
  User: kevin.reichmuth
  Date: 02.02.24
  Time: 2:14 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.gson.Gson" %>

<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Upload JSP Page</title>
</head>
<body>

<h1>Uploaded Data</h1>

    <table border="1">
        <tr>
            <th>Ticker</th>
            <th>QTY</th>
        </tr>

        <%
            // Retrieve tickerList and qtyList from query parameters
            String tickerListParam = request.getParameter("tickerList");
            String qtyListParam = request.getParameter("qtyList");

            // Parse JSON strings into arrays
            Gson gson = new Gson();
            String[] tickerList = gson.fromJson(tickerListParam, String[].class);
            int[] qtyList = gson.fromJson(qtyListParam, int[].class);

            // Loop through the arrays to populate the table
            for (int i = 0; i < tickerList.length; i++) {
        %>
        <tr>
            <td>
                <input type="text" value="<%= tickerList[i] %>" readonly>
            </td>
            <td>
                <input type="text" value="<%= qtyList[i] %>" readonly>
            </td>
        </tr>
        <%
            }
        %>
    </table>

</body>
</html>
