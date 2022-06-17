package domain;
import java.util.ArrayList;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import persistance.SqlTable;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;



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

    public void calcCorrelations(String portfolio){
        ArrayList<String> tickerList = sqlTable.getPortfolioTickers(portfolio);
        for(String ticker1 : tickerList){
            for(String ticker2 : tickerList){
                String metric = "Correl-"+ticker1+"-"+ticker2;
                ArrayList<Double> returnList1 = sqlTable.getMetricListDouble("steadyReturn",ticker1);
                ArrayList<Double> returnList2 = sqlTable.getMetricListDouble("steadyReturn",ticker2);
                double[] list1 = ArrayUtils.toPrimitive(returnList1.toArray(new Double[0]));
                double[] list2 = ArrayUtils.toPrimitive(returnList2.toArray(new Double[0]));
                double correlation = new PearsonsCorrelation().correlation(list1,list2);
                float correlationFloat = (float) correlation;
                sqlTable.insertMetricSummary(ticker1,metric,correlationFloat);
            }
        }
    }

    public void calcPortfolioReturn(String portfolio){
        ArrayList<String> tickerList = sqlTable.getPortfolioTickers(portfolio);
        float portfolioReturn = 0;

        for(String value:tickerList){
            if(value!="PORTFOLIO"){
                float weight = sqlTable.getPortfolioWeight(value,portfolio);
                float instrumentReturn = sqlTable.getMetricSummaryValue(value,"avgSimpleReturn");
                portfolioReturn= portfolioReturn+(weight*instrumentReturn);
            }
        }
        sqlTable.insertMetricSummary("PORTFOLIO","portfolioReturn",portfolioReturn);
        //System.out.println("Der Portfolioreturn betraegt: "+portfolioReturn);
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
        double weights[] = new double[countInstruments];
        double varianceCovarianceMatrix[][] = new double[countInstruments][countInstruments];
        int counter1 = 0;
        int counter2 = 0;

        //write the weights into weights[]
        for (String ticker : tickerList){
            double weight = (double) sqlTable.getPortfolioWeight(ticker, portfolio);
            weights[counter1] = weight;
            counter1++;
        }
        counter1 = 0;

        //Print Weights
        for(int i = 0; i<tickerList.size();i++){
            System.out.println(weights[i]);
        }

        //write covariances into varianceCovarainceMatrix[][]
        for(String ticker : tickerList){
            float stdev1 = sqlTable.getMetricSummaryValue(ticker,"standardDeviation");

            for (String ticker2 : tickerList){
                float stdev2 = sqlTable.getMetricSummaryValue(ticker2,"standardDeviation");
                String correlationString = "Correl-"+ticker+"-"+ticker2;
                float correlation = sqlTable.getMetricSummaryValue(ticker,correlationString);
                double varianceCovariance = (double) stdev1*stdev2*correlation;
                varianceCovarianceMatrix[counter1][counter2]=varianceCovariance;
                counter1++;
            }
            counter1=0;
            counter2++;
        }
        counter2 = 0;

        /*
        for(int i = 0; i<tickerList.size();i++){
            for(int j = 0; j<tickerList.size();j++){
                System.out.print(varianceCovarianceMatrix[i][j]+"     ");
            }
            System.out.println();
        }
         */

        RealMatrix matrixWeightColumn = MatrixUtils.createColumnRealMatrix(weights);
        RealMatrix matrixWeightRow = MatrixUtils.createRowRealMatrix(weights);
        RealMatrix matrixVarianceCovariance = MatrixUtils.createRealMatrix(varianceCovarianceMatrix);
        RealMatrix actual1 = matrixWeightRow.multiply(matrixVarianceCovariance).multiply(matrixWeightColumn);
        portfolioVolatility = (float) actual1.getEntry(0,0);
        sqlTable.insertMetricSummary("PORTFOLIO","portfolioVolatility",portfolioVolatility);
    }

    public void calcOptimalPortfolio(String portfolio,double condition){
        String newPortfolio = "";
        ArrayList<String> tickerList = sqlTable.getPortfolioTickers(portfolio);
        int countInstruments = countPortfolioInstruments(portfolio);
        int matrixSize = countInstruments+2;
        double lagrangeMatrix[][] = new double[matrixSize][matrixSize];
        double conditions[] = new double[matrixSize];
        int counter1 = 0;
        int counter2 = 0;
        int counter3 = 0;

        if(condition==0){
            newPortfolio="minRisk";
        }else {
            newPortfolio="targetReturn";
        }

        //write covariances into minRiskMatrix[][]
        for(String ticker : tickerList){
            float stdev1 = sqlTable.getMetricSummaryValue(ticker,"standardDeviation");

            for (String ticker2 : tickerList){
                float stdev2 = sqlTable.getMetricSummaryValue(ticker2,"standardDeviation");
                String correlationString = "Correl-"+ticker+"-"+ticker2;
                float correlation = sqlTable.getMetricSummaryValue(ticker,correlationString);
                double varianceCovariance = (double) stdev1*stdev2*correlation;
                lagrangeMatrix[counter1][counter2]=varianceCovariance;
                counter1++;
            }
            counter1=0;
            counter2++;
        }

        //write returns into minRiskMatrix[][]
        counter1 = 0;
        counter2 = countInstruments;
        counter3 = countInstruments+1;

        for(String ticker : tickerList){
            double simpleReturn = (double) sqlTable.getMetricSummaryValue(ticker,"avgSimpleReturn");
            lagrangeMatrix[counter1][counter2] = simpleReturn;
            lagrangeMatrix[counter2][counter1] = simpleReturn;
            lagrangeMatrix[counter1][counter3] = (double) 1;
            lagrangeMatrix[counter3][counter1] = (double) 1;
            counter1++;
        }
        lagrangeMatrix[counter2][counter2] = 0;
        lagrangeMatrix[counter2][counter3] = 0;
        lagrangeMatrix[counter3][counter2] = 0;
        lagrangeMatrix[counter3][counter3] = 0;

        //write values into conditions[]
        for(int i = 0; i<countInstruments; i++){
            conditions[i] = 0;
        }
        conditions[countInstruments] = condition;
        conditions[countInstruments+1]=1;

        RealMatrix matrixConditions = MatrixUtils.createColumnRealMatrix(conditions);
        RealMatrix matrixLagrange = MatrixUtils.createRealMatrix(lagrangeMatrix);
        RealMatrix inverseMatrixLagrange = MatrixUtils.inverse(matrixLagrange);
        RealMatrix actual = inverseMatrixLagrange.multiply(matrixConditions);

        //write optimal weights in DB (portfolio)
        /*
        for(int i = 0; i<5;i++){
            System.out.println(actual.getEntry(i,0));
        }

         */
        counter1=0;
        for(String ticker : tickerList){
            float newWeitght = (float) actual.getEntry(counter1,0);
            sqlTable.insertPortfolio(ticker,newPortfolio,newWeitght);
            counter1++;
        }



        /*
        for(int i = 0;i<conditions.length;i++){
            System.out.println(conditions[i]+";");
        }
         */


        /*
        for(int i = 0; i<lagrangeMatrix.length; i++){
            for(int j = 0; j<lagrangeMatrix.length; j++){
                System.out.print(lagrangeMatrix[i][j]+";");
            }
            System.out.println();
        }
         */


    }





}
