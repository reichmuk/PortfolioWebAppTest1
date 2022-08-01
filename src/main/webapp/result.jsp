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
                <td><label for="portfolioValueCurrent">Portfoliowert CHF (aktuell): </label></td>
                <td><input type="text" id="portfolioValueCurrent" class="input_data" value=<%=currentPortfolioValue%> readonly></td>
            </tr>
            <tr>
                <td><label for="portfolioReturnCurrent">Portfoliorendite in % (aktuell): </label></td>
                <td><input type="text" id="portfolioReturnCurrent" class="input_data" pattern="[0-9]+" value=<%=currentPortfolioReturn%> readonly></td>
            </tr>
            <tr>
                <td><label for="portfolioVolatilityCurrent">Portfoliovolatilität in % (aktuell): </label></td>
                <td><input type="text" id="portfolioVolatilityCurrent" class="input_data" pattern="[0-9]+" value=<%=currentPortfolioVolatility%> readonly></td>
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

            <tr>
                <!-- Get tickerList and tickerListSize -->
                <%
                    String instrument01 = null;
                    String ticker01 = null;
                    int qty01 = 0;
                    String ccy01 = null;
                    double price01 = 0;
                    String weight01 = null;
                    int optQty01 = 0;
                    String optWeight01 = null;
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
                    <input type="text" name="instrument01" class="input_data_instrument" size="35" value="<%=instrument01%>" readonly>
                </td>
                <td>
                    <input type="text" name="ticker01" class="input_data" size="15" value=<%=ticker01%> readonly>
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
                    <input type="text" name="weight01" class="input_data" size="15" pattern="[0-9]+" value=<%=weight01%> readonly>
                </td>
                <td>
                    <input type="text" name="optQty01" class="input_data" size="10" pattern="[0-9]+" value=<%=optQty01%> readonly>
                </td>
                <td>
                    <input type="text" name="optWeight01" class="input_data" size="15" pattern="[0-9]+" value=<%=optWeight01%> readonly>
                </td>
                <td>
                    <input type="text" name="trade01" class="input_data" size="10" pattern="[0-9]+" value=<%=trade01%> readonly>
                </td>
            </tr>

            <tr>
                <!-- Get tickerList and tickerListSize -->
                <%
                    String instrument02 = null;
                    String ticker02 = null;
                    int qty02 = 0;
                    String ccy02 = null;
                    double price02 = 0;
                    String weight02 = null;
                    int optQty02 = 0;
                    String optWeight02 = null;
                    int trade02 = 0;

                    if(tickerListSize>1){
                        ticker02 = tickerList.get(1);
                        instrument02 = sqlTable.getInstrumentData("name","ticker",ticker02);
                        qty02 = sqlTable.getPortfolioQuantity(ticker02,"current");
                        ccy02 = sqlTable.getInstrumentData("ccy","ticker",ticker02);
                        price02 = sqlTable.getLatestPrice(ticker02);
                        weight02 = df2.format(sqlTable.getPortfolioWeight(ticker02,"current")*100)+"%";
                        // UPDATE minRisk or targetReturn
                        if(strategy.equals("targetReturn")){
                            optQty02 = sqlTable.getPortfolioQuantity(ticker02,"targetReturn");
                            optWeight02 = df2.format(sqlTable.getPortfolioWeight(ticker02,"targetReturn")*100)+"%";
                            trade02 = optQty02-qty02;
                        }

                        if(strategy.equals("minRisk")){
                            optQty02 = sqlTable.getPortfolioQuantity(ticker02,"minRisk");
                            optWeight02 = df2.format(sqlTable.getPortfolioWeight(ticker02,"minRisk")*100)+"%";
                            trade02 = optQty02-qty02;
                        }
                    }
                %>

                <td>
                    <input type="text" name="instrument02" class="input_data_instrument" size="35" value="<%=instrument02%>" readonly>
                </td>
                <td>
                    <input type="text" name="ticker02" class="input_data" size="15" value=<%=ticker02%> readonly>
                </td>
                <td>
                    <input type="text" name="qty02" class="input_data" size="10" pattern="[0-9]+" value=<%=qty02%> readonly>
                </td>
                <td>
                    <input type="text" name="ccy02" class="input_data" size="10" value=<%=ccy02%> readonly>
                </td>
                <td>
                    <input type="text" name="price02" class="input_data" size="10" value=<%=price02%> readonly>
                </td>
                <td>
                    <input type="text" name="weight02" class="input_data" size="15" pattern="[0-9]+" value=<%=weight02%> readonly>
                </td>
                <td>
                    <input type="text" name="optQty02" class="input_data" size="10" pattern="[0-9]+" value=<%=optQty02%> readonly>
                </td>
                <td>
                    <input type="text" name="optWeight02" class="input_data" size="15" pattern="[0-9]+" value=<%=optWeight02%> readonly>
                </td>
                <td>
                    <input type="text" name="trade02" class="input_data" size="10" pattern="[0-9]+" value=<%=trade02%> readonly>
                </td>
            </tr>

            <tr>
                <!-- Get tickerList and tickerListSize -->
                <%
                    String instrument03 = null;
                    String ticker03 = null;
                    int qty03 = 0;
                    String ccy03 = null;
                    double price03 = 0;
                    String weight03 = "";
                    int optQty03 = 0;
                    String optWeight03 = "";
                    int trade03 = 0;

                    if(tickerListSize>2){
                        ticker03 = tickerList.get(2);
                        instrument03 = sqlTable.getInstrumentData("name","ticker",ticker03);
                        qty03 = sqlTable.getPortfolioQuantity(ticker03,"current");
                        ccy03 = sqlTable.getInstrumentData("ccy","ticker",ticker03);
                        price03 = sqlTable.getLatestPrice(ticker03);
                        weight03 = df2.format(sqlTable.getPortfolioWeight(ticker03,"current")*100)+"%";
                        // UPDATE minRisk or targetReturn
                        if(strategy.equals("targetReturn")){
                            optQty03 = sqlTable.getPortfolioQuantity(ticker03,"targetReturn");
                            optWeight03 = df2.format(sqlTable.getPortfolioWeight(ticker03,"targetReturn")*100)+"%";
                            trade03 = optQty03-qty03;
                        }

                        if(strategy.equals("minRisk")){
                            optQty03 = sqlTable.getPortfolioQuantity(ticker03,"minRisk");
                            optWeight03 = df2.format(sqlTable.getPortfolioWeight(ticker03,"minRisk")*100)+"%";
                            trade03 = optQty03-qty03;
                        }
                    }
                %>

                <td>
                    <input type="text" name="instrument03" class="input_data_instrument" size="35" value="<%=instrument03%>" readonly>
                </td>
                <td>
                    <input type="text" name="ticker03" class="input_data" size="15" value=<%=ticker03%> readonly>
                </td>
                <td>
                    <input type="text" name="qty03" class="input_data" size="10" pattern="[0-9]+" value=<%=qty03%> readonly>
                </td>
                <td>
                    <input type="text" name="ccy03" class="input_data" size="10" value=<%=ccy03%> readonly>
                </td>
                <td>
                    <input type="text" name="price03" class="input_data" size="10" value=<%=price03%> readonly>
                </td>
                <td>
                    <input type="text" name="weight03" class="input_data" size="15" pattern="[0-9]+" value=<%=weight03%> readonly>
                </td>
                <td>
                    <input type="text" name="optQty03" class="input_data" size="10" pattern="[0-9]+" value=<%=optQty03%> readonly>
                </td>
                <td>
                    <input type="text" name="optWeight03" class="input_data" size="15" pattern="[0-9]+" value=<%=optWeight03%> readonly>
                </td>
                <td>
                    <input type="text" name="trade03" class="input_data" size="10" pattern="[0-9]+" value=<%=trade03%> readonly>
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
                <td><label for="portfolioValueOptimal">Portfoliowert CHF (optimal): </label></td>
                <td><input type="text" id="portfolioValueOptimal" class="input_data" value=<%=optimalPortfolioValue%> readonly></td>
            </tr>
            <tr>
                <td><label for="portfolioReturnOptimal">Portfoliorendite in % (optimal): </label></td>
                <td><input type="text" id="portfolioReturnOptimal" class="input_data" pattern="[0-9]+" value=<%=optimalPortfolioReturn%> readonly></td>
            </tr>
            <tr>
                <td><label for="portfolioVolatilityOptimal">Portfoliovolatilität in % (optimal): </label></td>
                <td><input type="text" id="portfolioVolatilityOptimal" class="input_data" pattern="[0-9]+" value=<%=optimalPortfolioVolatility%> readonly></td>
            </tr>
        </table>

    </div>

    <br>
    <button type="button" onclick="history.back()">Back</button>

</body>

</html>