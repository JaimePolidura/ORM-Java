package es.jaime.connection.pool;

import java.sql.Connection;
import java.util.EnumSet;

public interface ConnectionPool {
    Connection acquire(EnumSet<AcquireConnectionOptions> options);

    void release(Connection connection);

    void closeAll();
}
