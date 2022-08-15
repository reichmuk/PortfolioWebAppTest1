<%--
  Created by IntelliJ IDEA.
  User: kevin.reichmuth
  Date: 31.08.2022
  Time: 08:00 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.sql.*"%>
<%@ page import="persistance.SqlTable" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="domain.Control" %>
<%@ page import="domain.Calculations" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="constants.Constants" %>

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


</head>

<body>

    <!-- Get tickerList and tickerListSize -->
    <%
        //Variables
        SqlTable sqlTable = Control.getSqlTable();
        Calculations calculations = Control.getCalculations();
        ArrayList<String> tickerList = sqlTable.getPortfolioTickers(Constants.CURRENT);
        String strategy = calculations.getStrategy();
        int tickerListSize = tickerList.size();
        DecimalFormat df2 = new DecimalFormat("#.00");
    %>

    <div id="div_portfolio">

        <h3>Übersicht:</h3>

        <table class="table_summary_values">

            <%
                int intCurrentPortfolioValue = (int) sqlTable.getMetricSummaryValue(Constants.PORTFOLIO,Constants.CURRENTPORTFOLIOVALUE);
                String currentPortfolioValue = String.format("%,d",intCurrentPortfolioValue);
                DecimalFormat df = new DecimalFormat("#.000000");
                double doubleCurrentPortfolioReturn = sqlTable.getMetricSummaryValue(Constants.PORTFOLIO,Constants.CURRENTPORTFOLIORETURN);
                double doubleCurrentPortfolioVolatility = sqlTable.getMetricSummaryValue(Constants.PORTFOLIO,Constants.CURRENTPORTFOLIOVOLATILITY);
                String currentPortfolioReturn = df.format(sqlTable.getMetricSummaryValue(Constants.PORTFOLIO,Constants.CURRENTPORTFOLIORETURN)*100)+"%";
                String currentPortfolioVolatility = df.format(sqlTable.getMetricSummaryValue(Constants.PORTFOLIO,Constants.CURRENTPORTFOLIOVOLATILITY)*100)+"%";
            %>

            <tr>
                <td><label for="portfolioValueCurrent">Portfoliowert CHF (aktuell): </label></td>
                <td><input type="text" id="portfolioValueCurrent" class="input_data" value=<%=currentPortfolioValue%> readonly></td>
            </tr>
            <tr>
                <td><label for="portfolioReturnCurrent">Portfoliorendite in % (aktuell): </label></td>
                <td><input type="text" id="portfolioReturnCurrent" class="input_data <%=doubleCurrentPortfolioReturn<0 ? "negative" : "" %>" value=<%=currentPortfolioReturn%> readonly></td>
            </tr>
            <tr>
                <td><label for="portfolioVolatilityCurrent">Portfoliovolatilität in % (aktuell): </label></td>
                <td><input type="text" id="portfolioVolatilityCurrent" class="input_data <%=doubleCurrentPortfolioVolatility<0 ? "negative" : "" %>" value=<%=currentPortfolioVolatility%> readonly></td>
            </tr>

        </table>

        <br>
        <br>

        <table>
            <tr>
                <th size="35">Titel</th>
                <th size="15">Ticker</th>
                <th size="10">QTY</th>
                <th size="10">CCY</th>
                <th size="10">Preis</th>
                <th size="15">Weight %</th>
                <th size="10">Opt. QTY</th>
                <th size="15">Opt. Weight %</th>
                <th size="10">Trade</th>
            </tr>

            <%
                for(int i = 0; i<tickerListSize;i++){
            %>
            <tr>
                <!-- Get tickerList and tickerListSize -->
                <%
                    String instrument = null;
                    String ticker = null;
                    int qty = 0;
                    String ccy = null;
                    double price = 0;
                    String weight = null;
                    int optQty = 0;
                    String optWeight = null;
                    double doubleOptWeight = 0;
                    int trade = 0;

                    ticker = tickerList.get(i);
                    instrument = sqlTable.getInstrumentData(Constants.NAME,Constants.TICKER,ticker);
                    qty = sqlTable.getPortfolioQuantity(ticker,Constants.CURRENT);
                    ccy = sqlTable.getInstrumentData(Constants.CCY,Constants.TICKER,ticker);
                    price = sqlTable.getLatestPrice(ticker);
                    weight = df2.format(sqlTable.getPortfolioWeight(ticker,Constants.CURRENT)*100)+"%";
                        // UPDATE minRisk or targetReturn
                        if(strategy.equals(Constants.TARGETRETURN)){
                            optQty = sqlTable.getPortfolioQuantity(ticker,Constants.TARGETRETURN);
                            optWeight = df2.format(sqlTable.getPortfolioWeight(ticker,Constants.TARGETRETURN)*100)+"%";
                            doubleOptWeight = sqlTable.getPortfolioWeight(ticker,Constants.TARGETRETURN);
                            trade = optQty-qty;
                        }

                        if(strategy.equals(Constants.MINRISK)){
                            optQty = sqlTable.getPortfolioQuantity(ticker,Constants.MINRISK);
                            optWeight = df2.format(sqlTable.getPortfolioWeight(ticker,Constants.MINRISK)*100)+"%";
                            doubleOptWeight = sqlTable.getPortfolioWeight(ticker,Constants.MINRISK);
                            trade = optQty-qty;
                        }
                %>

                <td>
                    <input type="text" class="input_data_instrument" size="35" value="<%=instrument%>" readonly>
                </td>
                <td>
                    <input type="text" class="input_data" size="15" value=<%=ticker%> readonly>
                </td>
                <td>
                    <input type="text" class="input_data" size="10" value=<%=qty%> readonly>
                </td>
                <td>
                    <input type="text" class="input_data" size="10" value=<%=ccy%> readonly>
                </td>
                <td>
                    <input type="text" class="input_data" size="10" value=<%=price%> readonly>
                </td>
                <td>
                    <input type="text" class="input_data" size="15" pattern="[0-9]+" value=<%=weight%> readonly>
                </td>
                <td>
                    <input type="text" class="input_data <%=optQty<0 ? "negative" : "" %>" size="10" value=<%=optQty%> readonly>
                </td>
                <td>
                    <input type="text" class="input_data <%=doubleOptWeight<0 ? "negative" : "" %>" size="15" value=<%=optWeight%> readonly>
                </td>
                <td>
                    <input type="text" class="input_data <%=trade<0 ? "negative" : "" %>" size="10" value=<%=trade%> readonly>
                </td>
            </tr>
            <%
                }
            %>

        </table>
    </div>

    <div id="div_opt_portfolio">

        <h3>Optimales Portfolio:</h3>

        <table class="table_summary_values">

            <%
                int intOptimalPortfolioValue = 0;
                String optimalPortfolioReturn = "";
                String optimalPortfolioVolatility = "";
                double doubleOptimalPortfolioReturn = 0;
                double doubleOptimalPortfolioVolatility = 0;

                if(strategy.equals(Constants.TARGETRETURN)){
                    intOptimalPortfolioValue = (int) sqlTable.getMetricSummaryValue(Constants.PORTFOLIO,Constants.TARGETRETURNPORTFOLIOVALUE);
                    optimalPortfolioReturn = df.format(sqlTable.getMetricSummaryValue(Constants.PORTFOLIO,Constants.TARGETRETURNPORTFOLIORETURN)*100)+"%";
                    optimalPortfolioVolatility = df.format(sqlTable.getMetricSummaryValue(Constants.PORTFOLIO,Constants.TARGETRETURNPORTFOLIOVOLATILITY)*100)+"%";
                    doubleOptimalPortfolioReturn = sqlTable.getMetricSummaryValue(Constants.PORTFOLIO,Constants.TARGETRETURNPORTFOLIORETURN);
                    doubleOptimalPortfolioVolatility = sqlTable.getMetricSummaryValue(Constants.PORTFOLIO,Constants.TARGETRETURNPORTFOLIOVOLATILITY);

                }

                if(strategy.equals(Constants.MINRISK)){
                    intOptimalPortfolioValue = (int) sqlTable.getMetricSummaryValue(Constants.PORTFOLIO,Constants.MINRISKPORTFOLIOVALUE);
                    optimalPortfolioReturn = df.format(sqlTable.getMetricSummaryValue(Constants.PORTFOLIO,Constants.MINRISKPORTFOLIORETURN)*100)+"%";
                    optimalPortfolioVolatility = df.format(sqlTable.getMetricSummaryValue(Constants.PORTFOLIO,Constants.MINRISKPORTFOLIOVOLATILITY)*100)+"%";
                    doubleOptimalPortfolioReturn = sqlTable.getMetricSummaryValue(Constants.PORTFOLIO,Constants.MINRISKPORTFOLIORETURN);
                    doubleOptimalPortfolioVolatility = sqlTable.getMetricSummaryValue(Constants.PORTFOLIO,Constants.MINRISKPORTFOLIOVOLATILITY);
                }
                String optimalPortfolioValue = String.format("%,d",intOptimalPortfolioValue);

            %>

            <tr>
                <td><label for="portfolioValueOptimal">Portfoliowert CHF (optimal): </label></td>
                <td><input type="text" id="portfolioValueOptimal" class="input_data" value=<%=optimalPortfolioValue%> readonly></td>
            </tr>
            <tr>
                <td><label for="portfolioReturnOptimal">Portfoliorendite in % (optimal): </label></td>
                <td><input type="text" id="portfolioReturnOptimal" class="input_data <%=doubleOptimalPortfolioReturn<0 ? "negative" : "" %>" value=<%=optimalPortfolioReturn%> readonly></td>
            </tr>
            <tr>
                <td><label for="portfolioVolatilityOptimal">Portfoliovolatilität in % (optimal): </label></td>
                <td><input type="text" id="portfolioVolatilityOptimal" class="input_data <%=doubleOptimalPortfolioVolatility<0 ? "negative" : "" %>" value=<%=optimalPortfolioVolatility%> readonly></td>
            </tr>
        </table>

    </div>

    <br>
    <button type="button" onclick="history.back()">Back</button>

</body>

</html>