package viewmodel;

import domain.Calculations;
import domain.Control;
import domain.YahooApi;
import persistance.SqlTable;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "RunServlet", value = "/RunServlet")
public class RunServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SqlTable sqlTable = Control.getSqlTable();
        sqlTable.insertPortfolio("UBSG.SW","current", 0.4f);
        sqlTable.insertPortfolio("ABBN.SW","current", 0.35f);
        sqlTable.insertPortfolio("ZURN.SW","current",0.25f);

        YahooApi yahooApi = Control.getYahooApi();
        yahooApi.priceImport("UBSG.SW");
        yahooApi.priceImport("ABBN.SW");
        yahooApi.priceImport("ZURN.SW");

        Calculations calculations = Control.getCalculations();
        calculations.calcSingleReturn("UBSG.SW");
        calculations.calcSingleReturn("ABBN.SW");
        calculations.calcSingleReturn("ZURN.SW");
        calculations.calcMetricSummary("UBSG.SW");
        calculations.calcMetricSummary("ABBN.SW");
        calculations.calcMetricSummary("ZURN.SW");
        calculations.calcPortfolioReturn("current");

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
