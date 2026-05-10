package es.jaime.connection.pool.impl;

import es.jaime.connection.pool.ConnectionPool;
import es.jaime.javaddd.application.utils.ExceptionUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.EnumSet;

public class SingleThreadedConnectionPool implements ConnectionPool {
    private final long connectionTimeoutMs;
    private final String url;

    private Connection connection;
    private long lastTimeAccessed;

    public SingleThreadedConnectionPool(long connectionTimeoutMs, String url) {
        this.connectionTimeoutMs = connectionTimeoutMs;
        this.url = url;
    }

    @Override
    public Connection acquire() {
        if (connection == null) {
            initConnection();
        }

        if (System.currentTimeMillis() > lastTimeAccessed + connectionTimeoutMs) {
            initConnection();
        }

        this.lastTimeAccessed = System.currentTimeMillis();

        return connection;
    }

    @Override
    public void release(Connection connection) {
    }

    private void initConnection() {
        ExceptionUtils.rethrowChecked(() -> {
            this.connection = DriverManager.getConnection(url);
            this.lastTimeAccessed = System.currentTimeMillis();
        });
    }
}
