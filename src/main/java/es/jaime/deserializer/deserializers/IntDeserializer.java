package es.jaime.deserializer.deserializers;

import es.jaime.deserializer.DatabaseTypeDeserializer;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class IntDeserializer implements DatabaseTypeDeserializer<Integer> {
    @Override
    public Integer deserialize(String fieldName, ResultSet resultSet, Class<? extends Integer> type) throws SQLException {
        return resultSet.getInt(fieldName);
    }
}
