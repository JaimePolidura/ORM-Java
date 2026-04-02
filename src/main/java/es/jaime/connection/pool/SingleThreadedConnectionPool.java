package es.jaime.connection.pool;

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
    public Connection acquire(AcquireConnectionOption option, AcquireConnectionOption... options) {
        if (connection == null) {
            initConnection();
        }

        EnumSet<AcquireConnectionOption> optionsSet = EnumSet.of(option, options);
        if (optionsSet.contains(AcquireConnectionOption.CHECK_LAST_ACCESS_TIMEOUT)
            && System.currentTimeMillis() > lastTimeAccessed + connectionTimeoutMs) {
            initConnection();
        }

        this.lastTimeAccessed = System.currentTimeMillis();

        return connection;
    }

    @Override
    public void release(Connection connection) {
    }

    @Override
    public void closeAll() {
        ExceptionUtils.rethrowChecked(() -> {
            this.connection.close();
            this.connection = null;
        });
    }

    private void initConnection() {
        ExceptionUtils.rethrowChecked(() -> {
            this.connection = DriverManager.getConnection(url);
            this.lastTimeAccessed = System.currentTimeMillis();
        });
    }
}
