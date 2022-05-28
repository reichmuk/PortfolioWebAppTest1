package persistance;
import java.sql.*;
import java.util.ArrayList;

public class SqlTable {


    public SqlTable(){
    }

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

    public void insertPrice(String ticker, int timeStamp, float price){
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

    public ArrayList<Float> getPriceList(String column, String ticker){
        String tickerString = "\""+ticker+"\"";
        ArrayList<Float> priceList = new ArrayList<Float>();
        ArrayList<Float> timeStampList = new ArrayList<Float>();
        Connection connection = getConnection();
        String sqlCommand = "SELECT time_stamp, price from prices where ticker="+tickerString+" order by time_stamp ASC;";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlCommand);
            while(resultSet.next()){
                float price = Float.parseFloat(resultSet.getString("price"));
                priceList.add(price);
                float timeStamp = Float.parseFloat(resultSet.getString("time_stamp"));
                timeStampList.add(timeStamp);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        if(column=="price"){
            return priceList;
        } else {return timeStampList;}
    }

    public ArrayList<Float> getMetricList(String metric, String ticker){
        String tickerString = "\""+ticker+"\"";
        String metricString = "\""+metric+"\"";
        ArrayList<Float> metricList = new ArrayList<Float>();
        Connection connection = getConnection();
        String sqlCommand = "SELECT value from metrics where ticker="+tickerString+" && metric ="+metricString+";";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlCommand);
            while(resultSet.next()){
                float value = Float.parseFloat(resultSet.getString("value"));
                metricList.add(value);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return metricList;
    }


    public void insertMetric(String ticker, int timeStamp, String metric, float value){
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

    public void insertMetricSummary(String ticker, String metric, float value){
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


    public void createSqlTable(String ticker){
        String tableName = "Prices"+ ticker;
        String sqlCommand = "CREATE TABLE "+tableName+"(instrument_id INT, date_id INT, price FLOAT(10), PRIMARY KEY (instrument_id,date_id))";
        Connection connection = getConnection();
                try {
            Statement statement = connection.createStatement();
            statement.execute(sqlCommand);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public int getInstrumentID(String ticker){
        int instrumentID=0;
        Connection connection = getConnection();
        String tickerString = "\""+ticker+"\"";
        String sqlCommand = "SELECT * from instruments WHERE ticker ="+tickerString;

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlCommand);
            while(resultSet.next()){
                instrumentID=Integer.parseInt(resultSet.getString("instrument_id"));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return instrumentID;
    }


}
