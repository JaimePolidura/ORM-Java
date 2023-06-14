package es.jaime.deserializer.deserializers;

import es.jaime.deserializer.DatabaseTypeDeserializer;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class EnumDeserializer implements DatabaseTypeDeserializer<Enum> {
    @Override
    public Enum deserialize(String fieldName, ResultSet resultSet, Class<? extends Enum> type) throws SQLException {
        String value = resultSet.getString(fieldName);

        return value != null && !value.equals("") ?
                Enum.valueOf(type, value) :
                null;
    }
}
