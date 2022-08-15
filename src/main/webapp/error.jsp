<%--
  Created by IntelliJ IDEA.
  User: kevin.reichmuth
  Date: 13.08.22
  Time: 10:42 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="styles.css">
    <title>Portfolio Web Application</title>
    <h1 style="display: inline-block" >Portfolio</h1>
    <h2 style="display: inline-block" >Analyse Tool</h2>
    <br>
    <b>Die Eingabe-Validierung war nicht erfolgreich!</b>
    <br>
    <p>Übericht Eingabe-Validierung:</p>

</head>
<body>
    <%
        String instrumentValidation = request.getParameter("instrument");
        String quantityValidation = request.getParameter("quantity");
        String strategyValidation = request.getParameter("strategy");
        String targetReturnValidation = request.getParameter("return");
    %>

    <table id="table_validation">

        <!Column name>
        <tr>
            <th>Validierung</th>
            <th>Resultat</th>
        </tr>
        <tr>
            <td class="input">Instrument-Validierung:</td>
            <td class="input <%=instrumentValidation.equals("false") ? "negative" : "" %>"><%=instrumentValidation%></td>
        </tr>
        <tr>
            <td class="input">Stückzahl-Validierung:</td>
            <td class="input <%=quantityValidation.equals("false") ? "negative" : ""%>"><%=quantityValidation%></td>
        </tr>
        <tr>
            <td class="input">Strategie-Validierung:</td>
            <td class="input <%=strategyValidation.equals("false") ? "negative" : ""%>"><%=strategyValidation%></td>
        </tr>
        <tr>
            <td class="input">Zielrendite-Validierung:</td>
            <td class="input <%=targetReturnValidation.equals("false") ? "negative" : ""%>"><%=targetReturnValidation%></td>
        </tr>
    </table>

    <br>
    <b>
        Eingabe-Hilfe & Validierung:
    </b>
    <p>
        - Instrument-Validierung: Es müssen mindestens zwei Titel selektiert werden. Titel-Duplikate sind nicht erlaubt.
        <br>
        - Stückzahl-Validierug: Es werden nur positive Ganzzahlen aktzeptiert.
        <br>
        - Strategie-Validierung: Es muss mindestens eine Strategie selektiert werden.
        <br>
        - Efficient Frontier (target return): Eine Zielrendite ist zwingend notwendig.
    </p>
    
    <br>
    <br>
    <br>
    <button type="button" onclick="history.back()">Back</button>


</body>
</html>
