package es.jaime.tablemapper;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.ResultSet;
import java.util.function.Function;

@AllArgsConstructor
public class TableMapper {
    @Getter private String table;
    @Getter private String id;
    @Getter private Function<ResultSet, Object> classBuilder;

    public static TableMapperBuilderStart table(String table){
        return new TableMapperBuilderStart(table);
    }
}
