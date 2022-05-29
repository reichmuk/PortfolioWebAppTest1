package domain;

import persistance.SqlTable;


/**
 * PortfolioWebApp - Main Class
 * Contains GETTER and SETTER for each class.
 *
 * @author Kevin Reichmuth
 * @version 28.05.2022
 */
public class Control {

    // Declare variables (classes)
    private static SqlTable sqlTable;
    private static Calculations calculations;
    private static YahooApi yahooApi;



    /**
     * CONSTRUCTOR
     */
    public Control(){
        // Initialize variables
        sqlTable = new SqlTable();
        calculations = new Calculations();
        yahooApi = new YahooApi();

        //CREATE PORTFOLIO WITH WEIGHTS
        //sqlTable.insertPortfolio("UBSG.SW","current", 0.4f);
        //sqlTable.insertPortfolio("NOVN.SW","current", 0.35f);
        //sqlTable.insertPortfolio("ABBN.SW","current",0.25f);

        //yahooApi.priceImport("NOVN.SW");
        //yahooApi.priceImport("ABBN.SW");
        //calculations.calcSingleReturn("NOVN.SW");
        //calculations.calcSingleReturn("ABBN.SW");
        //calculations.calcMetricSummary("NOVN.SW");
        //calculations.calcMetricSummary("ABBN.SW");
        calculations.calcPortfolioReturn("current");

        //yahooApi.priceImport("UBSG.SW");
        //calculations.calcSingleReturn("UBSG.SW");
        //calculations.calcMetricSummary("UBSG.SW");
        //sqlTable.resetTable("prices");
        //sqlTable.resetTable("metrics");
        //sqlTable.resetTable("metrics_summary");




    }

    /**
     * GETTER - Get class Calculations.
     * @return Class Calculations
     */
    public static Calculations getCalculations() {
        return calculations;
    }

    /**
     * GETTER - Get class YahooApi.
     * @return Class YahooApi
     */
    public static YahooApi getYahooApi() {
        return yahooApi;
    }

    /**
     * GETTER - Get class SqlTable.
     * @return Class SqlTable
     */
    public static SqlTable getSqlTable() {
        return sqlTable;
    }
}
