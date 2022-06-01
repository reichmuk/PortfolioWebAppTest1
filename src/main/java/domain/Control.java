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
