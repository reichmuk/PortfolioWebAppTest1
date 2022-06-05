import domain.Calculations;
import domain.Control;
import domain.YahooApi;
import persistance.SqlTable;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@WebServlet(name = "MyServlet", value = "/MyServlet")
public class MyServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter writer = response.getWriter()) {
            writer.println("<!DOCTYPE html><html>");
            writer.println("<head>");
            writer.println("<meta charset=\"UTF-8\" />");
            writer.println("<title>MyServlet.java:doGet(): Servlet code!</title>");
            writer.println("</head>");
            writer.println("<body>");

            writer.println("<h1>Portfolio Web App Initialized!</h1>");

            writer.println("</body>");
            writer.println("</html>");
        }

        try {
            Control control = new Control();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }



        /*try{


            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://127.0.0.1:3306/instrumentDB?useSSL=false&allowPublicKeyRetrieval=true";
            String user = "root";
            String password = "Blue_22!";
            Connection conn = DriverManager.getConnection(url,user,password);
            Statement statement = conn.createStatement();
            statement.execute("INSERT INTO instruments VALUES ('del', 'CHF', 'Delia', 'Switzerland')");
            System.out.println("Connection created");
            conn.close();

        }

        catch (ClassNotFoundException e){
            e.printStackTrace();
            System.out.println("Fehler1");
            System.out.println(e.getMessage());
        }
        catch (SQLException e){
            e.printStackTrace();
            System.out.println("Fehler2");
            System.out.println("SQLException:" + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
        }*/

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            Control control = new Control();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String instrument1 = request.getParameter("instrument1").toString();
        String instrument2 = request.getParameter("instrument2").toString();
        String instrument3 = request.getParameter("instrument3").toString();

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
        calculations.calcCorrelations("current");

        /*
        PrintWriter out = response.getWriter();
        out.println(instrument1);
        out.println(instrument2);
        out.println(instrument3);
        */
    }
}
