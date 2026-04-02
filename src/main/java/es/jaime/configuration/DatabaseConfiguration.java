package es.jaime.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.jaime.connection.pool.ConnectionPool;
import es.jaime.connection.pool.SingleThreadedConnectionPool;
import es.jaime.connection.pool.perthread.PerThreadConnectionPool;
import es.jaime.connection.pool.shared.SharedConnectionPool;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class DatabaseConfiguration {
    @Getter private final String url;
    @Getter private final long connectionTimeoutMs;
    @Getter private final boolean showQueries;
    @Getter private final ConnectionPool connectionPool;
    @Getter private final List<String> commandsToRun;
    @Getter private final ObjectMapper objectMapper;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String url;
        private long connectionTimeoutMs;
        private boolean showQueries;
        private List<String> commandsToRun;
        private ObjectMapper objectMapper;
        private ConnectionPoolImplementation connectionPoolImplementation;

        public Builder() {
            this.connectionTimeoutMs = Duration.ofHours(1L).toMillis();
            this.objectMapper = new ObjectMapper();
            this.commandsToRun = new ArrayList<>();
            this.connectionPoolImplementation = ConnectionPoolImplementation.PER_THREAD;
            this.showQueries = false;
        }

        public Builder logQueries(boolean log) {
            this.showQueries = log;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder connectionTimeout(Duration duration) {
            this.connectionTimeoutMs = duration.toMillis();
            return this;
        }

        public Builder logQueries() {
            this.showQueries = true;
            return this;
        }

        public Builder commandsToRun(List<String> commandsToRun) {
            this.commandsToRun.addAll(commandsToRun);
            return this;
        }

        public Builder commandToRun(String commandToRun) {
            this.commandsToRun.add(commandToRun);
            return this;
        }

        public Builder objectMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
            return this;
        }


        public Builder perThreadConnectionPool() {
            this.connectionPoolImplementation = ConnectionPoolImplementation.PER_THREAD;
            return this;
        }

        public Builder singleThreadedConnectionPool() {
            this.connectionPoolImplementation = ConnectionPoolImplementation.SINGLE_THREADED;
            return this;
        }

        public Builder sharedConnectionPool() {
            this.connectionPoolImplementation = ConnectionPoolImplementation.SHARED;
            return this;
        }

        public DatabaseConfiguration build() {
            ConnectionPool connectionPool = switch(connectionPoolImplementation) {
                case SINGLE_THREADED -> new SingleThreadedConnectionPool(connectionTimeoutMs, url);
                case PER_THREAD -> new PerThreadConnectionPool(connectionTimeoutMs, url);
                case SHARED -> new SharedConnectionPool(connectionTimeoutMs, url);
            };

            return new DatabaseConfiguration(url, connectionTimeoutMs, showQueries, connectionPool, commandsToRun,
                    objectMapper);
        }

        private enum ConnectionPoolImplementation {
            PER_THREAD, SHARED, SINGLE_THREADED
        }
    }
}
