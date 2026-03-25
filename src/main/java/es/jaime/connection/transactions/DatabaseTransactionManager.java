package es.jaime.connection.transactions;

import es.jaime.connection.ConnectionManager;
import es.jaime.connection.pool.AcquireConnectionOptions;
import es.jaime.connection.pool.ConnectionPool;
import es.jaime.javaddd.domain.database.TransactionManager;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.util.EnumSet;

public final class DatabaseTransactionManager implements TransactionManager {
    private final ConnectionManager connectionManager;

    public DatabaseTransactionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @SneakyThrows
    public void start() {
        connectionManager.acquireConnection(EnumSet.of(AcquireConnectionOptions.CHECK_LAST_ACCESS_TIMEOUT))
                .setAutoCommit(false);
    }

    @SneakyThrows
    public void rollback(){
        connectionManager.acquireConnection(EnumSet.of(AcquireConnectionOptions.DEFAULT_OPTIONS))
                .rollback();
        connectionManager.releaseConnection();
    }

    @SneakyThrows
    public void commit(){
        connectionManager.acquireConnection(EnumSet.of(AcquireConnectionOptions.DEFAULT_OPTIONS))
                .commit();
        connectionManager.releaseConnection();
    }
}
