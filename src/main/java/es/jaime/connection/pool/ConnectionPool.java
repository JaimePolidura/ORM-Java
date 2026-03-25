package es.jaime.connection.pool;

import java.sql.Connection;

public interface ConnectionPool {
    Connection acquire(AcquireConnectionOption option, AcquireConnectionOption... options);

    void release(Connection connection);

    void closeAll();
}
