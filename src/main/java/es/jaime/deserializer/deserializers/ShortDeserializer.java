package es.jaime.deserializer.deserializers;

import es.jaime.deserializer.DatabaseTypeDeserializer;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class ShortDeserializer implements DatabaseTypeDeserializer<Short> {
    @Override
    public Short deserialize(String fieldName, ResultSet resultSet, Class<? extends Short> type) throws SQLException {
        return resultSet.getShort(fieldName);
    }
}
