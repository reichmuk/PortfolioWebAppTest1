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
    <link rel="stylesheet" href="styles.css">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Upload JSP Page</title>
</head>
<body>

    <h1 style="display: inline-block" >Uploaded</h1>
    <h2 style="display: inline-block" >Data</h2>

    <div>
        <form action="MainServlet" method="post">
            <table id="table_portfolio_input">
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
                        <input name="instrument<%=i%>" type="text" value="<%= tickerList[i] %>" readonly>
                    </td>
                    <td>
                        <input name="quantity<%=i%>" type="text" value="<%= qtyList[i] %>" readonly>
                    </td>
                </tr>
                <%
                    }
                %>
            </table>

            <label for="input_counter">Anzahl Instrumente:</label>
            <input type="text" id="input_counter" name="counter" value="<%=tickerList.length%>" readonly>


            <h3>Zeitintervall historische Kurse:</h3>
            <input type="radio" id="3moTime" name="timeRange" class="radio" value="3mo" onchange="timeRadioValue(this.value)" checked>
            <label for="3moTime">3 Monate</label><br>
            <input type="radio" id="6moTime" name="timeRange" class="radio" value="6mo" onchange="timeRadioValue(this.value)">
            <label for="6moTime">6 Monate</label><br>
            <input type="radio" id="1yrTime" name="timeRange" class="radio" value="1y" onchange="timeRadioValue(this.value)">
            <label for="1yrTime">1 Jahr</label><br>
            <br>
            <label>Ausgewählter Zeitintervall:</label>
            <input type="text" id="input_time" name="time" value="3mo" readonly>

            <h3>Optimales Portfolio:</h3>

            <input type="radio" id="minRisk" name="calc_strat" class="radio" value="minRisk" onchange="displayRadioValue(this.value)">
            <label for="minRisk">Minimum Variance Portfolio</label><br>
            <input type="radio" id="targetReturn" name="calc_strat" class="radio" value="targetReturn" onchange="displayRadioValue(this.value)">
            <label for="targetReturn">Mean Variance Portfolio</label><br>
            <br>
            <label>Ausgewählte Strategie: </label>
            <input type="text" id="input_strategy" name="strategy" value="" readonly>
            <br>
            <br>
            <input type="range" min="0.1" max="20" value="10" step="0.1" class="slider" onchange="updateTextInput(this.value);">
            <br>
            <label for="input_targetReturn">Zielrendite in %: </label>
            <input type="text" id="input_targetReturn" name="targetReturn" value="" readonly>

            <script>
                function displayRadioValue(val){
                    document.getElementById('input_strategy').value=val;
                }

                function timeRadioValue(val){
                    document.getElementById('input_time').value=val;
                }

                function updateTextInput(val){
                    document.getElementById('input_targetReturn').value=val;
                }

            </script>
            <button type="submit">Submit</button>
            <br>
        </form>

    </div>


</body>
</html>
