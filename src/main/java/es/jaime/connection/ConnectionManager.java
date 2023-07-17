package es.jaime.connection;

import es.jaime.configuration.DatabaseConfiguration;
import es.jaime.connection.pool.ConnectionPool;
import es.jaimetruman.ReadQuery;
import es.jaimetruman.WriteQuery;
import lombok.SneakyThrows;

import java.sql.*;
import java.util.List;

public final class ConnectionManager {
    private final ThreadLocal<Connection> connectionThreadLocal;
    private final DatabaseConfiguration configuration;
    private final ConnectionPool connectionPool;

    @SneakyThrows
    public ConnectionManager(DatabaseConfiguration configuration) {
        this.connectionPool = configuration.connectionPool();
        this.configuration = configuration;
        this.connectionThreadLocal = new ThreadLocal<>();
    }

    public Connection acquireConnection() {
        if(connectionThreadLocal.get() == null){
            connectionThreadLocal.set(connectionPool.acquire());
        }

        return connectionThreadLocal.get();
    }

    public void releaseConnection() {
        connectionPool.release(connectionThreadLocal.get());
        connectionThreadLocal.remove();
    }

    public void releaseAllConnections() {
        connectionPool.releaseAll();
    }

    public ResultSet sendQuery(ReadQuery query) throws Exception {
        if(configuration.showQueries()) System.out.println(query);

        return createStatement().executeQuery(query.toString());
    }

    public ResultSet sendQuery(String query) throws Exception {
        if(configuration.showQueries()) System.out.println(query);

        return createStatement().executeQuery(query);
    }

    public void sendUpdate(WriteQuery query) throws Exception {
        if(configuration.showQueries()) System.out.println(query);

        createStatement().executeUpdate(query.toString());
    }

    public void sendUpdate(String query) throws Exception {
        if(configuration.showQueries()) System.out.println(query);

        createStatement().executeUpdate(query);
    }

    public void sendStatement(String statement) throws Exception {
        if(configuration.showQueries()) System.out.println(statement);

        createStatement().execute(statement);
    }

    private Statement createStatement() throws SQLException {
        return connectionPool.acquire()
                .createStatement();
    }

    @SneakyThrows
    public void runCommands(List<String> commandsToRun){
        if(commandsToRun.isEmpty()) {
            return;
        }

        Statement statement = createStatement();

        for (String command : commandsToRun) {
            if(configuration.showQueries()) System.out.println(command);
            statement.execute(command);
        }
    }
}
