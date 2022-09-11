package es.jaime.transacions;

import es.jaime.connection.ConnectionManager;
import lombok.SneakyThrows;

public final class DatabaseTransacionManager {
    private final ConnectionManager connectionManager;

    public DatabaseTransacionManager(ConnectionManager connectionManager) {
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
