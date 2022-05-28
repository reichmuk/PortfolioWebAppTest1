package domain;
import java.util.ArrayList;
import persistance.SqlTable;

public class Calculations {

    private SqlTable sqlTable;

    public Calculations(){
        sqlTable = Control.getSqlTable();
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
        ArrayList<Float> simpleReturns = sqlTable.getMetricList("simpleReturn", ticker);
        ArrayList<Float> steadyReturns = sqlTable.getMetricList("steadyReturn",ticker);
        float avgSimpleReturns=0;
        float avgSteadyReturns=0;
        float standardDeviation = 0;
        for(float value : simpleReturns){
            avgSimpleReturns = avgSimpleReturns+value;

        }
        avgSimpleReturns = avgSimpleReturns/simpleReturns.size();
        sqlTable.insertMetricSummary(ticker,"avgSimpleReturn",avgSimpleReturns);

        for(float value : steadyReturns){
            avgSteadyReturns = avgSteadyReturns+value;

        }
        avgSteadyReturns = avgSteadyReturns/steadyReturns.size();
        sqlTable.insertMetricSummary(ticker,"avgSteadyReturn",avgSteadyReturns);

        for(float value : steadyReturns){
            float calc = (value-avgSteadyReturns);
            calc = (float) Math.pow(calc,2);
            standardDeviation = standardDeviation+calc;
        }
        standardDeviation = standardDeviation/(steadyReturns.size()-1);
        standardDeviation = (float) Math.sqrt(standardDeviation);
        sqlTable.insertMetricSummary(ticker,"standardDeviation",standardDeviation);

    }



}
