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

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="styles.css">
    <title>Portfolio Web Application</title>
    <h1 style="display: inline-block" >Portfolio</h1>
    <h2 style="display: inline-block" >Analyse Tool</h2>
    <br>
    <b>Die Daten wurden erfolgreich übermittelt!</b>
    <br>
    <p>Unten finden Sie Ihr aktuelles Portfolio und das Optimale Portfolio berechnet anhand der Markowitz Portfolio-Theorie!</p>

    <script>

        function back(){
        }

    </script>

</head>

<body>

    <!-- Get tickerList and tickerListSize -->
    <%
        //Variables
        SqlTable sqlTable = new SqlTable();
        ArrayList<String> tickerList = sqlTable.getPortfolioTickers("current");
        int tickerListSize = tickerList.size();
    %>

    <div id="div_portfolio">

        <h3>Übersicht:</h3>

        <table class="table_summary_values">

            <%
                double currentPortfolioValue = sqlTable.getMetricSummaryValue("PORTFOLIO","currentPortfolioValue");
                double currentPortfolioReturn = sqlTable.getMetricSummaryValue("PORTFOLIO","currentPortfolioReturn");
                double currentPortfolioVolatility = sqlTable.getMetricSummaryValue("PORTFOLIO","currentPortfolioVolatility");

            %>

            <tr>
                <td><label for="portfolioWertCurrent">Portfoliowert CHF (aktuell): </label></td>
                <td><input type="text" id="portfolioWertCurrent" class="input_data" value=<%=currentPortfolioValue%> readonly></td>
            </tr>
            <tr>
                <td><label for="portfolioRenditeCurrent">Portfoliorendite in % (aktuell): </label></td>
                <td><input type="text" id="portfolioRenditeCurrent" class="input_data" pattern="[0-9]+" value=<%=currentPortfolioReturn%> readonly></td>
            </tr>
            <tr>
                <td><label for="portfolioVolatilitaetCurrent">Portfoliovolatilität in % (aktuell): </label></td>
                <td><input type="text" id="portfolioVolatilitaetCurrent" class="input_data" pattern="[0-9]+" value=<%=currentPortfolioVolatility%> readonly></td>
            </tr>

        </table>

        <br>
        <br>

        <table>

            <tr>
                <th>Titel</th>
                <th>Ticker</th>
                <th>QTY</th>
                <th>CCY</th>
                <th>Preis</th>
                <th>Weight</th>
                <th>Opt. QTY</th>
                <th>Opt. Weight</th>
                <th>Trade</th>
            </tr>

            <tr>
                <!-- Get tickerList and tickerListSize -->
                <%
                    String instrument01 = null;
                    String ticker01 = null;
                    int qty01 = 0;
                    String ccy01 = null;
                    double price01 = 0;
                    double weight01 = 0;
                    int optQty01 = 0;
                    double optWeight01 = 0;
                    int trade01 = 0;

                    if(tickerListSize>0){
                        ticker01 = tickerList.get(0);
                        instrument01 = sqlTable.getInstrumentData("name","ticker",ticker01);
                        qty01 = sqlTable.getPortfolioQuantity(ticker01,"current");
                        ccy01 = sqlTable.getInstrumentData("ccy","ticker",ticker01);
                        price01 = sqlTable.getLatestPrice(ticker01);
                        weight01 = sqlTable.getPortfolioWeight(ticker01,"current");
                        // UPDATE minRisk or targetReturn
                        optQty01 = sqlTable.getPortfolioQuantity(ticker01,"minRisk");
                        optWeight01 = sqlTable.getPortfolioWeight(ticker01,"minRisk");
                        // UPDATE trade (calculation)
                        trade01 = 1;
                    }
                %>

                <td>
                    <input type="text" name="instrument01" class="input_data_instrument" size="10" value=<%=instrument01%> readonly>
                </td>
                <td>
                    <input type="text" name="ticker01" class="input_data" size="10" value=<%=ticker01%> readonly>
                </td>
                <td>
                    <input type="text" name="qty01" class="input_data" size="10" pattern="[0-9]+" value=<%=qty01%> readonly>
                </td>
                <td>
                    <input type="text" name="ccy01" class="input_data" size="10" value=<%=ccy01%> readonly>
                </td>
                <td>
                    <input type="text" name="price01" class="input_data" size="10" value=<%=price01%> readonly>
                </td>
                <td>
                    <input type="text" name="weight01" class="input_data" size="10" pattern="[0-9]+" value=<%=weight01%> readonly>
                </td>
                <td>
                    <input type="text" name="optQty01" class="input_data" size="10" pattern="[0-9]+" value=<%=optQty01%> readonly>
                </td>
                <td>
                    <input type="text" name="optWeight01" class="input_data" size="10" pattern="[0-9]+" value=<%=optWeight01%> readonly>
                </td>
                <td>
                    <input type="text" name="trade01" class="input_data" size="10" pattern="[0-9]+" value=<%=trade01%> readonly>
                </td>
            </tr>
        </table>
    </div>

    <div id="div_opt_portfolio">

        <h3>Optimales Portfolio:</h3>

        <table class="table_summary_values">
            <tr>
                <td><label for="portfolioWertOptimal">Portfoliowert CHF (optimal): </label></td>
                <td><input type="text" id="portfolioWertOptimal" value="" class="input_data" readonly></td>
            </tr>
            <tr>
                <td><label for="portfolioRenditeOptimal">Portfoliorendite in % (optimal): </label></td>
                <td><input type="text" id="portfolioRenditeOptimal" value="" class="input_data" pattern="[0-9]+" readonly></td>
            </tr>
            <tr>
                <td><label for="portfolioVolatilitaetOptimal">Portfoliovolatilität in % (optimal): </label></td>
                <td><input type="text" id="portfolioVolatilitaetOptimal" value="" class="input_data" pattern="[0-9]+" readonly></td>
            </tr>
        </table>

    </div>

    <br>
    <button type="button" onclick="back()">Back</button>

</body>

</html>