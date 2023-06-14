package es.jaime.deserializer.deserializers;

import es.jaime.deserializer.DatabaseTypeDeserializer;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class StringDeserializer implements DatabaseTypeDeserializer<String> {
    @Override
    public String deserialize(String fieldName, ResultSet resultSet, Class<? extends String> type) throws SQLException {
        return resultSet.getString(fieldName);
    }
}
