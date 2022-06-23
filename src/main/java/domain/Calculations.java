package domain;
import java.util.ArrayList;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import persistance.SqlTable;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

/**
 * The class Calculations performs all necessary calculations (returns, volatility, correlations) on a single instrument
 * level and on a portfolio level. Furthermore it performs the calculation of the optimal Portfolio.
 * @author Kevin Reichmuth
 * @version 31.08.2022
 */
public class Calculations {

    //Declare variables
    private SqlTable sqlTable;

    /**
     * CONSTRUCTOR
     */
    public Calculations(){
        sqlTable = Control.getSqlTable();
    }

    /**
     * Method that calculates the simpleReturn and steadyReturn for each instrument for each day in the data-sample.
     * The results are stored in the MySQL-DB in the "metrics" table.
     * @param ticker The ticker of the instrument.
     */
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

    /**
     * Method that calculates the summary-metrics (avgSteadyReturn, avgSimpleReturn, standardDeviation) for each instrument.
     * The results are stored in the MySQL-DB in the "metrics_summary" table.
     * @param ticker The ticker of the instrument.
     */
    public void calcMetricSummary(String ticker){
        ArrayList<Float> simpleReturns = sqlTable.getMetricList("simpleReturn", ticker);
        ArrayList<Float> steadyReturns = sqlTable.getMetricList("steadyReturn",ticker);
        float avgSimpleReturns=0;
        float avgSteadyReturns=0;
        float standardDeviation = 0;

        for(float value : steadyReturns){
            avgSteadyReturns = avgSteadyReturns+value;
        }

        avgSteadyReturns = avgSteadyReturns/steadyReturns.size();
        avgSimpleReturns = (float) (Math.exp(avgSteadyReturns))-1;
        sqlTable.insertMetricSummary(ticker,"avgSteadyReturn",avgSteadyReturns);
        sqlTable.insertMetricSummary(ticker,"avgSimpleReturn",avgSimpleReturns);

        DescriptiveStatistics stats = new DescriptiveStatistics();

        for(float steadyReturn : steadyReturns){
            double value = (double)steadyReturn;
            stats.addValue(value);
        }

        standardDeviation = (float) stats.getStandardDeviation();
        sqlTable.insertMetricSummary(ticker,"standardDeviation",standardDeviation);

    }

    /**
     * Method that calculates the correlations between each instrument.
     * The results are stored in the MySQL-DB in the "metrics_summary" table (metric: Correl-Instrument1-Instrument2).
     * @param portfolio The "current" portfolio.
     */
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

    /**
     * Method that calculates the portfolio-return.
     * The results are stored in the MySQL-DB in the "metrics_summary" table (ticker: PORTFOLIO, metric: portfolioReturn).
     * @param portfolio The "current" portfolio.
     */
    public void calcPortfolioReturn(String portfolio){
        ArrayList<String> tickerList = sqlTable.getPortfolioTickers(portfolio);
        float portfolioReturn = 0;

        for(String value:tickerList){
            float weight = sqlTable.getPortfolioWeight(value,portfolio);
            float instrumentReturn = sqlTable.getMetricSummaryValue(value,"avgSimpleReturn");
            portfolioReturn= portfolioReturn+(weight*instrumentReturn);
            /*if(value!="PORTFOLIO"){
                float weight = sqlTable.getPortfolioWeight(value,portfolio);
                float instrumentReturn = sqlTable.getMetricSummaryValue(value,"avgSimpleReturn");
                portfolioReturn= portfolioReturn+(weight*instrumentReturn);
            }
             */
        }

        sqlTable.insertMetricSummary("PORTFOLIO","portfolioReturn",portfolioReturn);
    }

    /**
     * Method that counts the number of instruments in the portfolio.
     * @param portfolio The "current" portfolio.
     */
    public int countPortfolioInstruments(String portfolio){
        ArrayList<String> tickerList = sqlTable.getPortfolioTickers(portfolio);
        int counter = 0;
        for(String value : tickerList){
            counter++;
            /*if(value!="PORTFOLIO"){
                counter++;
            }
             */
        }
        return counter;
    }

    /**
     * Method that calculates the portfolio-volatility.
     * The results are stored in the MySQL-DB in the "metrics_summary" table (ticker: PORTFOLIO, metric: portfolioVolatility).
     * @param portfolio The "current" portfolio.
     */
    public void calcPortfolioVolatility(String portfolio){
        ArrayList<String> tickerList = sqlTable.getPortfolioTickers(portfolio);
        float portfolioVolatility = 0;
        int countInstruments = countPortfolioInstruments(portfolio);
        double weights[] = new double[countInstruments];
        double varianceCovarianceMatrix[][] = new double[countInstruments][countInstruments];
        int counter1 = 0;
        int counter2 = 0;

        //Write the weights into weights[]
        for (String ticker : tickerList){
            double weight = (double) sqlTable.getPortfolioWeight(ticker, portfolio);
            weights[counter1] = weight;
            counter1++;
        }
        counter1 = 0;

        //Write covariances into varianceCovarianceMatrix[][]
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
        //counter2 = 0;

        //Calculate the volatility with matrices
        RealMatrix matrixWeightColumn = MatrixUtils.createColumnRealMatrix(weights);
        RealMatrix matrixWeightRow = MatrixUtils.createRowRealMatrix(weights);
        RealMatrix matrixVarianceCovariance = MatrixUtils.createRealMatrix(varianceCovarianceMatrix);
        RealMatrix actual1 = matrixWeightRow.multiply(matrixVarianceCovariance).multiply(matrixWeightColumn);
        portfolioVolatility = (float) actual1.getEntry(0,0);
        portfolioVolatility = (float) Math.sqrt(portfolioVolatility);
        sqlTable.insertMetricSummary("PORTFOLIO","portfolioVolatility",portfolioVolatility);
    }

    /**
     * Method that calculates the optimal portfolio.
     * The results (new weights) are stored in the MySQL-DB in the "portfolio" table (portfolio: minRisk or targetReturn).
     * @param portfolio The "current" portfolio.
     * @param condition The condition minRisk=0 or targetReturn=x.
     */
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

        //Write covariances into lagrangeMatrix[][]
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

        //Write returns into lagrangeMatrix[][]
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

        //Write values into conditions[]
        for(int i = 0; i<countInstruments; i++){
            conditions[i] = 0;
        }

        //Calculate new weights in matrices
        conditions[countInstruments] = condition;
        conditions[countInstruments+1]=1;
        RealMatrix matrixConditions = MatrixUtils.createColumnRealMatrix(conditions);
        RealMatrix matrixLagrange = MatrixUtils.createRealMatrix(lagrangeMatrix);
        RealMatrix inverseMatrixLagrange = MatrixUtils.inverse(matrixLagrange);
        RealMatrix actual = inverseMatrixLagrange.multiply(matrixConditions);

        //Store new weights in DB
        counter1=0;
        for(String ticker : tickerList){
            float newWeitght = (float) actual.getEntry(counter1,0);
            sqlTable.insertPortfolio(ticker,newPortfolio,newWeitght);
            counter1++;
        }
    }
}
