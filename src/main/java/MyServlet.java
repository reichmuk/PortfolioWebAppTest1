import domain.Calculations;
import domain.Control;
import domain.YahooApi;
import persistance.SqlTable;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

@WebServlet(name = "MyServlet", value = "/MyServlet")
public class MyServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter writer = response.getWriter()) {
            writer.println("<!DOCTYPE html><html>");
            writer.println("<head>");
            writer.println("<meta charset=\"UTF-8\" />");
            writer.println("<title>MyServlet.java:doGet(): Servlet code!</title>");
            writer.println("</head>");
            writer.println("<body>");

            writer.println("<h1>Portfolio Web App Initialized!</h1>");

            writer.println("</body>");
            writer.println("</html>");
        }

        try {
            Control control = new Control();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }



        /*try{


            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://127.0.0.1:3306/instrumentDB?useSSL=false&allowPublicKeyRetrieval=true";
            String user = "root";
            String password = "Blue_22!";
            Connection conn = DriverManager.getConnection(url,user,password);
            Statement statement = conn.createStatement();
            statement.execute("INSERT INTO instruments VALUES ('del', 'CHF', 'Delia', 'Switzerland')");
            System.out.println("Connection created");
            conn.close();

        }

        catch (ClassNotFoundException e){
            e.printStackTrace();
            System.out.println("Fehler1");
            System.out.println(e.getMessage());
        }
        catch (SQLException e){
            e.printStackTrace();
            System.out.println("Fehler2");
            System.out.println("SQLException:" + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
        }*/

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            Control control = new Control();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        SqlTable sqlTable = Control.getSqlTable();
        Calculations calculations = Control.getCalculations();
        YahooApi yahooApi = Control.getYahooApi();

        ArrayList<String> instrumentList = new ArrayList<String>();
        ArrayList<Float> quantityList = new ArrayList<Float>();
        float totalQuantity = 0;

        for(int i =1; i<4;i++){
            String instrument = "instrument"+i;
            String instrumentValue = request.getParameter(instrument).toString();
            String quantity = "quantity"+i;
            String quantityValue = request.getParameter(quantity).toString();

            if(instrumentValue.equals("Select Instrument")){
            }else{
                instrumentList.add(instrumentValue);
                quantityList.add(Float.parseFloat(quantityValue));
            }
        }

        for(float value : quantityList){
            totalQuantity = totalQuantity+value;
        }


        for(int i = 0; i<instrumentList.size(); i++){
            String instrument = instrumentList.get(i);
            float quantity = quantityList.get(i);
            float weight = quantity/totalQuantity;
            String ticker = sqlTable.getInstrumentTicker(instrument);
            sqlTable.insertPortfolio(ticker,"current", weight);
            yahooApi.priceImport(ticker);
            calculations.calcSingleReturn(ticker);
            calculations.calcMetricSummary(ticker);
        }

        calculations.calcPortfolioReturn("current");
        calculations.calcCorrelations("current");



        /*
        String instrument1 = request.getParameter("instrument1").toString();
        String instrument2 = request.getParameter("instrument2").toString();
        String instrument3 = request.getParameter("instrument3").toString();
        String quantity1 = request.getParameter("quantity1").toString();
        String quantity2 = request.getParameter("quantity2").toString();
        String quantity3 = request.getParameter("quantity3").toString();

        String tickerInstrument1 = sqlTable.getInstrumentTicker(instrument1);
        String tickerInstrument2 = sqlTable.getInstrumentTicker(instrument2);
        String tickerInstrument3 = sqlTable.getInstrumentTicker(instrument3);

        sqlTable.insertPortfolio(tickerInstrument1,"current", 0.4f);
        sqlTable.insertPortfolio(tickerInstrument2,"current", 0.35f);
        sqlTable.insertPortfolio(tickerInstrument3,"current",0.25f);

        YahooApi yahooApi = Control.getYahooApi();
        yahooApi.priceImport(tickerInstrument1);
        yahooApi.priceImport(tickerInstrument2);
        yahooApi.priceImport(tickerInstrument3);

        calculations.calcSingleReturn(tickerInstrument1);
        calculations.calcSingleReturn(tickerInstrument2);
        calculations.calcSingleReturn(tickerInstrument3);
        calculations.calcMetricSummary(tickerInstrument1);
        calculations.calcMetricSummary(tickerInstrument2);
        calculations.calcMetricSummary(tickerInstrument3);

        calculations.calcPortfolioReturn("current");
        calculations.calcCorrelations("current");
         */

        /*
        PrintWriter out = response.getWriter();
        out.println(instrument1);
        out.println(instrument2);
        out.println(instrument3);
         */
    }
}
