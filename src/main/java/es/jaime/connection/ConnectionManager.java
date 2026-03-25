package es.jaime.connection;

import es.jaime.configuration.DatabaseConfiguration;
import es.jaime.connection.pool.AcquireConnectionOptions;
import es.jaime.connection.pool.ConnectionPool;
import es.jaimetruman.ReadQuery;
import es.jaimetruman.WriteQuery;
import lombok.SneakyThrows;

import java.sql.*;
import java.util.EnumSet;
import java.util.List;

public final class ConnectionManager {
    private final ThreadLocal<Connection> connectionThreadLocal;
    private final DatabaseConfiguration configuration;
    private final ConnectionPool connectionPool;

    @SneakyThrows
    public ConnectionManager(DatabaseConfiguration configuration) {
        this.connectionPool = configuration.getConnectionPool();
        this.configuration = configuration;
        this.connectionThreadLocal = new ThreadLocal<>();
    }

    public Connection acquireConnection(EnumSet<AcquireConnectionOptions> options) {
        if(connectionThreadLocal.get() == null){
            connectionThreadLocal.set(connectionPool.acquire(options));
        }

        return connectionThreadLocal.get();
    }

    public void releaseConnection() {
        connectionPool.release(connectionThreadLocal.get());
        connectionThreadLocal.remove();
    }

    public void releaseAllConnections() {
        connectionPool.closeAll();
    }

    public ResultSet sendQuery(ReadQuery query) throws Exception {
        if(configuration.isShowQueries()) System.out.println(query);

        return createStatement().executeQuery(query.toString());
    }

    public ResultSet sendQuery(String query) throws Exception {
        if(configuration.isShowQueries()) System.out.println(query);

        return createStatement().executeQuery(query);
    }

    public void sendUpdate(WriteQuery query) throws Exception {
        if(configuration.isShowQueries()) System.out.println(query);

        createStatement().executeUpdate(query.toString());
    }

    public void sendUpdate(String query) throws Exception {
        if(configuration.isShowQueries()) System.out.println(query);

        createStatement().executeUpdate(query);
    }

    public void sendStatement(String statement) throws Exception {
        if(configuration.isShowQueries()) System.out.println(statement);

        createStatement().execute(statement);
    }

    private Statement createStatement() throws SQLException {
        return connectionPool.acquire(EnumSet.of(AcquireConnectionOptions.DEFAULT_OPTIONS))
                .createStatement();
    }

    @SneakyThrows
    public void runCommands(List<String> commandsToRun){
        if (commandsToRun.isEmpty()) {
            return;
        }

        Statement statement = createStatement();

        for (String command : commandsToRun) {
            if(configuration.isShowQueries()) System.out.println(command);
            statement.execute(command);
        }
    }
}
