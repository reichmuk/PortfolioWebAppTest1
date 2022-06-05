import domain.Calculations;
import domain.Control;
import persistance.SqlTable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Run {

    public static void main(String[] args){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://127.0.0.1:3306/instrumentDB?useSSL=false&allowPublicKeyRetrieval=true";
            String user = "root";
            String password = "Blue_22!";
            Connection conn = DriverManager.getConnection(url,user,password);
            Statement statement = conn.createStatement();
            conn.close();



            Control control = new Control();
            SqlTable sqlTable = Control.getSqlTable();
            Calculations calculations = Control.getCalculations();
            calculations.calcCorrelations("current");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }
}
