package es.jaime.connection;

import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionManager {
    private final String url;
    private Connection connection;

    @SneakyThrows
    public ConnectionManager(String url) {
        this.url = url;
        this.connection = this.connect();
    }

    @SneakyThrows
    public boolean isConnected() {
        return this.connection != null && !this.connection.isClosed();
    }

    public Connection getConnection() throws Exception {
        if(!this.isConnected())
            this.connection = this.connect();

        return this.connection;
    }

    public void disconnect() throws SQLException {
        this.connection.close();
    }

    public void reconnect() throws Exception {
        this.connection = this.connect();
    }

    public Connection connect() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        return DriverManager.getConnection(this.url);
    }
}