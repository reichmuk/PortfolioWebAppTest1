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
        Calculations calculations = Control.getCalculations();
        //calculations.calcPortfolioReturn("current");
        //calculations.calcPortfolioVolatility("current");
        calculations.calcOptimalPortfolio("current",0);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
