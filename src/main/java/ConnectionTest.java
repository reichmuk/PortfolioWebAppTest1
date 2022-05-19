import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionTest {

    public static void main(String[] args){

        Connection conn = null;
        String url = "jdbc:mysql://127.0.0.1:3306/instrumentDB";
        String user = "root";
        String password = "Blue_22!";
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e){
            e.printStackTrace();
        }

        try {
            Statement statement = conn.createStatement();
            statement.execute("INSERT INTO instruments VALUES (4, 'CH0024608827', 'PGHN.SW', 'CHF', 'Partners Group', 'Switzerland')");
        }catch (SQLException e){
            e.printStackTrace();
        }



    }

}
