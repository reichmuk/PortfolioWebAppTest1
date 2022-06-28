package viewmodel;

import domain.Control;
import persistance.SqlTable;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "ResetServlet", value = "/ResetServlet")
public class ResetServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SqlTable sqlTable = Control.getSqlTable();
        sqlTable.resetTable("prices");
        sqlTable.resetTable("metrics");
        sqlTable.resetTable("metrics_summary");
        sqlTable.resetTable("portfolio");
        System.out.println("RESET hat funktioniert");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}