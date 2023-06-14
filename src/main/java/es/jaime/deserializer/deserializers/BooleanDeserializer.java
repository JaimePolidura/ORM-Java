package es.jaime.deserializer.deserializers;

import es.jaime.deserializer.DatabaseTypeDeserializer;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class BooleanDeserializer implements DatabaseTypeDeserializer<Boolean> {
    @Override
    public Boolean deserialize(String fieldName, ResultSet resultSet, Class<? extends Boolean> type) throws SQLException {
        return resultSet.getBoolean(fieldName);
    }
}
