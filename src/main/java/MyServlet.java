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
        ArrayList<Integer> quantityList = new ArrayList<Integer>();
        double totalQuantity = 0;

        for(int i =1; i<4;i++){
            String instrument = "instrument"+i;
            String instrumentValue = request.getParameter(instrument).toString();
            String quantity = "quantity"+i;
            String quantityValue = request.getParameter(quantity).toString();

            if(instrumentValue.equals("Select Instrument")){
            }else{
                instrumentList.add(instrumentValue);
                quantityList.add(Integer.parseInt(quantityValue));
            }
        }

        for(double value : quantityList){
            totalQuantity = totalQuantity+value;
        }


        for(int i = 0; i<instrumentList.size(); i++){
            String instrument = instrumentList.get(i);
            int quantity = quantityList.get(i);
            double weight = quantity/totalQuantity;
            String ticker = sqlTable.getInstrumentTicker(instrument);
            sqlTable.insertPortfolio(ticker,"current", weight);
            yahooApi.priceImport(ticker);
            calculations.calcSingleReturn(ticker);
            calculations.calcMetricSummary(ticker);
        }

        calculations.calcPortfolioReturn("current");
        calculations.calcCorrelations("current");
        calculations.calcPortfolioVolatility("current");
        calculations.calcOptimalPortfolio("current",0);

    }
}
