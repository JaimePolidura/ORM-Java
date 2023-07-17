package es.jaime.connection.pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static es.jaime.javaddd.application.utils.ExceptionUtils.rethrowChecked;

public final class SharedConnectionPool implements ConnectionPool {
    private final Map<Long, ConnectionPoolEntry> connectionsByThread;
    private final ConcurrentFreeConnectionList freeList;
    private final long connectionTimeoutMs;
    private final String url;

    public SharedConnectionPool(long connectionTimeoutMs, String url) {
        this.connectionsByThread = new ConcurrentHashMap<>();
        this.freeList = new ConcurrentFreeConnectionList(256);
        this.connectionTimeoutMs = connectionTimeoutMs;
        this.url = url;
    }

    @Override
    public Connection acquire() {
        ConnectionPoolEntry connectionPoolEntry = freeList.get();
        boolean connectionFoundInFreeList = connectionPoolEntry != null;

        if(!connectionFoundInFreeList){
            connectionPoolEntry = createConnectionPoolEntry();
        }
        if(connectionFoundInFreeList && connectionPoolEntry.hasTimeoutPassed(connectionTimeoutMs)){
            freeList.remove(connectionPoolEntry);
            connectionPoolEntry = createConnectionPoolEntry();
        }

        connectionsByThread.put(Thread.currentThread().getId(), connectionPoolEntry);

        return connectionPoolEntry.getConnection();
    }

    @Override
    public void release(Connection connection) {
        long threadId = Thread.currentThread().getId();

        freeList.add(connectionsByThread.get(threadId));
        connectionsByThread.remove(threadId);
    }

    @Override
    public void releaseAll() {
        freeList.removeAll();
    }

    private ConnectionPoolEntry createConnectionPoolEntry() {
        return rethrowChecked(() -> {
            Connection connection = DriverManager.getConnection(url);
            long lastTimeAccessed = System.currentTimeMillis();

            return new ConnectionPoolEntry(connection, lastTimeAccessed);
        });
    }
}
