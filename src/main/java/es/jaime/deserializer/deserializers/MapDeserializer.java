package es.jaime.deserializer.deserializers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.jaime.deserializer.DatabaseTypeDeserializer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class MapDeserializer implements DatabaseTypeDeserializer<Map> {
    @Override
    public Map deserialize(String fieldName, ResultSet resultSet, Class<? extends Map> type) throws SQLException {
        String data = resultSet.getString(fieldName);
        try {
            return new ObjectMapper().readValue(data, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
