package es.jaime.tablemapper;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.ResultSet;
import java.util.function.Function;

@AllArgsConstructor
public class TableMapperBuilderId {
    private final String table;
    private final String id;

    public TableMapper classBuilder(Function<ResultSet, Object> classBuilder){
        return new TableMapper(table, id, classBuilder);
    }
}
