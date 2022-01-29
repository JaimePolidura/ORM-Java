package es.jaime.repository;

import es.jaime.configuration.DatabaseConfiguration;
import es.jaime.mapper.EntityMapper;
import es.jaimetruman.ReadQuery;
import es.jaimetruman.insert.InsertOptionFinal;
import es.jaimetruman.update.UpdateOptionFull1;
import es.jaimetruman.update.UpdateOptionInitial;
import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public abstract class Repostitory<T, I> {
    protected abstract List<T> all();
    protected abstract Optional<T> findById(I id);
    protected abstract void save(T toPersist);
    protected abstract void deleteById(I id);

    protected abstract DatabaseConfiguration databaseConnection();
    protected abstract EntityMapper<T> entityMapper();
    public abstract T buildObjectFromResultSet(ResultSet resultSet) throws SQLException;
    protected abstract Map<String, Object> toPrimitives(T aggregate);

    protected List<T> buildListFromQuery(ReadQuery readQuery){
        try {
            ResultSet resultSet = databaseConnection().sendQuery(readQuery);
            List<T> toReturn = new ArrayList<>();

            while (resultSet.next()){
                toReturn.add(buildObjectFromResultSet(resultSet));
            }

            return toReturn;
        } catch (SQLException e) {
            return Collections.EMPTY_LIST;
        }
    }

    public Optional<T> buildObjectFromQuery(ReadQuery readQuery){
        try {
            ResultSet resultSet = databaseConnection().sendQuery(readQuery);

            resultSet.next();

            return Optional.ofNullable(buildObjectFromResultSet(resultSet));
        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    @SneakyThrows
    protected void updateExistingObject(T toUpdate, Object id, UpdateOptionInitial updateQueryOnSave, List<String> fieldsNames){
        String idField = entityMapper().getIdField();
        UpdateOptionFull1 updateQuery = updateQueryOnSave.set(entityMapper().getIdField(), id);
        Map<String, Object> primitives = toPrimitives(toUpdate);

        for(String fieldName : fieldsNames){
            if(fieldName.equalsIgnoreCase(idField)) continue;

            Object value = primitives.get(fieldName);

            updateQuery = updateQuery.andSet(fieldName, value);
        }

        databaseConnection().sendUpdate(
                updateQuery.where(idField).equal(id)
        );
    }

    @SneakyThrows
    protected void persistNewObject(T toPersist, List<String> fieldNames, InsertOptionFinal insertQueryOnSave){
        List<Object> valuesToAddInQuery = new ArrayList<>();
        Map<String, Object> toPrimitves = toPrimitives(toPersist);

        for(String fieldName : fieldNames){
            Object value = toPrimitves.get(fieldName);

            valuesToAddInQuery.add(value);
        }

        databaseConnection().sendUpdate(
                insertQueryOnSave.values(valuesToAddInQuery.toArray(new Object[0]))
        );
    }
}
