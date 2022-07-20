package viewmodel;
import domain.Calculations;
import domain.Control;
import domain.YahooApi;
import persistance.SqlTable;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.ArrayList;

@WebServlet(name = "MainServlet", value = "/MainServlet")
public class MainServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //Initiate control and reset DB
        try {
            Control control = new Control();
            SqlTable sqlTable = Control.getSqlTable();
            sqlTable.resetTable("prices");
            sqlTable.resetTable("metrics");
            sqlTable.resetTable("metrics_summary");
            sqlTable.resetTable("portfolio");
            System.out.println("RESET hat funktioniert");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        SqlTable sqlTable = Control.getSqlTable();
        Calculations calculations = Control.getCalculations();
        YahooApi yahooApi = Control.getYahooApi();

        ArrayList<String> instrumentList = new ArrayList<String>();
        ArrayList<Integer> quantityList = new ArrayList<Integer>();
        double portfolioValue = 0;

        //Save selected instruments in instrumentList
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

        //Import and store prices of all selected instruments
        for(int i = 0; i<instrumentList.size(); i++){
            String instrument = instrumentList.get(i);
            String ticker = sqlTable.getInstrumentTicker(instrument);
            yahooApi.priceImport(ticker);
            int quantity = quantityList.get(i);
            portfolioValue = portfolioValue+(quantity*sqlTable.getLatestPrice(ticker));
        }

        //Add all instruments to portfolio with quantity and weight & Calc and store singleReturns and summaryMetrics for each instrument
        for(int i = 0; i<instrumentList.size(); i++){
            String instrument = instrumentList.get(i);
            String ticker = sqlTable.getInstrumentTicker(instrument);
            int quantity = quantityList.get(i);
            double weight = (quantity*sqlTable.getLatestPrice(ticker))/portfolioValue;
            sqlTable.insertPortfolio(ticker,"current",quantity,weight);
            calculations.calcSingleReturn(ticker);
            calculations.calcMetricSummary(ticker);
        }

        //Process al portfolio calculations
        calculations.calcPortfolioValue("current");
        calculations.calcPortfolioReturn("current");
        calculations.calcCorrelations("current");
        calculations.calcPortfolioVolatility("current");
        calculations.calcOptimalPortfolio("current",0);

        //Direct to result page
        response.sendRedirect("result.jsp");

    }
}