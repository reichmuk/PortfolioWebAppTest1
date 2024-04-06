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
    <b>Willkommen im Portfolio Analyse Tool!</b>
    <br>
    <p>Bitte selektieren Sie ihre Titel und erfassen die dazugehörige Anzahl "QTY" im Bereich Musterportfolio.</p>

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
        }

    %>

    <script>
        // Sample instrument list and ccyList
        var instrumentList = <%= new Gson().toJson(instrumentList) %>;
        var ccyList = <%= new Gson().toJson(ccyList)%>;
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


    <!Portfolio Eingabe>
    <div>
        <h3>Portfolio Eingabe:</h3>

        <form action="MainServlet" method="post">
            <table id="table_portfolio_input">
                <!Column name>
                <tr>
                    <th>Titel</th>
                    <th>Anzahl</th>
                    <th>CCY</th>
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
                        <select name="instrument<%=i%>" class="operator" onchange="updateCcy(<%=i%>)">
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
                        <input type="text" name="quantity<%=i%>" class="input" size="10" pattern="[0-9]+">
                    </td>
                    <td>
                        <input type="text" name="Ccy<%=i%>" class="input_data" value="" readonly>
                    </td>


                    <script>

                        function updateCcy(i){
                            var selectElement = document.querySelector('select[name="instrument' + i + '"]');
                            var selectedInstrument = selectElement.value;
                            var position = instrumentList.indexOf(selectedInstrument);
                            var instrumentCcy = ccyList[position];
                            var ccyNr = 'Ccy'+i;
                            document.querySelector('input[name="' + ccyNr + '"]').value = instrumentCcy;
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

</body>

</html>