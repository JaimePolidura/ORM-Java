package es.jaime.transacions;

import es.jaime.connection.DatabaseConnection;
import lombok.SneakyThrows;

import java.sql.Connection;

public final class TransacionManager {
    private final Connection connection;

    public TransacionManager(DatabaseConnection databaseConnection) {
        this.connection = databaseConnection.getConnection();
    }

    @SneakyThrows
    public void start(){
        this.connection.setAutoCommit(false);
    }

    @SneakyThrows
    public void rollback(){
        this.connection.rollback();
    }

    @SneakyThrows
    public void commit(){
        this.connection.commit();
    }
}
