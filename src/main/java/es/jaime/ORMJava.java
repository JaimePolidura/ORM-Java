package es.jaime;

import es.jaime.deserializer.DatabaseTypeDeserializer;
import es.jaime.deserializer.DatabaseTypeDeserializerMapper;
import es.jaime.deserializer.deserializers.*;
import es.jaime.javaddd.domain.database.DatabaseTypeSerializer;
import es.jaimetruman.MySQLQueryBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

public final class ORMJava {
    private final static DatabaseTypeDeserializerMapper DATABASE_DESERIALIZER_MAPPER;

    static {
        DATABASE_DESERIALIZER_MAPPER = new DatabaseTypeDeserializerMapper();
        DATABASE_DESERIALIZER_MAPPER.addDeserializer(boolean.class, new BooleanDeserializer());
        DATABASE_DESERIALIZER_MAPPER.addDeserializer(double.class, new DoubleDeserializer());
        DATABASE_DESERIALIZER_MAPPER.addDeserializer(float.class, new FloatDeserializer());
        DATABASE_DESERIALIZER_MAPPER.addDeserializer(long.class, new LongDeserializer());
        DATABASE_DESERIALIZER_MAPPER.addDeserializer(int.class, new IntDeserializer());
        DATABASE_DESERIALIZER_MAPPER.addDeserializer(short.class, new ShortDeserializer());
        DATABASE_DESERIALIZER_MAPPER.addDeserializer(Enum.class, new EnumDeserializer());
        DATABASE_DESERIALIZER_MAPPER.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        DATABASE_DESERIALIZER_MAPPER.addDeserializer(String.class, new StringDeserializer());
        DATABASE_DESERIALIZER_MAPPER.addDeserializer(UUID.class, new UUIDDeserializer());
    }

    public static <T> void addCustomSerializer(Class<? extends T> type, DatabaseTypeSerializer<T> serializer) {
        MySQLQueryBuilder.addCustomSerializer(type, serializer);
    }

    public static <T> void addCustomDeserializer(Class<? extends T> type, DatabaseTypeDeserializer<T> serializer) {
        DATABASE_DESERIALIZER_MAPPER.addDeserializer(type, serializer);
    }

    public static DatabaseTypeDeserializerMapper getDatabaseDeserializerMapper() {
        return DATABASE_DESERIALIZER_MAPPER;
    }
}
