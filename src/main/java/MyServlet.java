import domain.Control;

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

    }
}
