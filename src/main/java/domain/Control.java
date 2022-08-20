package domain;
import constants.Constants;
import persistance.SqlTable;

/**
 * PortfolioWebApp - Main Class
 * Contains GETTER-methods for each class.
 * @author Kevin Reichmuth
 * @version 31.08.2022
 */
public class Control {

    //Declare variables (classes)
    private static SqlTable sqlTable;
    private static Calculations calculations;
    private static YahooApi yahooApi;
    private static Constants constants;


    /**
     * CONSTRUCTOR
     */
    public Control(){
        // Initialize variables
        sqlTable = new SqlTable();
        calculations = new Calculations();
        yahooApi = new YahooApi();
        constants = new Constants();
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

    /**
     * GETTER - Get class Constants.
     * @return Class Constants
     */
    public static Constants getConstants() {
        return constants;
    }
}
