<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.Statement" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.DriverManager" %>
<%@ page import="domain.Calculations" %>
<%@ page import="domain.Control" %><%--
  Created by IntelliJ IDEA.
  User: kevin.reichmuth
  Date: 31.08.2022
  Time: 08:00 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="styles.css">
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

    try {
        query = "select * from instruments";
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://127.0.0.1:3306/instrumentDB?useSSL=false&allowPublicKeyRetrieval=true";
        String user = "root";
        String password = "Blue_22!";
        Connection conn = DriverManager.getConnection(url, user, password);
        stm = conn.createStatement();
        rs = stm.executeQuery(query);
    }catch (Exception ex){
        ex.printStackTrace();
        out.println("Error "+ex.getMessage());
    }
%>



<!Portfolio Eingabe>
<div>
    <h3>Portfolio Eingabe:</h3>

    <form action="MainServlet" method="post">

        <table id="table_portfolio_input">

            <!Column name>
            <tr>
                <th>Titel</th>
                <th>Anzahl</th>
            </tr>

            <!Row 1>
            <tr>
                <td>
                    <select name="instrument1" class="input">
                        <option inst1="-1">Select Instrument</option>
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
                    <input type="text" name="quantity1" class="input" size="10" pattern="[0-9]+">
                </td>
            </tr>

            <!Row 2>
            <tr>
                <td>
                    <select name="instrument2" class="input">
                        <option inst1="-1">Select Instrument</option>
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
                    <input type="text" name="quantity2" class="input" size="10" pattern="[0-9]+">
                </td>
            </tr>

            <!Row 3>
            <tr>
                <td>
                    <select name="instrument3" class="input">
                        <option inst1="-1">Select Instrument</option>
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
                    <input type="text" name="quantity3" class="input" size="10" pattern="[0-9]+">
                </td>
            </tr>

        </table>

        <h3>Optimales Portfolio:</h3>

        <input type="radio" id="minRisk" name="calc_strat" class="radio" value="minRisk" onchange="displayRadioValue(this.value)">
        <label for="minRisk">Min-Risk-Portfolio</label><br>
        <input type="radio" id="targetReturn" name="calc_strat" class="radio" value="targetReturn" onchange="displayRadioValue(this.value)">
        <label for="targetReturn">Efficient Frontier (target return)</label><br>
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
        </script>

        <script>
            function updateTextInput(val){
                document.getElementById('input_targetReturn').value=val;
            }
        </script>

        <br>
        <br>

        <button type="submit">Submit</button>
        <br>
        <br>
    </form>

    <form action="ResetServlet" method="get">
        <button type="submit">Reset</button>
    </form>

    <br>

    <form action="info.jsp">
        <input name="infoButton" type="image" src="info_button.png" width="5%" height="2.5%">
        <br>
        <label>(info)</label>
    </form>

</div>

</body>

</html>