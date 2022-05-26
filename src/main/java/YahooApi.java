import org.json.JSONArray;
import org.json.JSONObject;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.Interval;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.util.Calendar;


public class YahooApi {

    private SqlTable sqlTable;

    public YahooApi(){
        sqlTable = new SqlTable();
    }

    public void priceImport(String ticker){

        SqlTable sqlTable = new SqlTable();
        //int id = sqlTable.getInstrumentID(ticker);
        String yahooUri = "https://yfapi.net/v8/finance/spark?interval=1d&range=5d&symbols="+ticker;

        //Connect to Yahoo API
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(yahooUri))
                .header("x-api-key", "sJXuN0xzJZ3hhqjjecWvHa90v3FtWMIj29B0rFB7")
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

            //Upload data to mySQL
            for(int i = 0; i<jsonArrayDate.length();i++){
                String stringTimeStamp = jsonArrayDate.get(i).toString();
                int timestamp = Integer.parseInt(stringTimeStamp);
                String stringPrice = jsonArrayPrice.get(i).toString();
                float price = Float.parseFloat(stringPrice);
                sqlTable.insertPrice(ticker,timestamp,price);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
