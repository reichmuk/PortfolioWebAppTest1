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
<%@ page import="java.util.ArrayList" %>
<html>
<head>
    <title>Title</title>
</head>
<body>

<%
    //Variables
    SqlTable sqlTable = new SqlTable();
    ArrayList<String> tickerList = sqlTable.getPortfolioTickers("current");
    int tickerListSize = tickerList.size();


%>

    <table>

        <tr>
            <th>Titel</th>
            <th>Ticker</th>
            <th>Anzahl</th>
            <th>CCY</th>
            <th>Preis</th>
            <th>Weight %</th>
            <th>% Change</th>
        </tr>

        <tr>
            <%
                String ticker0=null;
                String instrument0=null;
                String ccy0=null;
                String country0=null;
                double price0 = 0;
                double weight0 = 0;
                if(tickerListSize>0){
                    ticker0 = tickerList.get(0);
                    instrument0 = sqlTable.getInstrumentData("name","ticker",ticker0);
                    ccy0 = sqlTable.getInstrumentData("ccy","ticker",ticker0);
                    country0 = sqlTable.getInstrumentData("country","ticker",ticker0);
                    price0 = sqlTable.getLatestPrice(ticker0);
                    weight0 = sqlTable.getPortfolioWeight(ticker0,"current");
                }

            %>
            <td>
                <input readonly type="text" name = "instrument00" value=<%=instrument0%>>
            </td>
            <td>
                <input readonly type="text" name = "ticker00" value=<%=ticker0%>>
            </td>
            <td>
                <input readonly type="text" name = "ccy00" value=<%=ccy0%>>
            </td>
            <td>
                <input readonly type="text" name = "country00" value=<%=country0%>>
            </td>
            <td>
                <input readonly type="text" name = "country00" value=<%=price0%>>
            </td>
            <td>
                <input readonly type="text" name = "country00" value=<%=weight0%>>
            </td>
        </tr>


    </table>










</body>
</html>
