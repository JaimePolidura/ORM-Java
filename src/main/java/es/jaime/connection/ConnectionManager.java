package es.jaime.connection;

import es.jaime.configuration.DatabaseConfiguration;
import es.jaimetruman.ReadQuery;
import es.jaimetruman.WriteQuery;
import lombok.SneakyThrows;

import java.sql.*;
import java.util.List;

public final class ConnectionManager {
    private final DatabaseConfiguration configuration;
    private Connection connection;

    @SneakyThrows
    public ConnectionManager(DatabaseConfiguration configuration) {
        this.configuration = configuration;
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

    @SneakyThrows
    public void runCommands(List<String> commandsToRun){
        if(commandsToRun.isEmpty()) {
            return;
        }

        Statement statement = connection.createStatement();

        for (String command : commandsToRun) {
            if(configuration.showQueries()) System.out.println(command);
            statement.execute(command);
        }
    }

    public ResultSet sendQuery(ReadQuery query) throws Exception {
        if(configuration.showQueries()) System.out.println(query);

        return this.connection.createStatement().executeQuery(query.toString());
    }

    public ResultSet sendQuery(String query) throws Exception {
        if(configuration.showQueries()) System.out.println(query);

        return this.connection.createStatement().executeQuery(query);
    }

    public void sendUpdate(WriteQuery query) throws Exception {
        if(configuration.showQueries()) System.out.println(query);

        this.connection.createStatement().executeUpdate(query.toString());
    }

    public void sendUpdate(String query) throws Exception {
        if(configuration.showQueries()) System.out.println(query);

        this.connection.createStatement().executeUpdate(query);
    }

    public void sendStatement(String statement) throws Exception {
        if(configuration.showQueries()) System.out.println(statement);

        this.getConnection().createStatement().execute(statement);
    }

    public void disconnect() throws SQLException {
        this.connection.close();
    }

    public void reconnect() throws Exception {
        this.connection = this.connect();
    }

    public Connection connect() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        return DriverManager.getConnection(this.configuration.url());
    }
}
