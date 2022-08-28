package domain;
import org.json.JSONArray;
import org.json.JSONObject;
import persistance.SqlTable;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * The class YahooAPI performs the price-data-import from Yahoo Finance API (https://www.yahoofinanceapi.com/) and
 * stores the data in the MySQL-DB.
 * @author Kevin Reichmuth
 * @version 31.08.2022
 */
public class YahooApi {

    //Declare variables
    private SqlTable sqlTable;

    /**
     * CONSTRUCTOR
     */
    public YahooApi(){
        sqlTable = Control.getSqlTable();
    }

    /**
     * Method that imports the historical prices from the respective instrument.
     * The historical prices are stored in the MySQL-DB.
     * @param ticker The ticker of the instrument.
     */
    public void priceImport(String ticker){
        String yahooUri = "https://yfapi.net/v8/finance/spark?interval=1d&range=3mo&symbols="+ticker;

        //Connect to Yahoo API
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(yahooUri))
                .header("x-api-key", "UoiBfllMKe3O9En2EeYPF37w1EYYvXWH8ZeXruPW")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        try{
            //Create JSON Object
            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject jsonObject = new JSONObject(response.body());

            //Get the data from the JSON Object
            JSONArray jsonArrayDate = (JSONArray) jsonObject.getJSONObject(ticker).get("timestamp");
            JSONArray jsonArrayPrice = (JSONArray) jsonObject.getJSONObject(ticker).get("close");

            //Upload data to MySQL-DB
            for(int i = 0; i<jsonArrayDate.length();i++){
                String stringTimeStamp = jsonArrayDate.get(i).toString();
                int timestamp = Integer.parseInt(stringTimeStamp);
                String stringPrice = jsonArrayPrice.get(i).toString();
                double price = Double.parseDouble(stringPrice);
                sqlTable.insertPrice(ticker,timestamp,price);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
