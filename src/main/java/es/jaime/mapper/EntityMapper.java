package es.jaime.mapper;

import lombok.Getter;
import lombok.NonNull;

public final class EntityMapper<T> {
    @Getter @NonNull private final String table;
    @Getter @NonNull private final String idField;
    @Getter @NonNull private final Class<T> classToMap;

    public EntityMapper(String table, String idField, Class<T> classToMap) {
        this.table = table;
        this.idField = idField;
        this.classToMap = classToMap;
    }

    public static class TableMapperBuilder<T>{
        private String table;
        private String idField;
        private Class<T> classToMap;

        public TableMapperBuilder(String table) {
            this.table = table;
        }

        public TableMapperBuilder idField(String idField){
            this.idField = idField;

            return this;
        }

        public TableMapperBuilder classToMap(Class<T> classToMap){
            this.classToMap = classToMap;

            return this;
        }

        public EntityMapper build() {
            return new EntityMapper(table, idField, classToMap);
        }
    }

    public static TableMapperBuilder table(String table){
        return new TableMapperBuilder(table);
    }
}
