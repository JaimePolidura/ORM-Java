package es.jaime.deserializer.deserializers;

import es.jaime.deserializer.DatabaseTypeDeserializer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class DataDeserializer implements DatabaseTypeDeserializer<Date> {
    @Override
    public Date deserialize(String fieldName, ResultSet resultSet, Class<? extends Date> type) throws SQLException {
        java.sql.Date dateValue = resultSet.getDate(fieldName);
        return dateValue != null ? new Date(dateValue.getTime()) : null;
    }
}
