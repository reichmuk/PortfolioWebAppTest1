package viewmodel;
import domain.Control;
import persistance.SqlTable;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

/**
 * The ResetServlet is launched with the reset-button on the index.jsp page.
 * The Servlet will reset the MySQL-DB and all the entries on the Start-Page (index.jsp).
 * @author Kevin Reichmuth
 * @version 31.08.2022
 */
@WebServlet(name = "ResetServlet", value = "/ResetServlet")
public class ResetServlet extends HttpServlet {

    /**
     * doGet-Method performs the tasks mentioned in the description at the start of this Servlet.
     * @param request The Http-Servlet-Request
     * @param response The Http-Servlet-Response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Control control = new Control();
            SqlTable sqlTable = Control.getSqlTable();
            sqlTable.resetTable("prices");
            sqlTable.resetTable("metrics");
            sqlTable.resetTable("metrics_summary");
            sqlTable.resetTable("portfolio");
            System.out.println("RESET hat funktioniert");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        response.sendRedirect("index.jsp");
    }

    /**
     * doPost Method is not used in this Servlet!
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}