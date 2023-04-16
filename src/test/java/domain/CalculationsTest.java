package domain;
import constants.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;
import persistance.SqlTable;


public class CalculationsTest {

    private Control control;
    private SqlTable sqlTable;
    private Calculations calculations;

    /**
     * Initial set-up for each test-method.
     * @throws Exception exception if test fails
     */
    @BeforeEach
    public void setUp() throws Exception{
        control = new Control();
        sqlTable = Control.getSqlTable();
        calculations = Control.getCalculations();
        sqlTable.resetTable("portfolio");
        sqlTable.resetTable("prices");
        sqlTable.resetTable("metrics");
        sqlTable.resetTable("metrics_summary");
    }

    /**
     * Tests all the methods in the Calculations-Class.
     * @throws Exception exception if test fails
     */
    @Test
    public void testCalculations() throws Exception{


        // SET portfolio
        /**
        sqlTable.insertPortfolio("ABBN.SW", Constants.CURRENT,100,0.292239);
        sqlTable.insertPortfolio("NESN.SW", Constants.CURRENT,30,0.369908);
        sqlTable.insertPortfolio("UBSG.SW", Constants.CURRENT,200,0.337852);
         */

        /**
        // CALC portfolio values
        //calculations.calcPortfolioValue(Constants.CURRENT);
        calculations.calcPortfolioReturn(Constants.CURRENT);
        calculations.calcCorrelations(Constants.CURRENT);
        calculations.calcPortfolioVolatility(Constants.CURRENT);

        // CALC currentPortfolio
        System.out.println("current-Portfolio:");
        System.out.println(sqlTable.getPortfolioValue(Constants.CURRENT));
        assertEquals(sqlTable.getPortfolioValue(Constants.CURRENT),9427.20,1);
        System.out.println(sqlTable.getMetricSummaryValue(Constants.PORTFOLIO,Constants.CURRENTPORTFOLIORETURN));
        assertEquals(sqlTable.getMetricSummaryValue(Constants.PORTFOLIO,Constants.CURRENTPORTFOLIORETURN),0.004327,0.0001);
        System.out.println(sqlTable.getMetricSummaryValue(Constants.PORTFOLIO,Constants.CURRENTPORTFOLIOVOLATILITY));
        assertEquals(sqlTable.getMetricSummaryValue(Constants.PORTFOLIO,Constants.CURRENTPORTFOLIOVOLATILITY),0.008699,0.0001);

        // CALC minRiskPortfolio
        System.out.println("minRisk-Portfolio:");
        calculations.calcMinRiskPortfolio(Constants.MINRISK);
        //calculations.calcPortfolioValue(Constants.MINRISK);
        calculations.calcPortfolioReturn(Constants.MINRISK);
        calculations.calcPortfolioVolatility(Constants.MINRISK);
        System.out.println(sqlTable.getPortfolioValue(Constants.MINRISK));
        assertEquals(sqlTable.getPortfolioValue(Constants.MINRISK),9415.90,1);
        System.out.println(sqlTable.getMetricSummaryValue(Constants.PORTFOLIO,Constants.MINRISKPORTFOLIORETURN));
        assertEquals(sqlTable.getMetricSummaryValue(Constants.PORTFOLIO,Constants.MINRISKPORTFOLIORETURN),0.005536,0.0001);
        System.out.println(sqlTable.getMetricSummaryValue(Constants.PORTFOLIO,Constants.MINRISKPORTFOLIOVOLATILITY));
        assertEquals(sqlTable.getMetricSummaryValue(Constants.PORTFOLIO,Constants.MINRISKPORTFOLIOVOLATILITY),0.008612,0.0001);

        // CALC targetReturnPortfolio
        System.out.println("targetReturn-Portfolio:");
        calculations.calcOptimalPortfolio(Constants.TARGETRETURN,0.005);
        //calculations.calcPortfolioValue(Constants.TARGETRETURN);
        calculations.calcPortfolioReturn(Constants.TARGETRETURN);
        calculations.calcPortfolioVolatility(Constants.TARGETRETURN);
        System.out.println(sqlTable.getPortfolioValue(Constants.TARGETRETURN));
        assertEquals(sqlTable.getPortfolioValue(Constants.TARGETRETURN),9352.86,1);
        System.out.println(sqlTable.getMetricSummaryValue(Constants.PORTFOLIO,Constants.TARGETRETURNPORTFOLIORETURN));
        assertEquals(sqlTable.getMetricSummaryValue(Constants.PORTFOLIO,Constants.TARGETRETURNPORTFOLIORETURN),0.005,0.0001);
        System.out.println(sqlTable.getMetricSummaryValue(Constants.PORTFOLIO,Constants.TARGETRETURNPORTFOLIOVOLATILITY));
        assertEquals(sqlTable.getMetricSummaryValue(Constants.PORTFOLIO,Constants.TARGETRETURNPORTFOLIOVOLATILITY),0.008624,0.0001);
         */
    }
}
