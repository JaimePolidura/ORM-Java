package es.jaime.deserializer.deserializers;

import es.jaime.deserializer.DatabaseTypeDeserializer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public final class LocalDateTimeDeserializer implements DatabaseTypeDeserializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(String fieldName, ResultSet resultSet, Class<? extends LocalDateTime> type) throws SQLException {
        var sqlTimestamp = resultSet.getTimestamp(fieldName);
        return sqlTimestamp != null ? sqlTimestamp.toLocalDateTime() : null;
    }
}
