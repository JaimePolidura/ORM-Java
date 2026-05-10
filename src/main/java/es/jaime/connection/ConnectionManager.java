package es.jaime.connection;

import es.jaime.configuration.DatabaseConfiguration;
import es.jaime.transactions.TransactionPropagationLevel;
import es.jaime.transactions.TransactionManager;
import es.jaimetruman.ReadQuery;
import es.jaimetruman.WriteQuery;
import lombok.SneakyThrows;

import java.sql.*;
import java.util.List;

public final class ConnectionManager {
    private final DatabaseConfiguration configuration;
    private final TransactionManager transactionManager;

    @SneakyThrows
    public ConnectionManager(DatabaseConfiguration configuration) {
        this.configuration = configuration;
        this.transactionManager = new TransactionManager(configuration.getConnectionPool());
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

    @SneakyThrows
    public void startTransaction() {
        transactionManager.startTransaction(TransactionPropagationLevel.REQUIRED);
    }

    @SneakyThrows
    public void startTransaction(TransactionPropagationLevel option) {
        transactionManager.startTransaction(option);
    }

    @SneakyThrows
    public void commitTransaction() {
        transactionManager.commitTransaction();
    }

    @SneakyThrows
    public void rollbackTransaction() {
        transactionManager.rollbackTransaction();
    }

    public Statement createStatement() throws SQLException {
        return transactionManager.getCurrentConnection().createStatement();
    }

    public PreparedStatement createPreparedStatement(String sql) throws SQLException {
        return transactionManager.getCurrentConnection().prepareStatement(sql);
    }
}
