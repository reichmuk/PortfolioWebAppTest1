package persistance;
import constants.Constants;
import java.sql.*;
import java.util.ArrayList;

/**
 * The class SqlTable grants the access to the MySQL-DB.
 * Data from the MySQL-tables can be accessed and manipulated.
 * @author Kevin Reichmuth
 * @version 31.08.2022
 */
public class SqlTable {

    /**
     * CONSTRUCTOR
     */
    public SqlTable(){
    }

    //------------------------------------------------------------------------------------------------------------------
    // STARTUP - Methods
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Method that performs the connection to the MySQL-DB "instrumentDB".
     */
    public Connection getConnection(){
        Connection connection = null;
        String url = "jdbc:mysql://127.0.0.1:3306/instrumentDB";
        String user = "root";
        String password = "Blue_22!";
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e){
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Method that performs a cleanup if timeStamps in the different instruments are not consistent.
     */
    public void timeStampCleanup(){
        ArrayList<String> tickerList = getPortfolioTickers(Constants.CURRENT);
        String startTicker = tickerList.get(0);
        ArrayList<Double> startTickerTimestampList = getPriceList(Constants.TIMESTAMP,startTicker);

        //Generate list that consists only values which also exist in all other lists
        for(String ticker : tickerList){
            ArrayList<Double> tickerTimestampList = getPriceList(Constants.TIMESTAMP,ticker);
            startTickerTimestampList.retainAll(tickerTimestampList);
        }

        //Remove all values from DB which don't exist in main-list
        for(String ticker : tickerList){
            ArrayList<Double> tickerTimestampList = getPriceList(Constants.TIMESTAMP,ticker);
            tickerTimestampList.removeAll(startTickerTimestampList);
            for(double timeStamp : tickerTimestampList){
                removePrice((int)timeStamp,ticker);
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    // QUERY - Methods
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Method that returns the ticker of a given instrument.
     * @param instrument The name of the instrument
     * @return returns instrument ticker
     */
    public String getInstrumentTicker(String instrument){
        String instrumentName = "\""+instrument+"\"";
        String ticker = "";
        Connection connection = getConnection();
        String sqlCommand = "SELECT ticker from instruments where name="+instrumentName+";";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlCommand);
            while(resultSet.next()){
                String value = resultSet.getString(Constants.TICKER);
                ticker = value;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return ticker;
    }

    /**
     * Method to get a value from the instrument table.
     * @param selectColumn The ticker of the instrument
     * @param whereColumn The respective column (ticker, ccy, name, country)
     * @return returns the column value
     */
    public String getInstrumentData(String selectColumn, String whereColumn, String condition){
        String conditionString = "\""+condition+"\"";
        String result = "";
        Connection connection = getConnection();
        String sqlCommand = "SELECT "+selectColumn+" from instruments where "+whereColumn+"="+conditionString+";";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlCommand);
            while(resultSet.next()){
                result = resultSet.getString(selectColumn);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Method to to get the latest price of an instrument.
     * @param ticker The ticker of the instrument.
     * @return returns the latest price
     */
    public double getLatestPrice(String ticker){
        ArrayList<Double> prices = getPriceList(Constants.PRICE,ticker);
        int listSize = prices.size()-1;
        double price = prices.get(listSize);
        return price;
    }

    /**
     * Method to get a list with all prices or a list of all timestamps (attached to the price) of a dedicated
     * instrument.
     * @param column The column is either the "price" or the "time_stamp"
     * @param ticker The ticker of the instrument
     * @return returns the priceList or timeStampList
     */
    public ArrayList<Double> getPriceList(String column, String ticker){
        String tickerString = "\""+ticker+"\"";
        ArrayList<Double> priceList = new ArrayList<Double>();
        ArrayList<Double> timeStampList = new ArrayList<Double>();
        Connection connection = getConnection();
        String sqlCommand = "SELECT time_stamp, price from prices where ticker="+tickerString+" order by time_stamp ASC;";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlCommand);
            while(resultSet.next()){
                double price = Double.parseDouble(resultSet.getString(Constants.PRICE));
                priceList.add(price);
                double timeStamp = Double.parseDouble(resultSet.getString(Constants.TIMESTAMP));
                timeStampList.add(timeStamp);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        if(column==Constants.PRICE){
            return priceList;
        }else {return timeStampList;}
    }

    /**
     * The method returns the respective metric-values of the entire Metric-List from the MySQL-DB.
     * @param metric The respective metric (simpleReturn or steadyReturn)
     * @param ticker The ticker of the instrument
     * @return returns the metricList
     */
    public ArrayList<Double> getMetricList(String metric, String ticker){
        String tickerString = "\""+ticker+"\"";
        String metricString = "\""+metric+"\"";
        ArrayList<Double> metricList = new ArrayList<Double>();
        Connection connection = getConnection();
        String sqlCommand = "SELECT value from metrics where ticker="+tickerString+" && metric ="+metricString+";";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlCommand);
            while(resultSet.next()){
                double value = Double.parseDouble(resultSet.getString(Constants.VALUE));
                metricList.add(value);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return metricList;
    }

    /**
     * Method to get a value from the metrics_summary table.
     * @param ticker The ticker of the instrument
     * @param metric The respective metric (avgSimpleReturn, avgSteadyReturn, standardDeviation, Correlation)
     * @return returns the metricSummaryValue
     */
    public double getMetricSummaryValue(String ticker, String metric){
        String tickerString = "\""+ticker+"\"";
        String metricString = "\""+metric+"\"";
        double metricSummaryValue = 0;
        Connection connection = getConnection();
        String sqlCommand = "SELECT value from metrics_summary where ticker="+tickerString+" && metric ="+metricString+";";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlCommand);
            while(resultSet.next()){
                double value = Double.parseDouble(resultSet.getString(Constants.VALUE));
                metricSummaryValue = value;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return metricSummaryValue;
    }

    /**
     * Method that returns a list with all tickers of a given portfolio.
     * @param portfolio The portfolio (current, minRisk, targetReturn)
     * @return returns the tickerList
     */
    public ArrayList<String> getPortfolioTickers(String portfolio){
        String portfolioString = "\""+portfolio+"\"";
        ArrayList<String> tickerList = new ArrayList<String>();
        Connection connection = getConnection();
        String sqlCommand = "SELECT ticker from portfolio where portfolio="+portfolioString+";";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlCommand);
            while(resultSet.next()){
                String value = resultSet.getString(Constants.TICKER);
                tickerList.add(value);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return tickerList;
    }

    /**
     * Method that returns the quantity of an instrument from a given portfolio.
     * @param ticker The ticker of the instrument
     * @param portfolio The portfolio (current, minRisk, targetReturn)
     * @return returns the portfolioQuantity
     */
    public int getPortfolioQuantity(String ticker, String portfolio){
        String tickerString = "\""+ticker+"\"";
        String portfolioString = "\""+portfolio+"\"";
        int portfolioQuantity = 0;
        Connection connection = getConnection();
        String sqlCommand = "SELECT quantity from portfolio where ticker="+tickerString+" && portfolio ="+portfolioString+";";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlCommand);
            while(resultSet.next()){
                int value = Integer.parseInt(resultSet.getString(Constants.QUANTITY));
                portfolioQuantity = value;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return portfolioQuantity;
    }

    /**
     * Method that returns the weight of an instrument from a given portfolio.
     * @param ticker The ticker of the instrument
     * @param portfolio The portfolio (current, minRisk, targetReturn)
     * @return returns the portfolioWeight
     */
    public double getPortfolioWeight(String ticker, String portfolio){
        String tickerString = "\""+ticker+"\"";
        String portfolioString = "\""+portfolio+"\"";
        double portfolioWeight = 0;
        Connection connection = getConnection();
        String sqlCommand = "SELECT weight from portfolio where ticker="+tickerString+" && portfolio ="+portfolioString+";";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlCommand);
            while(resultSet.next()){
                double value = Double.parseDouble(resultSet.getString(Constants.WEIGHT));
                portfolioWeight = value;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return portfolioWeight;
    }

    /**
     * Method that returns the value of the portfolio.
     * @return returns the portfolioValue
     */
    public double getPortfolioValue(String portfolio){
        double portfolioValue = 0;
        String portfolioString = "\""+portfolio+"PortfolioValue"+"\"";
        Connection connection = getConnection();
        String sqlCommand = "SELECT value from metrics_summary where ticker=\"PORTFOLIO\" && metric="+portfolioString+";";
        //String sqlCommand = "SELECT value from metrics_summary where ticker=\"PORTFOLIO\" && metric=\"portfolioValue\";";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlCommand);
            while(resultSet.next()){
                double value = Double.parseDouble(resultSet.getString(Constants.VALUE));
                portfolioValue = value;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return portfolioValue;
    }

    //------------------------------------------------------------------------------------------------------------------
    // MANIPULATION - Methods
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Method to store a price in the MySQL-DB.
     * @param ticker The ticker of the instrument.
     * @param timeStamp The timestamp (date) of the price
     * @param price the price of the instrument
     */
    public void insertPrice(String ticker, int timeStamp, double price){
        String tickerString = "\""+ticker+"\"";
        Connection connection = getConnection();
        String sqlCommand = "INSERT INTO prices VALUES("+tickerString+","+timeStamp+","+price+");";
        try {
            Statement statement = connection.createStatement();
            statement.execute(sqlCommand);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Method to remove a price from the DB.
     * @param ticker The ticker of the instrument
     * @param timeStamp The timestamp (date) of the price
     */
    public void removePrice(int timeStamp, String ticker){
        String tickerString = "\""+ticker+"\"";
        Connection connection = getConnection();
        String sqlCommand = "DELETE from prices where time_stamp="+timeStamp+" and ticker="+tickerString+";";
        System.out.println(sqlCommand);

        try {
            Statement statement = connection.createStatement();
            statement.execute(sqlCommand);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Method to store a metric (simpleReturn or steadyReturn) in the MySQL-DB table "metrics".
     * @param ticker The ticker of the instrument.
     * @param timeStamp The timeStamp of the respective metric
     * @param metric The respective metric (simpleReturn or steadyReturn)
     * @param value The metric-value of the respective ticker on the given timeStamp (date)
     */
    public void insertMetric(String ticker, int timeStamp, String metric, double value){
        String tickerString = "\""+ticker+"\"";
        String metricString = "\""+metric+"\"";
        Connection connection = getConnection();
        String sqlCommand = "INSERT INTO metrics VALUES("+tickerString+","+timeStamp+","+metricString+","+value+");";
        try {
            Statement statement = connection.createStatement();
            statement.execute(sqlCommand);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Method to store a summary-metric (avgSimpleReturn, avgSteadyReturn, standardDeviation, Correlation) in the
     * MySQL-DB table "metrics_summary".
     * @param ticker The ticker of the instrument
     * @param metric The respective metric (avgSimpleReturn, avgSteadyReturn, standardDeviation, Correlation)
     * @param value The metric-value of the respective ticker
     */
    public void insertMetricSummary(String ticker, String metric, double value){
        String tickerString = "\""+ticker+"\"";
        String metricString = "\""+metric+"\"";
        Connection connection = getConnection();
        String sqlCommand = "INSERT INTO metrics_summary VALUES("+tickerString+","+metricString+","+value+");";
        try {
            Statement statement = connection.createStatement();
            statement.execute(sqlCommand);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Method to add an instrument to the portfolio.
     * @param ticker The ticker of the instrument
     * @param portfolio The portfolio (current, minRisk, targetReturn)
     * @param weight The respective weight of the instrument in the portfolio
     */
    public void insertPortfolio(String ticker, String portfolio, int quantity, double weight){
        String tickerString = "\""+ticker+"\"";
        String portfolioString = "\""+portfolio+"\"";
        Connection connection = getConnection();
        String sqlCommand = "INSERT INTO portfolio VALUES("+tickerString+","+portfolioString+","+quantity+","+weight+");";
        try {
            Statement statement = connection.createStatement();
            statement.execute(sqlCommand);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Method that resets a table in the MySQL-DB.
     * @param table The table to be reset
     */
    public void resetTable(String table){
        Connection connection = getConnection();
        String sqlCommand = "TRUNCATE TABLE " +table+";";
        try {
            Statement statement = connection.createStatement();
            statement.execute(sqlCommand);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}