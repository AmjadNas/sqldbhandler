package test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBManager {

    public Properties properties;

    private static DBManager instance;

    private DBManager() {

        try (FileInputStream fis = new FileInputStream("db.config")) {
            properties = new Properties();

            properties.load(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static DBManager getInstance() {
        if (instance == null) {
            instance = new DBManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {

        String url = properties.getProperty("url");

        return DriverManager.getConnection(url, properties.getProperty("user"), properties.getProperty("pass"));
    }
}
