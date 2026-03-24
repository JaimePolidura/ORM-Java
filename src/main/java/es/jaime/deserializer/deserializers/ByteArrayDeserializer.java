package es.jaime.deserializer.deserializers;

import es.jaime.deserializer.DatabaseTypeDeserializer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ByteArrayDeserializer implements DatabaseTypeDeserializer<byte[]> {
    @Override
    public byte[] deserialize(String fieldName, ResultSet resultSet, Class<? extends byte[]> type) throws SQLException {
        return resultSet.getBytes(fieldName);
    }
}
