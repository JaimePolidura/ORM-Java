package es.jaime.connection.transactions;

import es.jaime.connection.ConnectionManager;
import es.jaime.connection.pool.AcquireConnectionOption;
import es.jaime.javaddd.domain.database.TransactionManager;
import lombok.SneakyThrows;

public final class DatabaseTransactionManager implements TransactionManager {
    private final ConnectionManager connectionManager;

    public DatabaseTransactionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @SneakyThrows
    public void start() {
        connectionManager.acquireConnection(AcquireConnectionOption.CHECK_LAST_ACCESS_TIMEOUT).setAutoCommit(false);
    }

    @SneakyThrows
    public void rollback(){
        connectionManager.acquireConnection(AcquireConnectionOption.DEFAULT_OPTIONS).rollback();
        connectionManager.releaseConnection();
    }

    @SneakyThrows
    public void commit(){
        connectionManager.acquireConnection(AcquireConnectionOption.DEFAULT_OPTIONS).commit();
        connectionManager.releaseConnection();
    }
}
