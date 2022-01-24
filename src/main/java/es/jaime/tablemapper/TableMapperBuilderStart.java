package es.jaime.tablemapper;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class TableMapperBuilderStart {
    private final String table;

    public TableMapperBuilderId id(String id){
        return new TableMapperBuilderId(table, id);
    }
}
