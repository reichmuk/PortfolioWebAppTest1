<%--
  Created by IntelliJ IDEA.
  User: kevin.reichmuth
  Date: 31.08.2022
  Time: 08:00 AM
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
    <b>Tool-Informationen:</b>
    <br>
    <p>Hier erhalten Sie mehr Informationen zur Anwendung und Funktionsweise des Portfolio-Analyse-Tools.</p>
</head>
<body>

    <div id="div_portfolio">
        <h3>Funktionsweise:</h3>
        <p>Die Anwendung hat zwei wesentliche Aufgaben:
        <p>
            Erstens, ein bestehendes Portfolio analysieren zu können (Portfoliorendite, Portfoliovolatilität) und
            zweitens, eine Portfolio-Optimierung nach Markowitz berechnen zu können (Efficient Frontier- und Minimum-Risk-Portfolio).
        </p>

        <p>
            1952 veröffentlichte Harry Markowitz seine Portfoliotheorie in einem Artikel im «Journal of Finance».
            Seither wurde dieser Artikel und dessen Theorie weit verbreitet und vielfach referenziert.
            Für diese Portfoliotheorie wurde Markowitz der Nobelpreis verliehen. Auch heutzutage hat die Portfoliotheorie
            in der Finanzwelt und auch an den Universitäten einen hohen Stellenwert.
        </p>
        <p>
            Für die Umsetzung der Applikation wurden folgende Dienste verwendet: 1) Front-End: JSP, HTML, CSS 2) Business-Logik: Servlets, JAVA 3) Backend: MySQL
        </p>
        <br>
        <h3>Anwendung:</h3>
        <p>
            Die Webanwendung besteht aus drei Seiten, einer Start-Seite, einer Info-Seite und einer Resultat-Seite.
            Auf der Startseite kann der Benutzer sein Musterportfolio (Titel und Stückzahl) erfassen.
            Die verfügbaren Wertpapiere "statische Daten" sind auf Aktien der Teilnehmer der Schweizer Indizes (SMI) beschränkt.
            Der Benutzer hat die Möglichkeit, zwischen zwei verschiedenen Berechnungsmethoden. Es stehen die Optionen «Min-Risk-Portfolio»
            und «Efficient Frontier (target return)» zur Verfügung. Bei der Variante «Efficient Frontier (target return)» benötigt es zudem die Wahl
            einer Zielrendite. Mit der Betätigung des Submit-Buttons werden die Berechnungen gestartet und anschliessend auf der Resultat-Seite angezeigt.
            Die Resultat-Seite ist unterteilt in eine Übersicht zum aktuellen Portfolio, den notwendigen Anpassungen pro Instrument und einer
            Übersicht zum optialen Portfolio. Mit der Betätigung des "Back" Button gelangt man zurück auf die Start-Seite.
        </p>
        <br>
        <b>
            Eingabe-Hilfe & Validierung:
        </b>
        <p>
            - Titel: Es müssen mindestens zwei Titel selektiert werden. Titel-Duplikate sind nicht erlaubt.
        </p>
        <p>
            - Anzahl: Es werden nur positive Ganzzahlen aktzeptiert.
        </p>
        <p>
            - Min-Risk-Portfolio: Eine Zielrendite wird nicht benötigt.
        </p>
        <p>
            - Efficient Frontier (target return): Eine Zielrendite ist zwingend notwendig.
        </p>
    </div>

    <br>
    <button type="button" onclick="history.back()">Back</button>



</body>
</html>
