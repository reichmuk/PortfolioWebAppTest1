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
<%@ page import="domain.Control" %>
<%@ page import="domain.Calculations" %>
<%@ page import="java.text.DecimalFormat" %>

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

        function topercentage(){

        }

    </script>

</head>

<body>

    <!-- Get tickerList and tickerListSize -->
    <%
        //Variables
        SqlTable sqlTable = Control.getSqlTable();
        Calculations calculations = Control.getCalculations();
        ArrayList<String> tickerList = sqlTable.getPortfolioTickers("current");
        String strategy = calculations.getStrategy();
        int tickerListSize = tickerList.size();
        DecimalFormat df2 = new DecimalFormat("#.00");
    %>

    <div id="div_portfolio">

        <h3>Übersicht:</h3>

        <table class="table_summary_values">

            <%
                int intCurrentPortfolioValue = (int) sqlTable.getMetricSummaryValue("PORTFOLIO","currentPortfolioValue");
                String currentPortfolioValue = String.format("%,d",intCurrentPortfolioValue);
                DecimalFormat df = new DecimalFormat("#.000000");
                String currentPortfolioReturn = df.format(sqlTable.getMetricSummaryValue("PORTFOLIO","currentPortfolioReturn")*100)+"%";
                String currentPortfolioVolatility = df.format(sqlTable.getMetricSummaryValue("PORTFOLIO","currentPortfolioVolatility")*100)+"%";
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
                <th>Weight %</th>
                <th>Opt. QTY</th>
                <th>Opt. Weight %</th>
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
                    String weight01 = "";
                    int optQty01 = 0;
                    String optWeight01 = "";
                    int trade01 = 0;

                    if(tickerListSize>0){
                        ticker01 = tickerList.get(0);
                        instrument01 = sqlTable.getInstrumentData("name","ticker",ticker01);
                        qty01 = sqlTable.getPortfolioQuantity(ticker01,"current");
                        ccy01 = sqlTable.getInstrumentData("ccy","ticker",ticker01);
                        price01 = sqlTable.getLatestPrice(ticker01);
                        weight01 = df2.format(sqlTable.getPortfolioWeight(ticker01,"current")*100)+"%";
                        // UPDATE minRisk or targetReturn
                        if(strategy.equals("targetReturn")){
                            optQty01 = sqlTable.getPortfolioQuantity(ticker01,"targetReturn");
                            optWeight01 = df2.format(sqlTable.getPortfolioWeight(ticker01,"targetReturn")*100)+"%";
                            trade01 = optQty01-qty01;
                        }

                        if(strategy.equals("minRisk")){
                            optQty01 = sqlTable.getPortfolioQuantity(ticker01,"minRisk");
                            optWeight01 = df2.format(sqlTable.getPortfolioWeight(ticker01,"minRisk")*100)+"%";
                            trade01 = optQty01-qty01;
                        }
                    }
                %>

                <td>
                    <input type="text" name="instrument01" class="input_data_instrument" size="10" value="<%=instrument01%>" readonly>
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

            <%
                int intOptimalPortfolioValue = 0;
                String optimalPortfolioReturn = "";
                String optimalPortfolioVolatility = "";

                if(strategy.equals("targetReturn")){
                    intOptimalPortfolioValue = (int) sqlTable.getMetricSummaryValue("PORTFOLIO","targetReturnPortfolioValue");
                    optimalPortfolioReturn = df.format(sqlTable.getMetricSummaryValue("PORTFOLIO","targetReturnPortfolioReturn")*100)+"%";
                    optimalPortfolioVolatility = df.format(sqlTable.getMetricSummaryValue("PORTFOLIO","targetReturnPortfolioVolatility")*100)+"%";
                }

                if(strategy.equals("minRisk")){
                    intOptimalPortfolioValue = (int) sqlTable.getMetricSummaryValue("PORTFOLIO","minRiskPortfolioValue");
                    optimalPortfolioReturn = df.format(sqlTable.getMetricSummaryValue("PORTFOLIO","minRiskPortfolioReturn")*100)+"%";
                    optimalPortfolioVolatility = df.format(sqlTable.getMetricSummaryValue("PORTFOLIO","minRiskPortfolioVolatility")*100)+"%";
                }
                String optimalPortfolioValue = String.format("%,d",intOptimalPortfolioValue);

            %>

            <tr>
                <td><label for="portfolioWertOptimal">Portfoliowert CHF (optimal): </label></td>
                <td><input type="text" id="portfolioWertOptimal" class="input_data" value=<%=optimalPortfolioValue%> readonly></td>
            </tr>
            <tr>
                <td><label for="portfolioRenditeOptimal">Portfoliorendite in % (optimal): </label></td>
                <td><input type="text" id="portfolioRenditeOptimal" class="input_data" pattern="[0-9]+" value=<%=optimalPortfolioReturn%> readonly></td>
            </tr>
            <tr>
                <td><label for="portfolioVolatilitaetOptimal">Portfoliovolatilität in % (optimal): </label></td>
                <td><input type="text" id="portfolioVolatilitaetOptimal" class="input_data" pattern="[0-9]+" value=<%=optimalPortfolioVolatility%> readonly></td>
            </tr>
        </table>

    </div>

    <br>
    <button type="button" onclick="history.back()">Back</button>

</body>

</html>