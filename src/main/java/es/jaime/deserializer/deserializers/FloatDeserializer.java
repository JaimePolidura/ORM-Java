package es.jaime.deserializer.deserializers;

import es.jaime.deserializer.DatabaseTypeDeserializer;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class FloatDeserializer implements DatabaseTypeDeserializer<Float> {
    @Override
    public Float deserialize(String fieldName, ResultSet resultSet, Class<? extends Float> type) throws SQLException {
        return resultSet.getFloat(fieldName);
    }
}
