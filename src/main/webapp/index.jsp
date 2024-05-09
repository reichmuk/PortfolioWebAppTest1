<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.Statement" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.DriverManager" %>
<%@ page import="domain.Calculations" %>
<%@ page import="domain.Control" %>
<%@ page import="constants.Constants" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.google.gson.Gson" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%--
  Created by IntelliJ IDEA.
  User: kevin.reichmuth
  Date: 31.08.2022
  Time: 08:00 AM
--%>


<html>
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="styles.css">
    <link href="https://cdn.jsdelivr.net/npm/select2@4.0.13/dist/css/select2.min.css" rel="stylesheet" />
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/select2@4.0.13/dist/js/select2.min.js"></script>

    <title>Portfolio Web Application</title>

    <h1 style="display: inline-block" >Portfolio</h1>
    <h2 style="display: inline-block" >Analyse Tool</h2>
    <br>


    <script>
        function inputValidation(){
            for(i=1; i<2; i++){
                var id = "qty"+i
                var element = document.getElementById(id).innerHTML;
                alert(element);
                return false;
            }
        }
    </script>

</head>

<body>
    <%
        //Variables
        ResultSet rs = null;
        Statement stm = null;
        String query = null;
        ArrayList<String> instrumentList = new ArrayList<>();
        ArrayList<String> ccyList= new ArrayList<>();
        ArrayList<String> tickerList = new ArrayList<>();

        //Connection to DB
        try {
            query = "select * from instruments";
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = Constants.mysqlUrl;
            String password = "BlueBlueBlue22";
            String user = "root";

            Connection conn = DriverManager.getConnection(url, user, password);
            stm = conn.createStatement();
            rs = stm.executeQuery(query);
        }catch (Exception ex){
            ex.printStackTrace();
            out.println("Error "+ex.getMessage());
        }

        rs = stm.executeQuery(query);

        //Add all instruments to instrumentList
        while (rs.next()) {
            instrumentList.add(rs.getString("name"));
            ccyList.add(rs.getString("ccy"));
            tickerList.add(rs.getString("ticker"));
        }

    %>

    <script>
        // Sample instrument list and ccyList
        var instrumentList = <%= new Gson().toJson(instrumentList) %>;
        var ccyList = <%= new Gson().toJson(ccyList)%>;
        var tickerList = <%= new Gson().toJson(tickerList)%>;
        var yhFinanceKey = <%= new Gson().toJson(Constants.YHFINANCEKEY)%>
    </script>





    <!Portfolio Eingabe>
    <div id="div_input">
        <h3>Portfolio Eingabe:</h3>

        <table class="table_summary_values">
            <tr>
                <td><label>Portfoliowert CHF (aktuell):</label></td>
                <td><input type="text" name="portfolioValue" class="data" value="" readonly></td>
            </tr>
        </table>
        <br>

        <form action="MainServlet" method="post">
            <table id="table_portfolio_input">
                <!Column name>
                <tr>
                    <th>Titel</th>
                    <th>Anzahl</th>
                    <th>Währung</th>
                    <th>Preis</th>
                    <th>% Change</th>
                    <th>Value</th>
                    <th>Weight %</th>

                </tr>

                <!input counter>
                <%
                    // Retrieve the counter from the session
                    Integer counter = (Integer) session.getAttribute("counter");

                    // If the counter is not in the session, initialize it to 5
                    if (counter == null) {
                        counter = 5;
                        session.setAttribute("counter", counter);
                    }

                    String action = request.getParameter("action");

                    // Check if the action parameter is set and update the counter accordingly
                    if (action != null && action.equals("increment")) {
                        counter++;
                        session.setAttribute("counter", counter);
                    }

                    for(int i = 0; i<counter;i++){
                %>

                <!Row>
                <tr>
                    <td>
                        <select name="instrument<%=i%>" class="operator" onchange="updateInstrument(<%=i%>)">
                            <option>Select Instrument</option>
                            <%
                                rs = stm.executeQuery(query);
                                while (rs.next()){
                            %>
                                <option><%=rs.getString("name")%></option>
                            <%
                                }
                            %>
                        </select>
                    </td>

                    <td>
                        <input type="text" name="quantity<%=i%>" class="input" size="10" pattern="[0-9]+" onchange="updateWeight(<%=i%>)">
                    </td>

                    <td>
                        <input type="text" name="ccy<%=i%>" class="data" value="" readonly>
                    </td>

                    <td>
                        <input type="text" name="price<%=i%>" class="data" value="" readonly>
                    </td>

                    <td>
                        <input type="text" name="change<%=i%>" class="data" value="" readonly>
                    </td>

                    <td>
                        <input type="text" name="value<%=i%>" class="data" value="" readonly>
                    </td>

                    <td>
                        <input type="text" name="weight<%=i%>" class="data" value="" readonly>
                    </td>


                    <script>
                        function updateInstrument(i){
                            //update CCY
                            var selectElement = document.querySelector('select[name="instrument' + i + '"]');
                            var selectedInstrument = selectElement.value;
                            var position = instrumentList.indexOf(selectedInstrument);
                            var instrumentCcy = ccyList[position];
                            var ccyNr = 'ccy'+i;
                            document.querySelector('input[name="' + ccyNr + '"]').value = instrumentCcy;

                            //update price
                            var priceNr = 'price'+i;
                            var changeNr = 'change'+i;
                            var instrumentTicker = tickerList[position]

                            $(document).ready(function() {
                                var instrumentPrice;
                                var url = 'https://yfapi.net/v6/finance/quote?region=US&lang=en&symbols='+instrumentTicker;
                                $.ajax({
                                    url: url,
                                    type: "GET",
                                    dataType: "json",
                                    headers: {
                                        'accept': 'application/json',
                                        'X-API-KEY': 'FsZKqJFd6Z2Usj0TUTTGqdrtmEIRXd191njIimD8'
                                    },
                                    success: function(response) {
                                        var instrumentPrice = response.quoteResponse.result[0].regularMarketPrice;
                                        var priceChange = response.quoteResponse.result[0].regularMarketChangePercent;
                                        priceChange = priceChange.toFixed(2);
                                        if (priceChange < 0) {
                                            document.querySelector('input[name="change' + i + '"]').classList.add('negative');
                                        }
                                        priceChange += "%"
                                        document.querySelector('input[name="' + priceNr + '"]').value = instrumentPrice;
                                        document.querySelector('input[name="' + changeNr + '"]').value = priceChange;
                                    },
                                    error: function(xhr, status, error) {
                                        console.error("Error fetching stock price:", error);
                                    }
                                });
                            });

                            var quantity = parseFloat(document.querySelector('input[name="quantity' + i + '"]').value);
                            if(!isNaN(quantity)){
                                console.log("quantity is non NaN");
                                updateWeight(i);
                            }
                        }

                        function updateWeight(instrumentNumber){
                            var portfolioValue = 0;

                            setTimeout(function(){
                                for (var i = 0; i <= instrumentNumber; i++) {
                                    var quantity = parseFloat(document.querySelector('input[name="quantity' + i + '"]').value);
                                    var price = parseFloat(document.querySelector('input[name="price' + i + '"]').value);

                                    if(!isNaN(price)){
                                        var instrumentValue = parseFloat((quantity * price).toFixed(2));
                                        portfolioValue += instrumentValue;
                                        document.querySelector('input[name="value' + i + '"]').value = instrumentValue;
                                    }
                                }

                                for (var i = 0; i <= instrumentNumber; i++) {
                                    var quantity = parseFloat(document.querySelector('input[name="quantity' + i + '"]').value);
                                    var price = parseFloat(document.querySelector('input[name="price' + i + '"]').value);

                                    if(!isNaN(price)){
                                        var instrumentValue = quantity * price;
                                        var weight = (instrumentValue/portfolioValue*100).toFixed(2)+"%";
                                        document.querySelector('input[name="weight' + i + '"]').value = weight;
                                    }
                                }
                                document.querySelector('input[name="portfolioValue"]').value = portfolioValue;
                            },1000);
                        }
                    </script>

                </tr>
                <%
                    }
                %>
            </table>

            <label for="input_counter">Anzahl Instrumente:</label>
            <input type="text" id="input_counter" name="counter" value="<%=counter%>" readonly>

            <br>
            <br>

            <div class="container">
                <div class="sub-div">
                    <h4>Zeitintervall historische Kurse:</h4>
                    <input type="radio" id="3moTime" name="timeRange" class="radio" value="3mo" onchange="timeRadioValue(this.value)" checked>
                    <label for="3moTime">3 Monate</label><br>
                    <input type="radio" id="6moTime" name="timeRange" class="radio" value="6mo" onchange="timeRadioValue(this.value)">
                    <label for="6moTime">6 Monate</label><br>
                    <input type="radio" id="1yrTime" name="timeRange" class="radio" value="1y" onchange="timeRadioValue(this.value)">
                    <label for="1yrTime">1 Jahr</label><br>
                    <br>
                    <label>Ausgewählter Zeitintervall:</label>
                    <input type="text" id="input_time" name="time" value="3mo" readonly>
                </div>

                <div class="sub-div">
                    <h4>Optimales Portfolio:</h4>
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
                </div>

            </div>

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

            <br>
            <br>
            <button type="submit">Submit</button>
            <br>
        </form>

        <form action="ResetServlet" method="get">
            <button type="submit">Reset</button>
        </form>

        <form action="info.jsp">
            <input name="infoButton" type="image" src="info_button.png" width="5%" height="2.5%">
            <br>
            <label>(info)</label>
        </form>

    </div>



    <!-- Create a button that sends a request to increment the counter -->
    <form method="get" action="<%= request.getRequestURI() %>">
    <input type="hidden" name="action" value="increment">
    <input type="submit" value="Instrument hinzufügen">
    </form>

    <script>
        $(document).ready(function () {
            $("select").select2();
        });
    </script>



    <!-- File Upload -->
    <form>
        <label for="file"><b>File upload:</b></label>
        <br>
        <br>
        <input id="file" type="file" />
        <button type="button" onclick="processFile()">Upload</button>
    </form>

    <script>
        function processFile() {
            const fileInput = document.getElementById('file');
            const file = fileInput.files[0];

            if (file) {
                const reader = new FileReader();

                reader.onload = function (e) {
                    const content = e.target.result;
                    const rows = content.split('\n').map(row => row.trim());

                    const tickerList = [];
                    const qtyList = [];

                    for (let i = 1; i < rows.length; i++) { // Skip the header row
                        const [ticker, qty] = rows[i].split(';').map(data => data.trim());
                        tickerList.push(ticker);
                        qtyList.push(parseInt(qty));
                    }

                    // Display or further process the lists as needed
                    console.log('Ticker List:', tickerList);
                    console.log('Qty List:', qtyList);

                    // Redirect to upload.jsp
                    window.location.href = 'upload.jsp?tickerList=' + encodeURIComponent(JSON.stringify(tickerList)) + '&qtyList=' + encodeURIComponent(JSON.stringify(qtyList));

                };

                reader.readAsText(file);
            } else {
                alert('Please select a file before uploading.');
            }
        }
    </script>

</body>

</html>