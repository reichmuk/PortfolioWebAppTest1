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
     * APPROVED
     * Method that performs the connection to the MySQL-DB "instrumentDB".
     */
    public Connection getConnection(){
        Connection connection = null;
        String url = Constants.mysqlUrl;
        String password = "BlueBlueBlue22";
        String user = "root";

        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e){
            e.printStackTrace();
        }
        return connection;
    }


    /**
     * APPROVED
     * Method that performs a cleanup if timeStamps in the different instruments are not consistent.
     */

     public void timeStampCleanup(ArrayList<String> tickerList, ArrayList<Integer> cleanTimestampList){
         try(Connection connection = ConnectionManager.getConnection()){

             //Get timeStampList for respective ticker from DB
             for(String ticker : tickerList){
                 String tickerString = "\""+ticker+"\"";
                 ArrayList<Integer> timeStampList = new ArrayList<Integer>();
                 String sqlCommand = "SELECT time_stamp from prices where ticker="+tickerString+" order by time_stamp ASC;";

                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sqlCommand);
                 while(resultSet.next()){
                     int timeStamp = Integer.parseInt(resultSet.getString(Constants.TIMESTAMP));
                     timeStampList.add(timeStamp);
                 }

                 //Remove all values from DB which don't exist in cleanTimestampList
                 timeStampList.removeAll(cleanTimestampList);

                 for(int timeStamp : timeStampList){
                     String sqlCommand2 = "DELETE from prices where time_stamp="+timeStamp+" and ticker="+tickerString+";";
                     System.out.println(sqlCommand2);
                     statement.execute(sqlCommand2);
                 }

                 // Close the ResultSet, PreparedStatement, and Connection
                 resultSet.close();
                 statement.close();
             }

         } catch (SQLException e) {
             e.printStackTrace();
         }

         System.out.println("TimeStampCleanup COMPLETED!");
     }


    //------------------------------------------------------------------------------------------------------------------
    // QUERY - Methods
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Method that returns the tickerList for a given instrumentList of a given instrument.
     * @param instrumentList the list with all instruments
     * @return returns the tickerList
     */
    public ArrayList<String> getTickerList(ArrayList<String> instrumentList){
        ArrayList<String> tickerList = new ArrayList<>();

        try(Connection connection = ConnectionManager.getConnection()){
            for(int i = 0; i<instrumentList.size(); i++){
                String ticker = "";
                String instrument = instrumentList.get(i);
                String instrumentName = "\""+instrument+"\"";
                String sqlCommand = "SELECT ticker from instruments where name="+instrumentName+";";
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sqlCommand);

                while(resultSet.next()){
                    ticker = resultSet.getString(Constants.TICKER);
                }

                tickerList.add(ticker);

                // Close the ResultSet, PreparedStatement, and Connection
                resultSet.close();
                statement.close();
            }

        }catch (SQLException e) {
            e.printStackTrace();
        }

        return tickerList;
    }

    public ArrayList<Double> getLatestPriceList(ArrayList<String> tickerList){

        ArrayList<Double> latestPriceList = new ArrayList<>();

        try(Connection connection = ConnectionManager.getConnection()){
            Integer lastTimeStamp = 0;
            String ticker1 = tickerList.get(0);
            ticker1 = "\""+ticker1+"\"";
            String sqlCommand1 = "SELECT time_stamp from prices where ticker="+ticker1+";";
            Statement statement = connection.createStatement();
            ResultSet resultSet1 = statement.executeQuery(sqlCommand1);

            while (resultSet1.next()){
                lastTimeStamp = Integer.parseInt(resultSet1.getString(Constants.TIMESTAMP));
            }

            for(String ticker : tickerList){
                ticker = "\""+ticker+"\"";
                String sqlCommand2 = "SELECT price from prices where ticker="+ticker+" and time_stamp="+lastTimeStamp+";";
                ResultSet resultSet2 = statement.executeQuery(sqlCommand2);
                while (resultSet2.next()){
                    latestPriceList.add(Double.parseDouble(resultSet2.getString(Constants.PRICE)));
                }
                resultSet2.close();
            }

            // Close the ResultSet, PreparedStatement, and Connection
            resultSet1.close();
            statement.close();

        }catch (SQLException e) {
            e.printStackTrace();
        }

        return latestPriceList;
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
        String sqlCommand = "SELECT "+selectColumn+" from instruments where "+whereColumn+"="+conditionString+";";

        try(Connection connection = ConnectionManager.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlCommand);

            while(resultSet.next()){
                result = resultSet.getString(selectColumn);
            }

            // Close the ResultSet, PreparedStatement, and Connection
            resultSet.close();
            statement.close();

        }catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Method to get the latest price of an instrument.
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
     * Method to get the currency for a given instrumentName or ticker.
     * @param instrumentName The name of the instrument.
     * @return returns the respective CCY.
     */
    public String getInstrumentCcy(String instrumentName){
        String name = "\""+instrumentName+"\"";
        String sqlCommand = "SELECT ccy from instruments where ticker="+name+";";
        String result = "";

        try(Connection connection = ConnectionManager.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlCommand);

            while(resultSet.next()){
                result = resultSet.getString(Constants.CCY);
            }

            // Close the ResultSet, PreparedStatement, and Connection
            resultSet.close();
            statement.close();

        }catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
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
        String sqlCommand = "SELECT time_stamp, price from prices where ticker="+tickerString+" order by time_stamp ASC;";

        try (Connection connection = ConnectionManager.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlCommand);
            while(resultSet.next()){
                double price = Double.parseDouble(resultSet.getString(Constants.PRICE));
                priceList.add(price);
                double timeStamp = Double.parseDouble(resultSet.getString(Constants.TIMESTAMP));
                timeStampList.add(timeStamp);
            }

            // Close the ResultSet, PreparedStatement, and Connection
            resultSet.close();
            statement.close();

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
        String sqlCommand = "SELECT value from metrics where ticker="+tickerString+" && metric ="+metricString+";";

        try(Connection connection = ConnectionManager.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlCommand);
            while(resultSet.next()){
                double value = Double.parseDouble(resultSet.getString(Constants.VALUE));
                metricList.add(value);
            }

            // Close the ResultSet, PreparedStatement, and Connection
            resultSet.close();
            statement.close();

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
        String sqlCommand = "SELECT value from metrics_summary where ticker="+tickerString+" && metric ="+metricString+";";

        try(Connection connection = ConnectionManager.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlCommand);
            while(resultSet.next()){
                double value = Double.parseDouble(resultSet.getString(Constants.VALUE));
                metricSummaryValue = value;
            }

            // Close the ResultSet, PreparedStatement, and Connection
            resultSet.close();
            statement.close();

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
        String sqlCommand = "SELECT ticker from portfolio where portfolio="+portfolioString+";";
        try(Connection connection = ConnectionManager.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlCommand);
            while(resultSet.next()){
                String value = resultSet.getString(Constants.TICKER);
                tickerList.add(value);
            }

            // Close the ResultSet, PreparedStatement, and Connection
            resultSet.close();
            statement.close();

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
        String sqlCommand = "SELECT quantity from portfolio where ticker="+tickerString+" && portfolio ="+portfolioString+";";

        try(Connection connection = ConnectionManager.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlCommand);
            while(resultSet.next()){
                int value = Integer.parseInt(resultSet.getString(Constants.QUANTITY));
                portfolioQuantity = value;
            }

            // Close the ResultSet, PreparedStatement, and Connection
            resultSet.close();
            statement.close();

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
        String sqlCommand = "SELECT weight from portfolio where ticker="+tickerString+" && portfolio ="+portfolioString+";";

        try(Connection connection = ConnectionManager.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlCommand);
            while(resultSet.next()){
                double value = Double.parseDouble(resultSet.getString(Constants.WEIGHT));
                portfolioWeight = value;
            }

            // Close the ResultSet, PreparedStatement, and Connection
            resultSet.close();
            statement.close();

        }catch (SQLException e){
            e.printStackTrace();
        }

        return portfolioWeight;
    }

    public ArrayList<Double> getPortfolioWeightList(String portfolio){
        ArrayList<Double> portfolioWeightList = new ArrayList<>();
        String portfolioString = "\""+portfolio+"\"";
        String sqlCommand = "SELECT weight from portfolio where portfolio ="+portfolioString+";";

        try(Connection connection = ConnectionManager.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlCommand);
            while(resultSet.next()){
                double value = Double.parseDouble(resultSet.getString(Constants.WEIGHT));
                portfolioWeightList.add(value);
            }

            // Close the ResultSet, PreparedStatement, and Connection
            resultSet.close();
            statement.close();

        }catch (SQLException e){
            e.printStackTrace();
        }

        return portfolioWeightList;
    }


    /**
     * Method that returns the value of the portfolio.
     * @return returns the portfolioValue
     */
    public double getPortfolioValue(String portfolio){
        double portfolioValue = 0;
        String portfolioString = "\""+portfolio+"PortfolioValue"+"\"";
        String sqlCommand = "SELECT value from metrics_summary where ticker=\"PORTFOLIO\" && metric="+portfolioString+";";

        try(Connection connection = ConnectionManager.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlCommand);
            while(resultSet.next()){
                double value = Double.parseDouble(resultSet.getString(Constants.VALUE));
                portfolioValue = value;
            }

            // Close the ResultSet, PreparedStatement, and Connection
            resultSet.close();
            statement.close();

        }catch (SQLException e){
            e.printStackTrace();
        }

        return portfolioValue;
    }

    //------------------------------------------------------------------------------------------------------------------
    // MANIPULATION - Methods
    //------------------------------------------------------------------------------------------------------------------


    public void priceUpload(String ticker, ArrayList<Integer> timeStampList, ArrayList<Double> priceList) {
        String tickerString = "\"" + ticker + "\"";

        try(Connection connection = ConnectionManager.getConnection()){
            Statement statement = connection.createStatement();
            for (int j = 0; j < timeStampList.size(); j++) {
                int timeStamp = timeStampList.get(j);
                double price = priceList.get(j);
                String sqlCommand = "INSERT INTO prices VALUES(" + tickerString + "," + timeStamp + "," + price + ");";
                statement.execute(sqlCommand);
            }
            // Close the ResultSet, PreparedStatement, and Connection
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addMetricList(String ticker,ArrayList<Integer> timeStampList,String metric,ArrayList<Double> metricList){
        String tickerString = "\""+ticker+"\"";
        String metricString = "\""+metric+"\"";

        try(Connection connection = ConnectionManager.getConnection()){
            Statement statement = connection.createStatement();
            for(int i=0; i<metricList.size();i++){
                int timeStamp = timeStampList.get(i+1);
                double value = metricList.get(i);
                String sqlCommand = "INSERT INTO metrics VALUES("+tickerString+","+timeStamp+","+metricString+","+value+");";
                statement.execute(sqlCommand);
            }
            // Close the ResultSet, PreparedStatement, and Connection
            statement.close();

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
        String sqlCommand = "INSERT INTO metrics_summary VALUES("+tickerString+","+metricString+","+value+");";

        try(Connection connection = ConnectionManager.getConnection()){
            Statement statement = connection.createStatement();
            statement.execute(sqlCommand);
            // Close the ResultSet, PreparedStatement, and Connection
            statement.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Method to add an instrument to the portfolio.
     * @param tickerList The ticker of the instrument
     * @param portfolio The portfolio (current, minRisk, targetReturn)
     * @param weightList The respective weight of the instrument in the portfolio
     */
    public void insertPortfolio(ArrayList<String> tickerList, String portfolio, ArrayList<Integer> quantityList, ArrayList<Double> weightList){

        try(Connection connection = ConnectionManager.getConnection()) {
            Statement statement = connection.createStatement();

            for (int i = 0; i < tickerList.size(); i++) {
                String ticker = tickerList.get(i);
                String tickerString = "\"" + ticker + "\"";
                String portfolioString = "\"" + portfolio + "\"";
                int quantity = quantityList.get(i);
                double weight = weightList.get(i);
                String sqlCommand = "INSERT INTO portfolio VALUES(" + tickerString + "," + portfolioString + "," + quantity + "," + weight + ");";
                statement.execute(sqlCommand);
            }

            // Close the ResultSet, PreparedStatement, and Connection
            statement.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that resets a table in the MySQL-DB.
     * @param table The table to be reset
     */
    public void resetTable(String table){
        String sqlCommand = "TRUNCATE TABLE " +table+";";
        try(Connection connection = ConnectionManager.getConnection()){
            Statement statement = connection.createStatement();
            statement.execute(sqlCommand);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    // RESERVE - Methods
    //------------------------------------------------------------------------------------------------------------------

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

        try{connection.close();
        } catch (SQLException e){e.printStackTrace();}

    }
}