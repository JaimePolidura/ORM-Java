package es.jaime.deserializer.deserializers;

import es.jaime.deserializer.DatabaseTypeDeserializer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public final class UUIDDeserializer implements DatabaseTypeDeserializer<UUID> {
    @Override
    public UUID deserialize(String fieldName, ResultSet resultSet, Class<? extends UUID> type) throws SQLException {
        String value = resultSet.getString(fieldName);

        return value != null && !value.equals("") ?
                UUID.fromString(resultSet.getString(fieldName)) :
                null;
    }
}
