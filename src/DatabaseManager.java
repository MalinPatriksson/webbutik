import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager {
    public static Connection getConnection() throws IOException {
        Properties p = new Properties();
        p.load(new FileInputStream("src/settings.properties"));

        String URL = p.getProperty("connectionString");
        String NAME = p.getProperty("name");
        String PASSWORD = p.getProperty("password");

        try {
            return DriverManager.getConnection(URL, NAME, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to the database " + e.getMessage());
        }
    }
}