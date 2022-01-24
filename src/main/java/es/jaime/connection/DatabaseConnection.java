package es.jaime.connection;

import es.jaimetruman.ReadQuery;
import es.jaimetruman.WriteQuery;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class DatabaseConnection {
    private Connection connection;

    public DatabaseConnection() {
        this.connect();
    }

    @SneakyThrows
    public void connect(){
        if(connection == null || connection.isClosed()){
            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection(url());
        }
    }

    protected abstract String url();

    public ResultSet sendQuery(ReadQuery query) throws SQLException {
        return connection.createStatement().executeQuery(query.toString());
    }

    public void sendUpdate(WriteQuery query) throws SQLException {
        connection.createStatement().executeUpdate(query.toString());
    }

    public void sendUpdate(String query) throws SQLException {
        connection.createStatement().executeUpdate(query);
    }
}
