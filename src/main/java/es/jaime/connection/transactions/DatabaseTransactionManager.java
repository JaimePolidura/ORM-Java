package es.jaime.connection.transactions;

import es.jaime.connection.ConnectionManager;
import es.jaime.connection.pool.ConnectionPool;
import es.jaime.javaddd.domain.database.TransactionManager;
import lombok.SneakyThrows;

import java.sql.Connection;

public final class DatabaseTransactionManager implements TransactionManager {
    private final ThreadLocal<Connection> connectionThreadLocal;
    private final ConnectionPool connectionPool;

    public DatabaseTransactionManager(ConnectionManager connectionManager) {
        this.connectionPool = connectionManager.getPool();
        this.connectionThreadLocal = new ThreadLocal<>();
    }

    @SneakyThrows
    public void start() {
        connectionThreadLocal.set(connectionPool.acquire());
        connectionThreadLocal.get().setAutoCommit(false);
    }

    @SneakyThrows
    public void rollback(){
        connectionThreadLocal.get().rollback();

        connectionPool.release(connectionThreadLocal.get());
        connectionThreadLocal.set(null);
    }

    @SneakyThrows
    public void commit(){
        connectionThreadLocal.get().commit();

        connectionPool.release(connectionThreadLocal.get());
        connectionThreadLocal.set(null);
    }
}
