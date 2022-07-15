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

    public boolean showQueries(){
        return false;
    }

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
            if(showQueries()) System.out.println(command);
            statement.execute(command);
        }
    }

    public void sendStatement(String statement) throws SQLException {
        connectIfNotConnected();

        if(showQueries()) System.out.println(statement);

        this.connection.createStatement().execute(statement);
    }

    public final ResultSet sendQuery(ReadQuery query) throws SQLException {
        connectIfNotConnected();

        if(showQueries()) System.out.println(query);

        return connection.createStatement().executeQuery(query.toString());
    }

    public final ResultSet sendQuery(String query) throws SQLException {
        connectIfNotConnected();

        if(showQueries()) System.out.println(query);

        return connection.createStatement().executeQuery(query);
    }

    public final void sendUpdate(WriteQuery query) throws SQLException {
        connectIfNotConnected();

        if(showQueries()) System.out.println(query);

        connection.createStatement().executeUpdate(query.toString());
    }

    public final void sendUpdate(String query) throws SQLException {
        connectIfNotConnected();

        if(showQueries()) System.out.println(query);

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
