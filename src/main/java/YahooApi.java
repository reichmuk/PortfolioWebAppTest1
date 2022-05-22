import org.json.JSONArray;
import org.json.JSONObject;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.Interval;

import javax.json.JsonObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;


public class YahooApi {

    public YahooApi(){

    }

    public void testAPI(String ticker){

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://yfapi.net/v8/finance/spark?interval=1d&range=5d&symbols=UBSG.SW"))
                .header("x-api-key", "sJXuN0xzJZ3hhqjjecWvHa90v3FtWMIj29B0rFB7")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        try{
            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject jsonObject = new JSONObject(response.body());
            System.out.println(response.body());


            //Get the Dates of the JSONFile

            JSONArray jsonArrayDate = (JSONArray) jsonObject.getJSONObject("UBSG.SW").get("timestamp");
            JSONArray jsonArrayPrice = (JSONArray) jsonObject.getJSONObject("UBSG.SW").get("close");
            Iterator<Object> iterator = jsonArrayDate.iterator();
            while(iterator.hasNext()){
                System.out.println(iterator.next());
            }

            //JSONArray jsonArray = new JSONArray(response.body());
            //for(int i = 0; i<jsonArray.length();i++){
            //    JSONObject object = jsonArray.getJSONObject(i);
                //System.out.println(object.getString("timestamp"));
                //System.out.println(object.getString("close"));
            //}

            ArrayList<String> list1 = new ArrayList<String>();

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void testAPI2(){
        try {
            //Stock stock = YahooFinance.get("UBSG.SW");
            //stock.print();

            Calendar from = Calendar.getInstance();
            Calendar to = Calendar.getInstance();
            from.add(Calendar.YEAR, -1); // from 5 years ago

            Stock google = YahooFinance.get("GOOG", from, to, Interval.DAILY);

        }catch (Exception e){
            e.printStackTrace();
        }

    }


}
