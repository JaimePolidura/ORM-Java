package es.jaime.connection.transactions;

import es.jaime.connection.ConnectionManager;
import es.jaime.connection.pool.ConnectionPool;
import es.jaime.javaddd.domain.database.TransactionManager;
import lombok.SneakyThrows;

import java.sql.Connection;

public final class DatabaseTransactionManager implements TransactionManager {
    private final ConnectionManager connectionManager;

    public DatabaseTransactionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @SneakyThrows
    public void start() {
        connectionManager.acquireConnection().setAutoCommit(false);
    }

    @SneakyThrows
    public void rollback(){
        connectionManager.acquireConnection().rollback();
        connectionManager.releaseConnection();
    }

    @SneakyThrows
    public void commit(){
        connectionManager.acquireConnection().commit();
        connectionManager.releaseConnection();
    }
}
