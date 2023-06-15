package es.jaime.connection;

import es.jaime.javaddd.domain.database.TransactionManager;
import lombok.SneakyThrows;

public final class DatabaseTransactionManager implements TransactionManager {
    private final ConnectionManager connectionManager;

    public DatabaseTransactionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @SneakyThrows
    public void start(){
        this.connectionManager.getConnection().setAutoCommit(false);
    }

    @SneakyThrows
    public void rollback(){
        this.connectionManager.getConnection().rollback();
    }

    @SneakyThrows
    public void commit(){
        this.connectionManager.getConnection().commit();
    }
}
