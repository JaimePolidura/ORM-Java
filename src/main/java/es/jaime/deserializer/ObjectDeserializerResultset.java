package es.jaime.deserializer;

import es.jaime.ORMJava;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.List;

import static es.jaime.javaddd.application.utils.ExceptionUtils.*;
import static es.jaime.javaddd.application.utils.ReflectionUtils.*;

public final class ObjectDeserializerResultset {
    private final DatabaseTypeDeserializerMapper deserializer;

    public ObjectDeserializerResultset() {
        this.deserializer = ORMJava.getDatabaseDeserializerMapper();
    }

    public <T> T deserialize(ResultSet resultSet, Class<? extends T> mappingClass) throws Exception {
        List<Field> fields = getAllFields(mappingClass);
        T instance = rethrowChecked(mappingClass::newInstance);

        for (Field field : fields) {
            Class<?> type = field.getType();
            field.setAccessible(true);
            field.set(instance, deserializer.deserialize(type, resultSet, field.getName()));
        }

        return instance;
    }
}
