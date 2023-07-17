package es.jaime.connection.pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static es.jaime.javaddd.application.utils.ExceptionUtils.*;

public final class PerThreadConnectionPool implements ConnectionPool {
    private final Map<Long, ConnectionPoolEntry> connectionsByThread;
    private final long connectionTimeoutMs;
    private final String url;

    public PerThreadConnectionPool(long connectionTimeoutMs, String url) {
        this.connectionsByThread = new ConcurrentHashMap<>();
        this.connectionTimeoutMs = connectionTimeoutMs;
        this.url = url;
    }

    @Override
    public Connection acquire() {
        long currentThreadId = Thread.currentThread().getId();
        boolean hasConnection = connectionsByThread.containsKey(currentThreadId);

        if(!hasConnection){
            createPooolConnectionEntry(currentThreadId);
        }

        ConnectionPoolEntry poolEntry = connectionsByThread.get(currentThreadId);
        if(poolEntry.hasTimeoutPassed(connectionTimeoutMs)){
            poolEntry = recreateConnectionPoolEntry(currentThreadId, poolEntry);
        }

        return poolEntry.getConnection();
    }

    @Override
    public void release(Connection connection) {
    }

    @Override
    public void releaseAll() {
        connectionsByThread.values().forEach(ConnectionPoolEntry::close);
    }

    private ConnectionPoolEntry recreateConnectionPoolEntry(long currentThreadId, ConnectionPoolEntry poolEntry) {
        poolEntry.close();

        return createPooolConnectionEntry(currentThreadId);
    }

    private ConnectionPoolEntry createPooolConnectionEntry(long currentThreadId) {
        ConnectionPoolEntry connectionPoolEntry = createConnectionPoolEntry();
        connectionsByThread.put(currentThreadId, connectionPoolEntry);
        return connectionPoolEntry;
    }

    private ConnectionPoolEntry createConnectionPoolEntry() {
        return rethrowChecked(() -> {
            Connection connection = DriverManager.getConnection(url);
            long lastTimeAccessed = System.currentTimeMillis();

            return new ConnectionPoolEntry(connection, lastTimeAccessed);
        });
    }
}
