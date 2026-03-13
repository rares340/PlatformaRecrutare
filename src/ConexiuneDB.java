import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexiuneDB {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/platforma_recrutare";
        String user = "root";
        String password = "";

        try (Connection connection = DriverManager.getConnection(url,user,password)){
            System.out.println("Conexiune reusita");
        } catch (SQLException e){
            System.out.println("Eroare");
            e.printStackTrace();
        }
    }
}