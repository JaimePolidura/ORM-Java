package es.jaime.connection.pool;

import java.sql.Connection;

public interface ConnectionPool {
    Connection acquire();
}
