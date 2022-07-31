<%--
  Created by IntelliJ IDEA.
  User: kevin.reichmuth
  Date: 23.07.22
  Time: 7:18 PM
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
    <p>Hier erhalten Sie mehr Informationen zur Anwendung und Funktionsweise des Portfolio Analyse Tool.</p>
</head>
<body>

    <div id="div_portfolio">
        <h3>Funktionsweise:</h3>
        <p>Die Anwendung hat zwei wesentliche Aufgaben:
        <p>
            Erstens ein bestehendes Portfolio analysieren zu können (Portfoliorendite, Portfoliovolatilität) und
            zweitens eine Portfolio-Optimierung nach Markowitz berechnen zu können (Efficient Frontier, Minimum-Risk-Portfolio).
        </p>

        <p>
            1952 veröffentlichte Harry Markowitz seine Portfoliotheorie in einem Artikel im «Journal of Finance».
            Seither wurde dieser Artikel und dessen Theorie weit verbreitet und viel-fach referenziert.
            Für diese Portfoliotheorie wurde Markowitz der Nobelpreis verliehen. Auch heutzutage hat die Portfoliotheorie
            in der Finanzwelt und auch an den Universitä-ten einen hohen Stellenwert.
        </p>
        <p>
            Für die Umsetzung der Applikation wurden folgende Dienste verwendet: 1) Front-End: JSP, CSS 2) Business-Logik: Servlets, JAVA 3) Backend: MySQL
        </p>
        <br>
        <h3>Anwendung:</h3>
        <p>
            Die Webanwendung besteht aus zwei Seiten, einer Startseite und einer Resultatseite.
            Auf der Startseite kann der Benutzer sein Musterportfolio (Titel und Stückzahl) erfassen.
            Die verfügbaren Wertpapiere "statischen Daten" sind auf Aktien der Teilnehmer der Schweizer Indizes (SMI & SPI) beschränkt.
            Der Benutzer hat die Möglichkeit zwischen zwei verschiedenen Berechnungsmetho-den. Es stehen die Optionen «Min-Risk-Portfolio»
            und «Optimales Portfolio» zur Ver-fügung. Bei der Variante «Optimales Portfolio» benötigt es zudem die Definition einer Zielrendite.
            Mit der Betätigung des Submit-Buttons werden die Berechnungengestartet und auf der Resultat-Seite angezeigt.
            Die Resultat-Seite ist unterteilt in eine Übersicht zum Aktuellen Portfolio, den notwendigen Anpassungen pro Instrument und einer
            übersicht zum Optialen Portfolio.
            Mit der Betätigung auf den Back Button gelangt man zrück auf die Start-Seite.

            Bei Min-Risk keine Zielrendite
            Bei Efficient-Frontier braucht zierendite sonst wird mit 0 gerechnet
            nur postivie zahlen bei Rendite und Anzahl
            Mindestens zwei Instrumente wählen
            nicht zweimal das gleiche

        </p>
        <br>
    </div>

    <br>
    <button type="button" onclick="history.back()">Back</button>



</body>
</html>
