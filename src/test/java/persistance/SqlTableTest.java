package persistance;
import constants.Constants;
import domain.Control;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.Assert.*;

public class SqlTableTest {
    private Control control;
    private SqlTable sqlTable;

    /**
     * Initial set-up for each test-method.
     * @throws Exception exception if test fails
     */
    @BeforeEach
    public void setUp() throws Exception{
        control = new Control();
        sqlTable = Control.getSqlTable();
        sqlTable.resetTable("portfolio");
        sqlTable.resetTable("prices");
        sqlTable.resetTable("metrics");
        sqlTable.resetTable("metrics_summary");
    }

    /**
     * Tests method getInstrumentTicker() in the SqlTable-class.
     * @throws Exception exception if test fails
     */

    @Test
    public void testGetInstrumentTicker() throws Exception{
        /**
        assertEquals(sqlTable.getInstrumentTicker("ABB Ltd"),"ABBN.SW");
        assertEquals(sqlTable.getInstrumentTicker("UBS Group AG"),"UBSG.SW");
        assertEquals(sqlTable.getInstrumentTicker("Roche Holding AG"),"ROG.SW");
         */
    }

    @Test
    public void testGetInstrumentData() throws Exception{
        /**
        assertEquals(sqlTable.getInstrumentData(Constants.CCY,Constants.TICKER,"ABBN.SW"),"CHF");
         */
    }


    /**
     * Tests method insertMetric() and getMetricList() in the SqlTable-class.
     * @throws Exception exception if test fails
     */
    @Test
    public void testMetric() throws Exception{
        /**
        sqlTable.insertMetric("ABBN.SW",1652857200,Constants.SIMPLERETURN,0.01);
        sqlTable.insertMetric("ABBN.SW",1652943600,Constants.SIMPLERETURN,0.02);
        sqlTable.insertMetric("ABBN.SW",1653030000,Constants.SIMPLERETURN,0.03);
        ArrayList<Double> metricList = sqlTable.getMetricList(Constants.SIMPLERETURN,"ABBN.SW");
        assertEquals(metricList.size(),3);
        assertEquals(metricList.get(0),0.01,0);
         */
    }

    /**
     * Tests method insertMetricSummary() and getMetricSummaryValue() in the SqlTable-class.
     * @throws Exception exception if test fails
     */
    @Test
    public void testMetricSummary() throws Exception{
        /**
        sqlTable.insertMetricSummary("ABBN.SW",Constants.STANDARDDEVIATION,0.01);
        sqlTable.insertMetricSummary("ABBN.SW", Constants.AVGSIMPLERETURN, 0.02);
        sqlTable.insertMetricSummary("ABBN.SW", Constants.AVGSTEADYRETURN, 0.03);
        assertEquals(sqlTable.getMetricSummaryValue("ABBN.SW",Constants.STANDARDDEVIATION),0.01,0);
        assertEquals(sqlTable.getMetricSummaryValue("ABBN.SW",Constants.AVGSIMPLERETURN),0.02,0);
         */
    }

    /**
     * Tests method insertPortfolio(), getPortfolioQuantity() and getPortfolioWeight in the SqlTable-class.
     * @throws Exception exception if test fails
     */
    @Test
    public void testPortfolio() throws Exception{
        /**
        sqlTable.insertPortfolio("ABBN.SW",Constants.CURRENT,10,0.1);
        sqlTable.insertPortfolio("UBSG.SW",Constants.CURRENT,20,0.5);
        sqlTable.insertPortfolio("ROG.SW",Constants.CURRENT,5,0.4);
        ArrayList<String> tickerList = sqlTable.getPortfolioTickers(Constants.CURRENT);
        assertEquals(tickerList.size(),3);
        assertEquals(tickerList.get(0),"ABBN.SW");
        assertEquals(sqlTable.getPortfolioQuantity("ABBN.SW",Constants.CURRENT),10,0);
        assertEquals(sqlTable.getPortfolioWeight("UBSG.SW",Constants.CURRENT),0.5,0);
         */
    }

    /**
     * Tests method insertMetricSummary() and getPortfolioValue() in the SqlTable-class.
     * @throws Exception exception if test fails
     */
    @Test
    public void testPortfolioValue() throws Exception{
        /**
        sqlTable.insertMetricSummary(Constants.PORTFOLIO,Constants.CURRENTPORTFOLIOVALUE,10000);
        assertEquals(sqlTable.getPortfolioValue(Constants.CURRENT),10000,0);
         */
    }
}
