import java.util.ArrayList;
import java.lang.Math.*;

public class Calculations {

    private SqlTable sqlTable;

    public Calculations(){
        sqlTable = new SqlTable();
    }

    public void calcSingleReturn(String ticker){
        ArrayList<Float> prices = sqlTable.getPriceList("price",ticker);
        ArrayList<Float> timeStamps = sqlTable.getPriceList("time_stamp",ticker);
        for(int i = 0; i< prices.size(); i++){
            if(i==0){} else{
                float simpleReturn = (prices.get(i) - prices.get(i-1)) / prices.get(i-1);
                float steadyReturn = (float) Math.log(1+simpleReturn);
                float timeStamp = timeStamps.get(i);
                int timeStampInt = (int) timeStamp;
                sqlTable.insertMetric(ticker,timeStampInt,"simpleReturn",simpleReturn);
                sqlTable.insertMetric(ticker,timeStampInt,"steadyReturn",steadyReturn);
            }
        }
    }

    public void calcMetricSummary(String ticker){
        ArrayList<Float> steadyReturns = sqlTable.getMetricList("steadyReturn",ticker);
        ArrayList<Float> simpleReturns = sqlTable.getMetricList("simpleReturn", ticker);
    }



}
