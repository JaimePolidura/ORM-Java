package es.jaime.repository;

import es.jaime.CheckedFunction;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.ResultSet;
import java.util.Map;

@AllArgsConstructor
public final class ConditionalClassMapping<E, T> {
    @Getter private final CheckedFunction<ResultSet, T> typeValueAccessor;
    @Getter private final Map<T, Class<? extends E>> entitiesTypeMapper;
    @Getter private final Class<T> typeClass;

    public static ConditionalClassMapping empty() {
        return new ConditionalClassMapping(null, null, null);
    }

    public Class<? extends E> getMappingClass(ResultSet resultSet) {
        T type = typeValueAccessor.applyOrRethrow(resultSet, "Type not found on class " + typeClass.getName());

        return entitiesTypeMapper.get(type);
    }

    public static <E, T> ConditionalClassMappingBuilder<E, T> builder() {
        return new ConditionalClassMappingBuilder<>();
    }

    public static class ConditionalClassMappingBuilder<E, T> {
        @Getter private CheckedFunction<ResultSet, T> typeValueAccessor;
        @Getter private Map<T, Class<? extends E>> entitiesTypeMapper;
        @Getter private Class<T> typeClass;

        public ConditionalClassMapping<E, T> build() {
            return new ConditionalClassMapping(typeValueAccessor, entitiesTypeMapper, typeClass);
        }

        public ConditionalClassMappingBuilder<E, T> entitiesTypeMapper(Map<T, Class<? extends E>> entitiesTypeMapper) {
            this.entitiesTypeMapper = entitiesTypeMapper;
            return this;
        }

        public ConditionalClassMappingBuilder<E, T> typeClass(Class<T> typeClass) {
            this.typeClass = typeClass;
            return this;
        }

        public ConditionalClassMappingBuilder<E, T> typeValueAccessor(CheckedFunction<ResultSet, T> typeValueAccessor) {
            this.typeValueAccessor = typeValueAccessor;
            return this;
        }
    }
}
