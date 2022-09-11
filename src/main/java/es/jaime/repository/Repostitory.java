package es.jaime.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.jaime.configuration.DatabaseConfiguration;
import es.jaime.mapper.EntityMapper;
import es.jaime.utils.ExceptionUtils;
import es.jaimetruman.ReadQuery;
import es.jaimetruman.WriteQuery;
import es.jaimetruman.insert.InsertOptionFinal;
import es.jaimetruman.update.UpdateOptionFull1;
import es.jaimetruman.update.UpdateOptionInitial;
import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static es.jaime.utils.ExceptionUtils.*;

public abstract class Repostitory<T, I> {
    protected final ObjectMapper objectMapper;
    protected final DatabaseConfiguration databaseConnection;

    public Repostitory(DatabaseConfiguration databaseConnection){
        this.databaseConnection = databaseConnection;
        this.objectMapper = databaseConnection.objectMapper();
    }

    protected abstract List<T> all();
    protected abstract Optional<T> findById(I id);
    protected abstract <O extends T> void save(O toPersist);
    protected abstract void deleteById(I id);

    protected abstract DatabaseConfiguration databaseConfiguration();
    protected abstract EntityMapper<T> entityMapper();
    public abstract T buildObjectFromResultSet(ResultSet resultSet) throws SQLException;

    protected <O extends T> Map<String, Object> toPrimitives(O aggregate){
        return this.objectMapper.convertValue(aggregate, Map.class);
    }

    protected void execute(String query){
        runChecked(() -> this.databaseConfiguration().sendStatement(query));
    }

    protected void execute(WriteQuery query){
        runChecked(() -> this.databaseConfiguration().sendUpdate(query));
    }

    protected List<T> buildListFromQuery(ReadQuery readQuery){
        return this.buildListFromQuery(readQuery.toString());
    }

    @SneakyThrows
    protected List<T> buildListFromQuery(String readQuery){
        try {
            ResultSet resultSet = databaseConfiguration().sendQuery(readQuery);
            List<T> toReturn = new ArrayList<>();

            while (resultSet.next()){
                toReturn.add(buildObjectFromResultSet(resultSet));
            }

            return toReturn;
        } catch (SQLException e) {
            e.printStackTrace();

            return Collections.EMPTY_LIST;
        }
    }

    public Optional<T> buildObjectFromQuery(ReadQuery readQuery){
        return buildObjectFromQuery(readQuery.toString());
    }

    @SneakyThrows
    public Optional<T> buildObjectFromQuery(String readQuery){
        try {
            ResultSet resultSet = databaseConfiguration().sendQuery(readQuery);

            resultSet.next();

            return Optional.ofNullable(buildObjectFromResultSet(resultSet));
        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    protected void updateExistingObject(T toUpdate, Object id, UpdateOptionInitial updateQueryOnSave, List<String> fieldsNames){
        ExceptionUtils.runChecked(() -> {
            String idField = entityMapper().getIdField();

            UpdateOptionFull1 updateQuery = updateQueryOnSave.set(entityMapper().getIdField(), id);
            Map<String, Object> primitives = toPrimitives(toUpdate);

            for(String fieldName : fieldsNames){
                if(fieldName.equalsIgnoreCase(idField)) continue;
                Object value = primitives.get(fieldName);
                updateQuery = updateQuery.andSet(fieldName, value);
            }

            databaseConfiguration().sendUpdate(
                    updateQuery.where(idField).equal(id)
            );
        });
    }

    protected <O extends T> void persistNewObject(O toPersist, List<String> fieldNames, InsertOptionFinal insertQueryOnSave){
        runChecked(() -> {
            List<Object> valuesToAddInQuery = new ArrayList<>();
            Map<String, Object> toPrimitves = toPrimitives(toPersist);

            for(String fieldName : fieldNames){
                Object rawValue = toPrimitves.get(fieldName);
                valuesToAddInQuery.add(this.toJSONIfNeccesary(rawValue));
            }

            runChecked(() -> databaseConfiguration().sendUpdate(insertQueryOnSave.values(valuesToAddInQuery.toArray(new Object[0]))));
        });
    }

    private Object toJSONIfNeccesary(Object rawValue) throws JsonProcessingException {
        if(rawValue instanceof Map || rawValue instanceof Collection)
            return this.objectMapper.writeValueAsString(rawValue);

        return rawValue;
    }
}
