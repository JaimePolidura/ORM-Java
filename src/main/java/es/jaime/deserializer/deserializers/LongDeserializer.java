package es.jaime.deserializer.deserializers;

import es.jaime.deserializer.DatabaseTypeDeserializer;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class LongDeserializer implements DatabaseTypeDeserializer<Long> {
    @Override
    public Long deserialize(String fieldName, ResultSet resultSet, Class<? extends Long> type) throws SQLException {
        return resultSet.getLong(fieldName);
    }
}
