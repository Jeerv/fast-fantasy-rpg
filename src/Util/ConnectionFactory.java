package Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/ff_rpg?useSSL=false&serverTimezone=UTC";
    private static final String DEFAULT_USER = "app_ff";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = System.getenv().getOrDefault("DB_URL", DEFAULT_URL);
            String user = System.getenv().getOrDefault("DB_USER", DEFAULT_USER);
            String password = System.getenv().getOrDefault("DB_PASSWORD", "");
            return DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver do MySQL não encontrado!", e);
        }
    }
}
