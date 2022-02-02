package es.jaime.configuration;

import es.jaimetruman.ReadQuery;
import es.jaimetruman.WriteQuery;
import lombok.Getter;
import lombok.SneakyThrows;

import java.sql.*;
import java.util.Collections;
import java.util.List;

public abstract class DatabaseConfiguration {
    @Getter private Connection connection;

    protected abstract String url();

    public List<String> getCommandsToRun(){
        return Collections.EMPTY_LIST;
    }

    @SneakyThrows
    public void runCommands(){
        connectIfNotConnected();

        if(getCommandsToRun().isEmpty()) return;

        Statement statement = connection.createStatement();
        List<String> commandsToRun = this.getCommandsToRun();

        for (String command : commandsToRun) {
            statement.execute(command);
        }
    }

    public final ResultSet sendQuery(ReadQuery query) throws SQLException {
        connectIfNotConnected();

        return connection.createStatement().executeQuery(query.toString());
    }

    public final ResultSet sendQuery(String query) throws SQLException {
        connectIfNotConnected();

        return connection.createStatement().executeQuery(query);
    }

    public final void sendUpdate(WriteQuery query) throws SQLException {
        connectIfNotConnected();

        connection.createStatement().executeUpdate(query.toString());
    }

    public final void sendUpdate(String query) throws SQLException {
        connectIfNotConnected();

        connection.createStatement().executeUpdate(query);
    }

    @SneakyThrows
    private void connectIfNotConnected(){
        if(this.connection == null || connection.isClosed()){
            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection(url());
        }
    }
}
