import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexiuneDB {
        private static String url = "jdbc:mysql://localhost:3306/platforma_recrutare";
        private static String user = "root";
        private static String password = "";

        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(url, user, password);
        }
}