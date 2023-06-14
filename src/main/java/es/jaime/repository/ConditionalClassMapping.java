package es.jaime.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.sql.ResultSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Builder
@AllArgsConstructor
public final class ConditionalClassMapping<E, T> {
    @Getter private final Map<T, Class<? extends E>> entitiesTypeMapper;
    @Getter private final Function<ResultSet, T> typeBuilder;
    @Getter private final Class<T> typeClass;

    public static ConditionalClassMapping empty() {
        return new ConditionalClassMapping(null, null, null);
    }

    public Class<? extends E> getMappingClass(ResultSet resultSet) {
        T type = typeBuilder.apply(resultSet);

        return Objects.requireNonNull(entitiesTypeMapper.get(type), String.format("Type %s not registered", type));
    }
}
