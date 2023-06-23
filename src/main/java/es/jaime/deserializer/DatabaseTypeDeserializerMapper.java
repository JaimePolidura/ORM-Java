package es.jaime.deserializer;

import java.sql.ResultSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static es.jaime.javaddd.application.utils.ExceptionUtils.*;

public final class DatabaseTypeDeserializerMapper {
    private final Map<Class<?>, DatabaseTypeDeserializer<?>> deserializers;

    public DatabaseTypeDeserializerMapper() {
        this.deserializers = new ConcurrentHashMap<>();
    }

    public <T> void addDeserializer(Class<? extends T> type, DatabaseTypeDeserializer<T> deserializer) {
        this.deserializers.put(type, deserializer);
    }

    public Object deserialize(Class<?> type, ResultSet resultSet, String fieldName) {
        return rethrowChecked(() -> {
            DatabaseTypeDeserializer databaseTypeDeserializer = getDeserializer(type);

            return databaseTypeDeserializer.deserialize(fieldName, resultSet, type);
        });
    }

    private DatabaseTypeDeserializer<?> getDeserializer(Class<?> clazz) {
        Class<?> actual = clazz;

        while (actual != Object.class) {
            if(deserializers.containsKey(actual)){
                return deserializers.get(actual);
            }

            actual = actual.getSuperclass();
        }

        return deserializers.get(String.class);
    }
}
