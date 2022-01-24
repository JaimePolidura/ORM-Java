package es.jaime.mapper;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

public final class TableMapper<T> {
    @Getter private final String table;
    @Getter private final String idField;
    @Getter private final List<String> fields;
    @Getter private final String valueObjectField;
    @Getter private boolean usingValueObjects;

    public TableMapper(String table, String idField, List<String> fields, String valueObjectField) {
        this.table = table;
        this.idField = idField;
        this.fields = fields;
        this.valueObjectField = valueObjectField;

        this.usingValueObjects = valueObjectField != null && !valueObjectField.equalsIgnoreCase("");
    }

    public static class TableMapperBuilder{
        private String table;
        private String idField;
        private List<String> fields;
        private String valueObjectField;

        public TableMapperBuilder(String table) {
            this.table = table;
        }

        public TableMapperBuilder idField(String idField){
            this.idField = idField;

            return this;
        }

        public TableMapperBuilder fields(String... fields){
            this.fields = Arrays.asList(fields);

            return this;
        }

        public TableMapperBuilder usingValueObjects(String valueObjectField){
            this.valueObjectField = valueObjectField;

            return this;
        }

        public TableMapper build() {
            return new TableMapper(table, idField, fields, valueObjectField);
        }
    }

    public static TableMapperBuilder table(String table){
        return new TableMapperBuilder(table);
    }
}
