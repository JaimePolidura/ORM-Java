package es.jaime.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.jaime.connection.pool.ConnectionPool;
import es.jaime.connection.pool.PerThreadConnectionPool;

import java.util.Collections;
import java.util.List;

public abstract class DatabaseConfiguration {
    public abstract String url();

    public long connectionTimeoutMs() {
        return 8 * 60 * 60 * 1000;
    }

    public boolean showQueries(){
        return false;
    }

    public ConnectionPool connectionPool() {
        return new PerThreadConnectionPool(connectionTimeoutMs(), url());
    }

    public List<String> getCommandsToRun(){
        return Collections.EMPTY_LIST;
    }

    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }
}
