package es.jaime.mapper;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class TableMapperBuilderStart {
    private final String table;

    public TableMapper idField(String id){
        return new TableMapper(table, id);
    }
}
