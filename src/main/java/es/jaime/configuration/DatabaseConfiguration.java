package es.jaime.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.jaime.connection.pool.ConnectionPool;
import es.jaime.connection.pool.perthread.PerThreadConnectionPool;
import es.jaime.connection.pool.shared.SharedConnectionPool;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
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
        private boolean perThreadConnectionPool;

        public Builder() {
            this.objectMapper = new ObjectMapper();
            this.commandsToRun = new ArrayList<>();
            this.connectionTimeoutMs = 0;
            this.showQueries = false;
            this.perThreadConnectionPool = true;
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
            this.connectionTimeoutMs = duration.get(ChronoUnit.MILLIS);
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
            this.perThreadConnectionPool = true;
            return this;
        }

        public Builder sharedConnectionPool() {
            this.perThreadConnectionPool = false;
            return this;
        }

        public DatabaseConfiguration build() {
            ConnectionPool connectionPool = perThreadConnectionPool ?
                    new PerThreadConnectionPool(connectionTimeoutMs, url) :
                    new SharedConnectionPool(connectionTimeoutMs, url);

            return new DatabaseConfiguration(url, connectionTimeoutMs, showQueries, connectionPool, commandsToRun,
                    objectMapper);
        }
    }
}
