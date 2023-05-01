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
            System.out.println("DB reset COMPLETED!");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //Variables
        SqlTable sqlTable = Control.getSqlTable();
        Calculations calculations = Control.getCalculations();
        YahooApi yahooApi = Control.getYahooApi();

        ArrayList<String> instrumentList = new ArrayList<String>();
        ArrayList<String> tickerList = new ArrayList<>();
        ArrayList<Integer> quantityList = new ArrayList<Integer>();
        ArrayList<Integer> optimalQuantityList = new ArrayList<>();
        ArrayList<Double> latestPriceList = new ArrayList<>();
        ArrayList<Double> weightList = new ArrayList<>();

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
        for(int i =0; i<5;i++){ //ACHTUNG LOOP noch anpassen!
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
        if(instrumentList.size()<2){
            instrumentValidation=false;
        }
        HashSet<String> instrumentSet = new HashSet<String>(instrumentList);

        if(instrumentList.size()!=instrumentSet.size()){
            instrumentValidation=false;
        }


        //Validation
        if(strategyValidation&&targetReturnValidation&&quantityValidation&&instrumentValidation){

            tickerList = sqlTable.getTickerList(instrumentList);

            //Import and store prices of all selected instruments and get lastPriceList
            yahooApi.priceImport(tickerList);
            System.out.println("Price Import COMPLETED!");

            //Get lastPriceList
            latestPriceList=sqlTable.getLatestPriceList(tickerList);

            //Calc portfolioValue
            double portfolioValue = calculations.calcPortfolioValue(quantityList,latestPriceList);
            sqlTable.insertMetricSummary(Constants.PORTFOLIO,Constants.CURRENTPORTFOLIOVALUE,portfolioValue);
            System.out.println("Calc Portfolio Value COMPLETED!");

            //Add all instruments to portfolio with quantity and weight
            for(int i = 0; i<tickerList.size(); i++){
                int quantity = quantityList.get(i);
                double latestPrice = latestPriceList.get(i);
                double weight = (quantity*latestPrice)/portfolioValue;
                weightList.add(weight);
            }
            sqlTable.insertPortfolio(tickerList, Constants.CURRENT,quantityList,weightList);

            //Calc and store singleReturns and summaryMetrics for each instrument
            calculations.calcSingleReturn(tickerList);
            System.out.println("Calc Single Returns COMPLETED!");


            //Process all portfolio calculations
            calculations.calcPortfolioReturn(tickerList, Constants.CURRENT);
            System.out.println("Calc Portfolio Return COMPLETED!");
            calculations.calcCorrelations(tickerList);
            System.out.println("Calc Correlations COMPLETED!");
            calculations.calcPortfolioVolatility(tickerList, Constants.CURRENT);

            if(strategy.equals(Constants.MINRISK)){
                optimalQuantityList = calculations.calcMinRiskPortfolio(tickerList, strategy);
                System.out.println("Calc MinRisk-Portfolio COMPLETED!");
                double optimalPortfolioValue = calculations.calcPortfolioValue(optimalQuantityList,latestPriceList);
                sqlTable.insertMetricSummary(Constants.PORTFOLIO,Constants.MINRISKPORTFOLIOVALUE,optimalPortfolioValue);
                System.out.println("Calc optimalPortfolioValue COMPLETED!");
            }

            if(strategy.equals(Constants.TARGETRETURN)){
                optimalQuantityList = calculations.calcOptimalPortfolio(tickerList, strategy,targetReturnCondition);
                System.out.println("Calc TargetReturn-Portfolio COMPLETED!");
                double optimalPortfolioValue = calculations.calcPortfolioValue(optimalQuantityList,latestPriceList);
                sqlTable.insertMetricSummary(Constants.PORTFOLIO,Constants.TARGETRETURNPORTFOLIOVALUE,optimalPortfolioValue);
                System.out.println("Calc optimalPortfolioValue COMPLETED!");
            }

            calculations.calcPortfolioReturn(tickerList, strategy);
            System.out.println("Calc optimalPortfolioReturn COMPLETED!");
            calculations.calcPortfolioVolatility(tickerList, strategy);
            System.out.println("Calc optimalPortfolioVolatility COMPLETED");

            //Direct to result page
            response.sendRedirect("result.jsp");
        } else {
            response.sendRedirect("error.jsp?strategy="+strategyValidation+"&return="+targetReturnValidation+"&quantity="+quantityValidation+"&instrument="+instrumentValidation);
        }
    }
}