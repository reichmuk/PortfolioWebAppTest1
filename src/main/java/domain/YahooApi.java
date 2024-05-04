package domain;
import constants.Constants;
import org.json.JSONArray;
import org.json.JSONObject;
import persistance.SqlTable;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

/**
 * The class YahooAPI performs the price-data-import from Yahoo Finance API (https://www.yahoofinanceapi.com/) and
 * stores the data in the MySQL-DB.
 * @author Kevin Reichmuth
 * @version 31.08.2022
 */
public class YahooApi {

    //Declare variables
    private SqlTable sqlTable;
    private static ArrayList<Integer> timeStampList;

    /**
     * CONSTRUCTOR
     */
    public YahooApi(){
        sqlTable = Control.getSqlTable();
        timeStampList = new ArrayList<>();
    }

    /**
     * APPROVED
     * Method that imports the prices and timestamps from the Yahoo API.
     * @param tickerList the list with all tickers
     * @return returns the lastPriceList, which contains the latest prie of each ticker
     */

    public void priceImport(ArrayList<String> tickerList, String timeFrame){

        for(int i = 0; i<tickerList.size(); i++){
            //Variables
            double lastPrice=0;
            ArrayList<Integer> timeStampListImport = new ArrayList<>();
            ArrayList<Double> priceList = new ArrayList<>();
            String ticker = tickerList.get(i);
            String yahooUri = "https://yfapi.net/v8/finance/spark?interval=1d&range="+timeFrame+"&symbols="+ticker;

            //Connect to Yahoo API
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(yahooUri))
                    .header("x-api-key", Constants.YHFINANCEKEY)
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
            try {
                //Create JSON Object
                HttpResponse<String> response = HttpClient.newHttpClient()
                        .send(request, HttpResponse.BodyHandlers.ofString());
                JSONObject jsonObject = new JSONObject(response.body());

                //Get the data from the JSON Object
                JSONArray jsonArrayDate = (JSONArray) jsonObject.getJSONObject(ticker).get("timestamp");
                JSONArray jsonArrayPrice = (JSONArray) jsonObject.getJSONObject(ticker).get("close");

                //Move data from JSON to ArrayList
                for(int j = 0; j<jsonArrayDate.length();j++){
                    String stringTimeStamp = jsonArrayDate.get(j).toString();
                    int timestamp = Integer.parseInt(stringTimeStamp);
                    timeStampListImport.add(timestamp);
                    String stringPrice = jsonArrayPrice.get(j).toString();
                    double price = Double.parseDouble(stringPrice);
                    priceList.add(price);
                }

                //Build first Timestamp List for Cleanup
                if(i==0){
                    timeStampList=timeStampListImport;
                }
                timeStampList.retainAll(timeStampListImport);

                //Price upload
                sqlTable.priceUpload(ticker,timeStampListImport,priceList);

            } catch (Exception e){
            e.printStackTrace();
            }
        }
        sqlTable.timeStampCleanup(tickerList,timeStampList);
    }

    public ArrayList<Integer> getTimeStampList() {
        return timeStampList;
    }

}