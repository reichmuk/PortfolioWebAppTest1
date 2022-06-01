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

    public void calcPortfolioReturn(String portfolio){
        ArrayList<String> tickerList = sqlTable.getPortfolioTickers(portfolio);
        float portfolioReturn = 0;

        for(String value:tickerList){
            if(value!="PORTFOLIO"){
                float weight = sqlTable.getPortfolioWeight(value,portfolio);
                float instrumentReturn = sqlTable.getMetricSummaryValue(value,"avgSimpleReturn");
                portfolioReturn= portfolioReturn+(weight*instrumentReturn);
                System.out.println(weight*instrumentReturn);
            }
        }
        System.out.println("Der Portfolioreturn beträgt: "+portfolioReturn);
    }

    public int countPortfolioInstruments(String portfolio){
        ArrayList<String> tickerList = sqlTable.getPortfolioTickers(portfolio);
        int counter = 0;
        for(String value : tickerList){
            if(value!="PORTFOLIO"){
                counter++;
            }
        }
        return counter;
    }

    public void calcPortfolioVolatility(String portfolio){
        ArrayList<String> tickerList = sqlTable.getPortfolioTickers(portfolio);
        float portfolioVolatility = 0;
        int countInstruments = countPortfolioInstruments(portfolio);
        float weights[] = new float[countInstruments];
        float varianceCovarianceMatrix[][] = new float[countInstruments][countInstruments];

        for (String value : tickerList){
            int counter = 0;
            if(value!="PORTFOLIO"){
                float weight = sqlTable.getPortfolioWeight(value, portfolio);
                weights[counter] = weight;
                counter++;
            }
        }
    }
}
