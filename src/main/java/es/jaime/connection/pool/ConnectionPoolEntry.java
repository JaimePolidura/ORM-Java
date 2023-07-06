package es.jaime.connection.pool;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.sql.Connection;

@AllArgsConstructor
public final class ConnectionPoolEntry {
    private Connection connection;
    private long lastTimeAccessed;

    public boolean hasTimeoutPassed(long timeout) {
        return System.currentTimeMillis() > lastTimeAccessed + timeout;
    }

    @SneakyThrows
    public void close() {
        this.connection.close();
    }

    public Connection getConnection() {
        return connection;
    }
}
