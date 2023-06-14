package es.jaime.deserializer.deserializers;

import es.jaime.deserializer.DatabaseTypeDeserializer;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class DoubleDeserializer implements DatabaseTypeDeserializer<Double> {
    @Override
    public Double deserialize(String fieldName, ResultSet resultSet, Class<? extends Double> type) throws SQLException {
        return resultSet.getDouble(fieldName);
    }
}
