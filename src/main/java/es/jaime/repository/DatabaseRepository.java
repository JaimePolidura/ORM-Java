package es.jaime.repository;

import es.jaime.connection.DatabaseConnection;
import es.jaime.mapper.TableMapper;
import es.jaimetruman.delete.Delete;
import es.jaimetruman.select.Select;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public abstract class DatabaseRepository<T> {
    private final DatabaseConnection dataBaseConnection;

    public abstract TableMapper<T> mapper();
    public abstract T buildObject(ResultSet resultSet) throws SQLException;

    protected List<T> all(){
        try{
            Select query = Select.from(table());
            ResultSet resultSet = dataBaseConnection.executeQuery(query);
            List<T> list = new ArrayList<>();

            while (resultSet.next()){
                list.add(buildObject(resultSet));
            }

            return list;
        }catch (Exception e){
            return Collections.EMPTY_LIST;
        }
    }

    protected Optional<T> findById(Object id){
        try {
            Select query = Select.from(table()).where(idField()).equal(id);
            ResultSet resultSet = dataBaseConnection.executeQuery(query);
            resultSet.next();

            T object = buildObject(resultSet);

            return Optional.ofNullable(object);
        }catch (Exception e){
            return Optional.empty();
        }
    }

    @SneakyThrows
    protected void deleteById(Object id){
        Delete queryDelete = Delete.from(table()).where(idField()).equal(id);
        dataBaseConnection.executeUpdate(queryDelete);
    }

    private String table(){
        return this.mapper().getTable();
    }

    private String idField(){
        return this.mapper().getIdField();
    }

}
