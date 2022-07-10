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
     * Method that calculates the portfolioValue of the respective portfolio.
     * The result is stored in the MySQL-DB "metrics_summary" table.
     * @param portfolio The "current" portfolio.
     */
    public void calcPortfolioValue(String portfolio){
        double portfolioValue = 0;
        ArrayList<String> instruments = sqlTable.getPortfolioTickers(portfolio);

        for(String ticker:instruments){
            double quantity = sqlTable.getPortfolioQuantity(ticker,portfolio);
            double price = sqlTable.getLatestPrice(ticker);
            portfolioValue = portfolioValue + (quantity*price);
        }
        sqlTable.insertMetricSummary("PORTFOLIO","portfolioValue",portfolioValue);
    }

    /**
     * Method that calculates the portfolioQuantity based on the give weight.
     * @param weight The weight of the respective instrument in the portfolio.
     * @return returns the instrumentQuantity
     */
    public int calcInstrumentQuantity(double weight, double price){
        double portfolioValue = sqlTable.getPortfolioValue();
        double instrumentQuantity = (portfolioValue*weight)/price;
        return (int) instrumentQuantity;
    }


    /**
     * Method that calculates the simpleReturn and steadyReturn for each instrument for each day in the data-sample.
     * The results are stored in the MySQL-DB in the "metrics" table.
     * @param ticker The ticker of the instrument.
     */
    public void calcSingleReturn(String ticker){
        ArrayList<Double> prices = sqlTable.getPriceList("price",ticker);
        ArrayList<Double> timeStamps = sqlTable.getPriceList("time_stamp",ticker);

        for(int i = 0; i< prices.size(); i++){
            if(i==0){} else{
                double simpleReturn = (prices.get(i) - prices.get(i-1)) / prices.get(i-1);
                double steadyReturn = Math.log(1+simpleReturn);
                double timeStamp = timeStamps.get(i);
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
        ArrayList<Double> simpleReturns = sqlTable.getMetricList("simpleReturn", ticker);
        ArrayList<Double> steadyReturns = sqlTable.getMetricList("steadyReturn",ticker);
        double avgSimpleReturns=0;
        double avgSteadyReturns=0;
        double standardDeviation = 0;

        for(double value : steadyReturns){
            avgSteadyReturns = avgSteadyReturns+value;
        }

        avgSteadyReturns = avgSteadyReturns/steadyReturns.size();
        avgSimpleReturns = (Math.exp(avgSteadyReturns))-1;
        sqlTable.insertMetricSummary(ticker,"avgSteadyReturn",avgSteadyReturns);
        sqlTable.insertMetricSummary(ticker,"avgSimpleReturn",avgSimpleReturns);

        DescriptiveStatistics stats = new DescriptiveStatistics();

        for(double steadyReturn : steadyReturns){
            double value = steadyReturn;
            stats.addValue(value);
        }

        standardDeviation = stats.getStandardDeviation();
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
                ArrayList<Double> returnList1 = sqlTable.getMetricList("steadyReturn",ticker1);
                ArrayList<Double> returnList2 = sqlTable.getMetricList("steadyReturn",ticker2);
                double[] list1 = ArrayUtils.toPrimitive(returnList1.toArray(new Double[0]));
                double[] list2 = ArrayUtils.toPrimitive(returnList2.toArray(new Double[0]));
                double correlation = new PearsonsCorrelation().correlation(list1,list2);
                sqlTable.insertMetricSummary(ticker1,metric,correlation);
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
        double portfolioReturn = 0;

        for(String value:tickerList){
            double weight = sqlTable.getPortfolioWeight(value,portfolio);
            double instrumentReturn = sqlTable.getMetricSummaryValue(value,"avgSimpleReturn");
            portfolioReturn= portfolioReturn+(weight*instrumentReturn);
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
        double portfolioVolatility = 0;
        int countInstruments = countPortfolioInstruments(portfolio);
        double weights[] = new double[countInstruments];
        double varianceCovarianceMatrix[][] = new double[countInstruments][countInstruments];
        int counter1 = 0;
        int counter2 = 0;

        //Write the weights into weights[]
        for (String ticker : tickerList){
            double weight = sqlTable.getPortfolioWeight(ticker, portfolio);
            weights[counter1] = weight;
            counter1++;
        }
        counter1 = 0;

        //Write covariances into varianceCovarianceMatrix[][]
        for(String ticker : tickerList){
            double stdev1 = sqlTable.getMetricSummaryValue(ticker,"standardDeviation");

            for (String ticker2 : tickerList){
                double stdev2 = sqlTable.getMetricSummaryValue(ticker2,"standardDeviation");
                String correlationString = "Correl-"+ticker+"-"+ticker2;
                double correlation = sqlTable.getMetricSummaryValue(ticker,correlationString);
                double varianceCovariance = stdev1*stdev2*correlation;
                varianceCovarianceMatrix[counter1][counter2]=varianceCovariance;
                counter1++;
            }
            counter1=0;
            counter2++;
        }

        //Calculate the volatility with matrices
        RealMatrix matrixWeightColumn = MatrixUtils.createColumnRealMatrix(weights);
        RealMatrix matrixWeightRow = MatrixUtils.createRowRealMatrix(weights);
        RealMatrix matrixVarianceCovariance = MatrixUtils.createRealMatrix(varianceCovarianceMatrix);
        RealMatrix actual1 = matrixWeightRow.multiply(matrixVarianceCovariance).multiply(matrixWeightColumn);
        portfolioVolatility = actual1.getEntry(0,0);
        portfolioVolatility = Math.sqrt(portfolioVolatility);
        sqlTable.insertMetricSummary("PORTFOLIO","portfolioVolatility",portfolioVolatility);
    }

    /**
     * Method that calculates the optimal portfolio.
     * The results (new weights & new quantities) are stored in the MySQL-DB in the "portfolio" table (portfolio: minRisk or targetReturn).
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
            double stdev1 = sqlTable.getMetricSummaryValue(ticker,"standardDeviation");

            for (String ticker2 : tickerList){
                double stdev2 = sqlTable.getMetricSummaryValue(ticker2,"standardDeviation");
                String correlationString = "Correl-"+ticker+"-"+ticker2;
                double correlation = sqlTable.getMetricSummaryValue(ticker,correlationString);
                double varianceCovariance = stdev1*stdev2*correlation;
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
            double simpleReturn = sqlTable.getMetricSummaryValue(ticker,"avgSimpleReturn");
            lagrangeMatrix[counter1][counter2] = simpleReturn;
            lagrangeMatrix[counter2][counter1] = simpleReturn;
            lagrangeMatrix[counter1][counter3] = 1;
            lagrangeMatrix[counter3][counter1] = 1;
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
            double newWeitght = actual.getEntry(counter1,0);
            double price = sqlTable.getLatestPrice(ticker);
            int newQuantity = calcInstrumentQuantity(newWeitght,price);
            sqlTable.insertPortfolio(ticker,newPortfolio,newQuantity,newWeitght);
            counter1++;
        }
    }
}
