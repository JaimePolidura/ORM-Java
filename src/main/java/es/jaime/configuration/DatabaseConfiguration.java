package es.jaime.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

public abstract class DatabaseConfiguration {
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
}
