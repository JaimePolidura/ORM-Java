package es.jaime.connection;

import es.jaime.configuration.DatabaseConfiguration;
import es.jaime.connection.pool.AcquireConnectionOption;
import es.jaime.connection.pool.ConnectionPool;
import es.jaime.javaddd.domain.exceptions.IllegalState;
import es.jaimetruman.ReadQuery;
import es.jaimetruman.WriteQuery;
import lombok.SneakyThrows;

import java.sql.*;
import java.util.List;

public final class ConnectionManager {
    private final ThreadLocal<Connection> connectionThreadLocal;
    private final ThreadLocal<Boolean> transactionInProgress;
    private final DatabaseConfiguration configuration;
    private final ConnectionPool connectionPool;

    @SneakyThrows
    public ConnectionManager(DatabaseConfiguration configuration) {
        this.connectionPool = configuration.getConnectionPool();
        this.configuration = configuration;
        this.connectionThreadLocal = new ThreadLocal<>();
        this.transactionInProgress = ThreadLocal.withInitial(() -> false);
    }

    public ResultSet sendQuery(ReadQuery query) throws Exception {
        checkTransactionInProgress();

        if(configuration.isShowQueries()) System.out.println(query);

        return createStatement().executeQuery(query.toString());
    }

    public ResultSet sendQuery(String query) throws Exception {
        checkTransactionInProgress();

        if(configuration.isShowQueries()) System.out.println(query);

        return createStatement().executeQuery(query);
    }

    public void sendUpdate(WriteQuery query) throws Exception {
        checkTransactionInProgress();

        if(configuration.isShowQueries()) System.out.println(query);

        createStatement().executeUpdate(query.toString());
    }

    public void sendUpdate(String query) throws Exception {
        checkTransactionInProgress();

        if(configuration.isShowQueries()) System.out.println(query);

        createStatement().executeUpdate(query);
    }

    public void sendStatement(String statement) throws Exception {
        checkTransactionInProgress();

        if(configuration.isShowQueries()) System.out.println(statement);

        createStatement().execute(statement);
    }

    @SneakyThrows
    public void runCommands(List<String> commandsToRun){
        checkTransactionInProgress();

        if (commandsToRun.isEmpty()) {
            return;
        }

        Statement statement = createStatement();

        for (String command : commandsToRun) {
            if(configuration.isShowQueries()) System.out.println(command);
            statement.execute(command);
        }
    }

    @SneakyThrows
    public void startTransaction() {
        checkNoTransactionInProgress();
        transactionInProgress.set(true);
        getCurrentConnection(AcquireConnectionOption.CHECK_LAST_ACCESS_TIMEOUT).setAutoCommit(false);
    }

    @SneakyThrows
    public void rollbackTransaction(){
        checkTransactionInProgress();

        transactionInProgress.set(false);
        connectionThreadLocal.get().rollback();
        stopUsingCurrentConnection();
    }

    @SneakyThrows
    public void commitTransaction(){
        checkTransactionInProgress();

        transactionInProgress.set(false);
        connectionThreadLocal.get().commit();
        stopUsingCurrentConnection();
    }

    public Statement createStatement() throws SQLException {
        checkNoTransactionInProgress();

        return getCurrentConnection(AcquireConnectionOption.DEFAULT_OPTIONS)
                .createStatement();
    }

    public PreparedStatement createPreparedStatement(String sql) throws SQLException {
        checkNoTransactionInProgress();

        return getCurrentConnection(AcquireConnectionOption.DEFAULT_OPTIONS)
                .prepareStatement(sql);
    }

    private void checkNoTransactionInProgress() {
        if (transactionInProgress.get()) {
            throw new IllegalState("Cannot start transaction when another transaction in the current thread is in progress");
        }
    }

    private void checkTransactionInProgress() {
        if (!transactionInProgress.get()) {
            throw new IllegalState("Cannot execute sql when no transaction has been started");
        }
    }

    private Connection getCurrentConnection(AcquireConnectionOption option, AcquireConnectionOption...options) {
        if(connectionThreadLocal.get() == null){
            connectionThreadLocal.set(connectionPool.acquire(option, options));
        }

        return connectionThreadLocal.get();
    }

    private void stopUsingCurrentConnection() {
        connectionPool.release(connectionThreadLocal.get());
        connectionThreadLocal.remove();
    }
}
