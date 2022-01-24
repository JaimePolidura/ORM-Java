package es.jaime.mapper;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public final class TableMapper<T> {
    @Getter private String table;
    @Getter private String idField;

    public static TableMapperBuilderStart table(String table){
        return new TableMapperBuilderStart(table);
    }
}
