package es.jaime.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.jaime.connection.ConnectionManager;
import es.jaimetruman.ReadQuery;
import es.jaimetruman.WriteQuery;
import lombok.SneakyThrows;

import java.sql.*;
import java.util.Collections;
import java.util.List;

public abstract class DatabaseConfiguration {
    protected ConnectionManager connectionManager;

    public abstract String url();

    public boolean showQueries(){
        return false;
    }

    public List<String> getCommandsToRun(){
        return Collections.EMPTY_LIST;
    }

    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }

    public ConnectionManager getConnectionManager() {
        return this.connectionManager;
    }

    @SneakyThrows
    public void runCommands(){
        if(getCommandsToRun().isEmpty()) return;

        Statement statement = connectionManager.getConnection().createStatement();
        List<String> commandsToRun = this.getCommandsToRun();

        for (String command : commandsToRun) {
            if(showQueries()) System.out.println(command);
            statement.execute(command);
        }
    }

    public void sendStatement(String statement) throws Exception {
        if(showQueries()) System.out.println(statement);

        this.getConnection().createStatement().execute(statement);
    }

    public final ResultSet sendQuery(ReadQuery query) throws Exception {
        if(showQueries()) System.out.println(query);

        return this.getConnection().createStatement().executeQuery(query.toString());
    }

    public final ResultSet sendQuery(String query) throws Exception {
        if(showQueries()) System.out.println(query);

        return this.getConnection().createStatement().executeQuery(query);
    }

    public final void sendUpdate(WriteQuery query) throws Exception {
        if(showQueries()) System.out.println(query);

        this.getConnection().createStatement().executeUpdate(query.toString());
    }

    public final void sendUpdate(String query) throws Exception {
        if(showQueries()) System.out.println(query);

        this.getConnection().createStatement().executeUpdate(query);
    }

    private Connection getConnection() throws Exception {
        return connectionManager.getConnection();
    }
}
