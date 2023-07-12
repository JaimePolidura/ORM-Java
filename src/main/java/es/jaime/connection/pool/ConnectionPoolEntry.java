package es.jaime.connection.pool;

import lombok.Getter;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.util.concurrent.atomic.AtomicInteger;

import static es.jaime.javaddd.application.utils.ExceptionUtils.*;

public final class ConnectionPoolEntry {
    public static final int CONNECTION_NOT_IN_USE = 0;
    public static final int CONNECTION_IN_USE = 1;
    public static final int CONNECTION_CLOSED = 2;

    @Getter
    private final AtomicInteger state;
    @Getter
    private final long createdByThreadId;
    private Connection connection;
    private long lastTimeAccessed;

    public ConnectionPoolEntry(Connection connection, long lastTimeAccessed) {
        this.createdByThreadId = Thread.currentThread().getId();
        this.state = new AtomicInteger(CONNECTION_IN_USE);
        this.lastTimeAccessed = lastTimeAccessed;
        this.connection = connection;
    }

    public boolean compareAndSetState(int expectedValue, int newValue) {
        return state.compareAndSet(expectedValue, newValue);
    }

    public boolean hasTimeoutPassed(long timeout) {
        return System.currentTimeMillis() > lastTimeAccessed + timeout;
    }

    public void recreate(ConnectionPoolEntry other) {
        this.close();

        this.lastTimeAccessed = other.lastTimeAccessed;
        this.connection = other.getConnection();
        this.state.set(other.getState().get());
    }

    @SneakyThrows
    public void close() {
        state.set(CONNECTION_CLOSED);
        ignoreException(connection::close);
    }

    public Connection getConnection() {
        return connection;
    }
}
