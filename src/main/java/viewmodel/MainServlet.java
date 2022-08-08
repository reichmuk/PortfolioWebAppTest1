package viewmodel;
import constants.Constants;
import domain.Calculations;
import domain.Control;
import domain.YahooApi;
import persistance.SqlTable;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;


/**
 * The MainServlet is launched with the submit-button on the index.jsp page.
 * The Servlet initiates the control-clas, does a DB-Reset, performs entry-validations, starts the YH-API price import,
 * Creates the "current" portfolio, does a timestamp cleanup, starts the calculations and forwards the user to the
 * Result-Page (result.jsp).
 * @author Kevin Reichmuth
 * @version 31.08.2022
 */

@WebServlet(name = "MainServlet", value = "/MainServlet")
public class MainServlet extends HttpServlet {

    /**
     * doGet Method is not used in this Servlet!
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    /**
     * doPost-Method performs the tasks mentioned in the description at the start of this Servlet.
     * @param request The Http-Servlet-Request
     * @param response The Http-Servlet-Response
     */
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
        double targetReturnCondition=0;

        //Get optimal portfolio parameter
        Boolean strategyValidation = true;
        Boolean targetReturnValidation = true;
        String strategy = request.getParameter("strategy").toString();
        String targetReturnString = request.getParameter(Constants.TARGETRETURN).toString();
        calculations.setStrategy(strategy);


        if(strategy.equals("")){
            strategyValidation=false;
        }

        if(strategy.equals(Constants.MINRISK)){
            targetReturnCondition = 0;
        }

        if(strategy.equals(Constants.TARGETRETURN)){
            if(targetReturnString ==""){
                targetReturnCondition = 0;
                targetReturnValidation = false;
            }
            else {
                targetReturnCondition = Double.parseDouble(targetReturnString)/100;
            }
        }

        //Save selected instruments in instrumentList, quantities in quantityList
        boolean quantityValidation = true;
        for(int i =1; i<4;i++){ //ACHTUNG LOOP noch anpassen!
            String instrument = "instrument"+i;
            String instrumentValue = request.getParameter(instrument).toString();
            String quantity = "quantity"+i;
            String quantityValue = request.getParameter(quantity).toString();
            if(quantityValue.equals("")){
                quantityValue="0";
            }

            if(instrumentValue.equals("Select Instrument")){
            }else{
                instrumentList.add(instrumentValue);
                quantityList.add(Integer.parseInt(quantityValue));
            }
        }

        //Perform quantity validation
        for(int quantity : quantityList){
            if(quantity==0){
                quantityValidation=false;
            }
        }

        //Perform instrument Validation (not empty and no duplicates)
        boolean instrumentValidation = true;
        if(instrumentList.size()==0){
            instrumentValidation=false;
        }
        HashSet<String> instrumentSet = new HashSet<String>(instrumentList);

        if(instrumentList.size()!=instrumentSet.size()){
            instrumentValidation=false;
        }



        //Validation
        if(strategyValidation&&targetReturnValidation&&quantityValidation&&instrumentValidation){

            //Import and store prices of all selected instruments
            for(int i = 0; i<instrumentList.size(); i++){
                String instrument = instrumentList.get(i);
                String ticker = sqlTable.getInstrumentTicker(instrument);
                yahooApi.priceImport(ticker);
                int quantity = quantityList.get(i);
                portfolioValue = portfolioValue+(quantity*sqlTable.getLatestPrice(ticker));
            }

            //Add all instruments to portfolio with quantity and weight
            for(int i = 0; i<instrumentList.size(); i++){
                String instrument = instrumentList.get(i);
                String ticker = sqlTable.getInstrumentTicker(instrument);
                int quantity = quantityList.get(i);
                double weight = (quantity*sqlTable.getLatestPrice(ticker))/portfolioValue;
                sqlTable.insertPortfolio(ticker,Constants.CURRENT,quantity,weight);
            }

            //Timestamp cleanup
            sqlTable.timeStampCleanup();


            //Process al portfolio calculations

            //Calc and store singleReturns and summaryMetrics for each instrument
            for(int i = 0; i<instrumentList.size(); i++){
                String instrument = instrumentList.get(i);
                String ticker = sqlTable.getInstrumentTicker(instrument);
                calculations.calcSingleReturn(ticker);
                calculations.calcMetricSummary(ticker);
            }

            calculations.calcPortfolioValue(Constants.CURRENT);
            calculations.calcPortfolioReturn(Constants.CURRENT);
            calculations.calcCorrelations(Constants.CURRENT);
            calculations.calcPortfolioVolatility(Constants.CURRENT);

            if(strategy.equals(Constants.MINRISK)){
                calculations.calcMinRiskPortfolio(strategy);
            }

            if(strategy.equals(Constants.TARGETRETURN)){
                calculations.calcOptimalPortfolio(strategy,targetReturnCondition);
            }

            calculations.calcPortfolioValue(strategy);
            calculations.calcPortfolioReturn(strategy);
            calculations.calcPortfolioVolatility(strategy);

            //Direct to result page
            response.sendRedirect("result.jsp");
        }

        System.out.println("Strategy-Validation: "+strategyValidation);
        System.out.println("Target-Return-Validation: "+targetReturnValidation);
        System.out.println("Quantity-Validation:" +quantityValidation);
        System.out.println("Instrument-Validation: "+instrumentValidation);

    }
}