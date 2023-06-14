package es.jaime.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.sql.ResultSet;

@AllArgsConstructor
public final class EntityMapper<E, T> {
    @Getter @NonNull private final String table;
    @Getter @NonNull private final String idField;
    @Getter @NonNull private final Class<? extends E>[] classToMap;
    @Getter          private final ConditionalClassMapping<E, T> conditionalClassMapping;
    @Getter          private final boolean hasConditionalMappingClass;

    public Class<? extends E> getMappingClass(ResultSet resultSet) {
        return hasConditionalMappingClass ?
                conditionalClassMapping.getMappingClass(resultSet) :
                classToMap[0];
    }

    public static class TableMapperBuilder<E, T>{
        private ConditionalClassMapping<E, T> conditionalClassMapping;
        private boolean hasConditionalMappingClass;
        private Class<? extends T>[] classesToMap;
        private String idField;
        private String table;

        public TableMapperBuilder() {
            this.conditionalClassMapping = ConditionalClassMapping.empty();
        }

        public TableMapperBuilder<E, T> conditionalClassMapping(ConditionalClassMapping<E, T> conditionalClassMapping) {
            this.conditionalClassMapping = conditionalClassMapping;
            this.hasConditionalMappingClass = true;
            return this;
        }

        public TableMapperBuilder<E, T> table(String table) {
            this.table = table;
            return this;
        }

        public TableMapperBuilder idField(String idField){
            this.idField = idField;
            return this;
        }

        @SafeVarargs
        public final TableMapperBuilder classesToMap(Class<? extends T>... classToMap){
            this.hasConditionalMappingClass = false;
            this.classesToMap = classToMap;
            return this;
        }

        public EntityMapper build() {
            return new EntityMapper(table, idField, classesToMap, conditionalClassMapping, hasConditionalMappingClass);
        }
    }

    public static EntityMapper.TableMapperBuilder builder(){
        return new EntityMapper.TableMapperBuilder();
    }
}
