package domain;
import java.util.ArrayList;
import constants.Constants;
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
    private static String strategy;
    private YahooApi yahooApi;

    /**
     * CONSTRUCTOR
     */
    public Calculations(){
        sqlTable = Control.getSqlTable();
        yahooApi = Control.getYahooApi();
        strategy = "";
    }

    //------------------------------------------------------------------------------------------------------------------
    // Calculations single instrument
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Method that calculates the simpleReturn and steadyReturn for each instrument for each day in the data-sample.
     * The results are stored in the MySQL-DB in the "metrics" table.
     */
    public void calcSingleReturn(ArrayList<String>tickerList){
        ArrayList<Integer> timeStampList = yahooApi.getTimeStampList();
        ArrayList<Double> simpleReturnList = new ArrayList<>();
        ArrayList<Double> steadyReturnList = new ArrayList<>();

        for(int i = 0; i<tickerList.size(); i++){
            String ticker = tickerList.get(i);
            ArrayList<Double> priceList = sqlTable.getPriceList(Constants.PRICE,ticker);

            for(int j = 0; j< priceList.size(); j++){
                if(j==0){} else{
                    double simpleReturn = (priceList.get(j) - priceList.get(j-1)) / priceList.get(j-1);
                    simpleReturnList.add(simpleReturn);
                    double steadyReturn = Math.log(1+simpleReturn);
                    steadyReturnList.add(steadyReturn);
                }
            }
            sqlTable.addMetricList(ticker,timeStampList,Constants.SIMPLERETURN,simpleReturnList);
            simpleReturnList.clear();
            sqlTable.addMetricList(ticker,timeStampList,Constants.STEADYRETURN,steadyReturnList);
            calcMetricSummary(ticker, steadyReturnList);
            steadyReturnList.clear();
        }
    }

    /**
     * Method that calculates the summary-metrics (avgSteadyReturn, avgSimpleReturn, standardDeviation) for each instrument.
     * The results are stored in the MySQL-DB in the "metrics_summary" table.
     * @param ticker The ticker of the instrument.
     */
    public void calcMetricSummary(String ticker, ArrayList<Double> steadyReturnList){
        double avgSimpleReturns=0;
        double avgSteadyReturns=0;
        double standardDeviation=0;

        for(double value : steadyReturnList){
            avgSteadyReturns = avgSteadyReturns+value;
        }

        avgSteadyReturns = avgSteadyReturns/steadyReturnList.size();
        avgSimpleReturns = (Math.exp(avgSteadyReturns))-1;
        sqlTable.insertMetricSummary(ticker,Constants.AVGSTEADYRETURN,avgSteadyReturns);
        sqlTable.insertMetricSummary(ticker,Constants.AVGSIMPLERETURN,avgSimpleReturns);

        DescriptiveStatistics stats = new DescriptiveStatistics();

        for(double steadyReturn : steadyReturnList){
            double value = steadyReturn;
            stats.addValue(value);
        }

        standardDeviation = stats.getStandardDeviation();
        sqlTable.insertMetricSummary(ticker,Constants.STANDARDDEVIATION,standardDeviation);
    }


    /**
     * Method that calculates the correlations between each instrument.
     * The results are stored in the MySQL-DB in the "metrics_summary" table (metric: Correl-Instrument1-Instrument2).
     */
    public void calcCorrelations(ArrayList<String>tickerList){

        for(String ticker1 : tickerList){
            for(String ticker2 : tickerList){
                String metric = "Correl-"+ticker1+"-"+ticker2;
                ArrayList<Double> returnList1 = sqlTable.getMetricList(Constants.STEADYRETURN,ticker1);
                ArrayList<Double> returnList2 = sqlTable.getMetricList(Constants.STEADYRETURN,ticker2);
                double[] list1 = ArrayUtils.toPrimitive(returnList1.toArray(new Double[0]));
                double[] list2 = ArrayUtils.toPrimitive(returnList2.toArray(new Double[0]));
                double correlation = new PearsonsCorrelation().correlation(list1,list2);
                sqlTable.insertMetricSummary(ticker1,metric,correlation);
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    // Calculations portfolio level
    //------------------------------------------------------------------------------------------------------------------

    public double calcPortfolioValue(ArrayList<Integer> quantityList, ArrayList<Double> latestPriceList){
        double portfolioValue = 0;
        for(int i = 0; i<quantityList.size(); i++){
            int quantity = quantityList.get(i);
            double latestPrice = latestPriceList.get(i);
            portfolioValue = portfolioValue+(quantity*latestPrice);
        }
        return portfolioValue;
    }

    /**
     * Method that calculates the portfolio-return.
     * The results are stored in the MySQL-DB in the "metrics_summary" table (ticker: PORTFOLIO, metric: portfolioReturn).
     * @param portfolio The "current" portfolio.
     */
    public void calcPortfolioReturn(ArrayList<String>tickerList, String portfolio){
        double portfolioReturn = 0;
        String metricName = portfolio+"PortfolioReturn";

        for(String ticker:tickerList){
            double weight = sqlTable.getPortfolioWeight(ticker,portfolio);
            double instrumentReturn = sqlTable.getMetricSummaryValue(ticker,Constants.AVGSIMPLERETURN);
            portfolioReturn= portfolioReturn+(weight*instrumentReturn);
        }

        sqlTable.insertMetricSummary(Constants.PORTFOLIO,metricName,portfolioReturn);
    }

    /**
     * Method that calculates the portfolio-volatility.
     * The results are stored in the MySQL-DB in the "metrics_summary" table (ticker: PORTFOLIO, metric: portfolioVolatility).
     * @param portfolio The "current" portfolio.
     */
    public void calcPortfolioVolatility(ArrayList<String> tickerList, String portfolio){
        double portfolioVolatility = 0;
        int countInstruments = 0;

        for(String count : tickerList){
            countInstruments++;
        }

        double weights[] = new double[countInstruments];
        double varianceCovarianceMatrix[][] = new double[countInstruments][countInstruments];
        int counter1 = 0;
        int counter2 = 0;
        String metricName = portfolio+"PortfolioVolatility";

        //Write the weights into weights[]
        ArrayList<Double> portfolioWeightList = sqlTable.getPortfolioWeightList(portfolio);
        for (double weight : portfolioWeightList){
            weights[counter1] = weight;
            counter1++;
        }
        counter1 = 0;

        for(double weight : weights){
            System.out.println(weight);
        }

        //Write covariances into varianceCovarianceMatrix[][]
        for(String ticker : tickerList){
            double stdev1 = sqlTable.getMetricSummaryValue(ticker,Constants.STANDARDDEVIATION);

            for (String ticker2 : tickerList){
                double stdev2 = sqlTable.getMetricSummaryValue(ticker2,Constants.STANDARDDEVIATION);
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
        sqlTable.insertMetricSummary(Constants.PORTFOLIO,metricName,portfolioVolatility);
    }

    /**
     * Method that calculates the minRisk portfolio.
     * The results (new weights & new quantities) are stored in the MySQL-DB in the "portfolio" table (portfolio: minRisk).
     * @param portfolio The "current" portfolio.
     */
    public ArrayList<Integer> calcMinRiskPortfolio(ArrayList<String> tickerList, String portfolio){
        int countInstruments = countInstruments(tickerList);
        int matrixSize = countInstruments+1;
        double lagrangeMatrix[][] = new double[matrixSize][matrixSize];
        double conditions[] = new double[matrixSize];
        double portfolioValue;
        int counter1 = 0;
        int counter2 = 0;
        int counter3 = 0;
        ArrayList<Double> weightList = new ArrayList<>();
        ArrayList<Integer> quantityList = new ArrayList<>();
        ArrayList<Double> latestPriceList = new ArrayList<>();

        //Write covariances into lagrangeMatrix[][]
        for(String ticker : tickerList){
            double stdev1 = sqlTable.getMetricSummaryValue(ticker,Constants.STANDARDDEVIATION);

            for (String ticker2 : tickerList){
                double stdev2 = sqlTable.getMetricSummaryValue(ticker2,Constants.STANDARDDEVIATION);
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
        counter3 = countInstruments;

        for(String ticker : tickerList){
            lagrangeMatrix[counter1][counter3] = 1;
            lagrangeMatrix[counter3][counter1] = 1;
            counter1++;
        }
        lagrangeMatrix[counter3][counter3] = 0;

        //Write values into conditions[]
        for(int i = 0; i<countInstruments; i++){
            conditions[i] = 0;
        }

        //Calculate new weights in matrices
        conditions[countInstruments]=1;
        RealMatrix matrixConditions = MatrixUtils.createColumnRealMatrix(conditions);
        RealMatrix matrixLagrange = MatrixUtils.createRealMatrix(lagrangeMatrix);
        RealMatrix inverseMatrixLagrange = MatrixUtils.inverse(matrixLagrange);
        RealMatrix actual = inverseMatrixLagrange.multiply(matrixConditions);

        //Calculate new quantities and store new quantities and weights in DB
        latestPriceList = sqlTable.getLatestPriceList(tickerList);
        portfolioValue = sqlTable.getPortfolioValue(Constants.CURRENT);
        counter1=0;
        for (String ticker : tickerList){
            weightList.add(actual.getEntry(counter1,0));
            int quantity = (int) ((portfolioValue * actual.getEntry(counter1,0))/latestPriceList.get(counter1));
            quantityList.add(quantity);
            counter1++;
        }
        sqlTable.insertPortfolio(tickerList,portfolio,quantityList,weightList);
        return quantityList;
    }

    /**
     * Method that calculates the optimal portfolio.
     * The results (new weights & new quantities) are stored in the MySQL-DB in the "portfolio" table (portfolio: targetReturn).
     * @param portfolio The "current" portfolio.
     * @param targetYield The condition targetReturn=x.
     */
    public ArrayList<Integer> calcOptimalPortfolio(ArrayList<String> tickerList, String portfolio, double targetYield){
        int countInstruments = countInstruments(tickerList);
        int matrixSize = countInstruments+2;
        double lagrangeMatrix[][] = new double[matrixSize][matrixSize];
        double conditions[] = new double[matrixSize];
        double portfolioValue;
        int counter1 = 0;
        int counter2 = 0;
        int counter3 = 0;
        ArrayList<Double> weightList = new ArrayList<>();
        ArrayList<Integer> quantityList = new ArrayList<>();
        ArrayList<Double> latestPriceList = new ArrayList<>();


        //Write covariances into lagrangeMatrix[][]
        for(String ticker : tickerList){
            double stdev1 = sqlTable.getMetricSummaryValue(ticker,Constants.STANDARDDEVIATION);

            for (String ticker2 : tickerList){
                double stdev2 = sqlTable.getMetricSummaryValue(ticker2,Constants.STANDARDDEVIATION);
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
            double simpleReturn = sqlTable.getMetricSummaryValue(ticker,Constants.AVGSIMPLERETURN);
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
        conditions[countInstruments] = targetYield;
        conditions[countInstruments+1]=1;
        RealMatrix matrixConditions = MatrixUtils.createColumnRealMatrix(conditions);
        RealMatrix matrixLagrange = MatrixUtils.createRealMatrix(lagrangeMatrix);
        RealMatrix inverseMatrixLagrange = MatrixUtils.inverse(matrixLagrange);
        RealMatrix actual = inverseMatrixLagrange.multiply(matrixConditions);


        //Calculate new quantities and store new quantities and weights in DB
        latestPriceList = sqlTable.getLatestPriceList(tickerList);
        portfolioValue = sqlTable.getPortfolioValue(Constants.CURRENT);
        counter1=0;
        for (String ticker : tickerList){
            weightList.add(actual.getEntry(counter1,0));
            int quantity = (int) ((portfolioValue * actual.getEntry(counter1,0))/latestPriceList.get(counter1));
            quantityList.add(quantity);
            counter1++;
        }
        sqlTable.insertPortfolio(tickerList,portfolio,quantityList,weightList);
        return quantityList;
    }

    //------------------------------------------------------------------------------------------------------------------
    // Support methods
    //------------------------------------------------------------------------------------------------------------------


    /**
     * Method that counts the number of instruments in the portfolio.
     */
    public int countInstruments(ArrayList<String> tickerList){
        int counter = 0;

        for(String value : tickerList){
            counter++;
        }

        return counter;
    }

    /**
     * SETTER - Set variable strategy.
     * @param strategy String
     */
    public void setStrategy(String strategy){
        this.strategy=strategy;
    }

    /**
     * GETTER - Get variable strategy.
     * @return String strategy
     */
    public String getStrategy(){
        return strategy;
    }
}