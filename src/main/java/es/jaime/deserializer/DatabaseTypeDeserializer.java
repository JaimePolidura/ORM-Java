package es.jaime.deserializer;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface DatabaseTypeDeserializer<T> {
    T deserialize(String fieldName, ResultSet resultSet, Class<? extends T> type) throws SQLException;
}
