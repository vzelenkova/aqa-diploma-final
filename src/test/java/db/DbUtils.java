package db;

import io.qameta.allure.Step;
import java.sql.*;
import java.util.concurrent.atomic.AtomicReference;

public class DbUtils {

    private static final AtomicReference<Config> configRef = new AtomicReference<>();

    private static Config getConfig() {
        return configRef.updateAndGet(config -> config != null ? config : buildConfig());
    }

    private static Config buildConfig() {
        String dbType = System.getProperty("db.type",
                System.getenv("DB_TYPE") != null ? System.getenv("DB_TYPE") : "postgres");

        System.out.println("DbUtils DB_TYPE: '" + dbType + "'");
        System.out.println("DbUtils SPRING_DATASOURCE_URL: '" + System.getenv("SPRING_DATASOURCE_URL") + "'");

        String url;
        switch (dbType) {
            case "mysql":
                url = System.getProperty("db.url",
                        System.getenv("SPRING_DATASOURCE_URL") != null ?
                                System.getenv("SPRING_DATASOURCE_URL") :
                                "jdbc:mysql://mysql:3306/app?allowPublicKeyRetrieval=true&useSSL=false&connectTimeout=10000");
                break;
            case "postgres":
            default:
                url = System.getProperty("db.url",
                        System.getenv("SPRING_DATASOURCE_URL") != null ?
                                System.getenv("SPRING_DATASOURCE_URL") :
                                "jdbc:postgresql://postgres:5432/app");
                break;
        }

        String user = System.getProperty("db.user",
                System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "app");
        String password = System.getProperty("db.password",
                System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "pass");

        return new Config(url, user, password);
    }

    private static class Config {
        final String url;
        final String user;
        final String password;

        Config(String url, String user, String password) {
            this.url = url;
            this.user = user;
            this.password = password;
        }
    }

    @Step("Очистка таблиц")
    public static void clearTables() throws SQLException {
        Config config = getConfig();
        try (Connection conn = DriverManager.getConnection(config.url, config.user, config.password);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM payment_entity");
            stmt.executeUpdate("DELETE FROM credit_request_entity");
            stmt.executeUpdate("DELETE FROM order_entity");
        }
    }

    @Step("Получение значения колонки \"{column}\" из таблицы \"{table}\"")
    public static String getValueFromDatabase(String column, String table) throws SQLException {
        Config config = getConfig();
        String query = String.format(
                "SELECT %s FROM %s ORDER BY created DESC LIMIT 1", column, table
        );
        try (Connection conn = DriverManager.getConnection(config.url, config.user, config.password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) return rs.getString(column);
        }
        return null;
    }
}