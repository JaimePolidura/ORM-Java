package es.jaime.repository;

import es.jaime.connection.ConnectionManager;
import es.jaime.deserializer.ObjectDeserializerResultset;
import es.jaimetruman.ReadQuery;
import es.jaimetruman.WriteQuery;
import es.jaimetruman.delete.Delete;
import es.jaimetruman.insert.Insert;
import es.jaimetruman.select.Select;
import es.jaimetruman.update.Update;
import es.jaimetruman.update.UpdateOptionFull1;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static es.jaime.javaddd.application.utils.ExceptionUtils.rethrowChecked;
import static es.jaime.javaddd.application.utils.ReflectionUtils.*;

public abstract class Repository<E, I, T> {
    private final ObjectDeserializerResultset objectDeserializerResulset;
    private final ConnectionManager connectionManager;
    private final EntityMapper<E, T> entityMapper;

    public Repository(ConnectionManager connectionManager) {
        this.objectDeserializerResulset = new ObjectDeserializerResultset();
        this.connectionManager = connectionManager;
        this.entityMapper = entityMapper();
    }

    public abstract EntityMapper<E, T> entityMapper();

    public List<E> findAll() {
        return buildListFromQuery(Select.from(entityMapper.getTable()));
    }

    public Optional<E> findById(I id) {
        return buildObjectFromQuery(
                Select.from(entityMapper.getTable()).where(entityMapper.getIdField()).equal(id)
        );
    }

    public void deleteById(I id) {
        rethrowChecked(() -> connectionManager.sendUpdate(Delete.from(entityMapper.getTable())
                .where(entityMapper.getIdField()).equal(id)));
    }

    public <O extends E> void save(O toPersist) {
        Object valueId = rethrowChecked(() -> getFieldValue(toPersist, entityMapper.getIdField()));
        boolean existsInDatabase = findById((I) valueId).isPresent();

        if(existsInDatabase){
            updateExistingObject(toPersist, valueId);
        }else{
            insertNewObject(toPersist);
        }
    }

    protected <O extends E> void updateExistingObject(O toUpdate, Object fieldIdValue) {
        rethrowChecked(() -> {
            List<String> fieldNames = getAllFields(toUpdate.getClass()).stream()
                    .map(Field::getName)
                    .collect(Collectors.toList());
            UpdateOptionFull1 lastUpdateQuery = Update.table(entityMapper.getTable())
                    .set(entityMapper.getIdField(), fieldIdValue);

            for (String fieldName : fieldNames) {
                if(fieldName.equalsIgnoreCase(entityMapper.getIdField())) continue;

                Object fieldValue = getFieldValue(toUpdate, fieldName);
                lastUpdateQuery = lastUpdateQuery.andSet(fieldName, fieldValue);
            }

            connectionManager.sendUpdate(lastUpdateQuery.where(entityMapper.getIdField()).equal(fieldIdValue));
        });
    }

    private <O extends E> void insertNewObject(O toPersist) {
        rethrowChecked(() -> {
            List<String> fieldNames = getAllFields(toPersist.getClass()).stream()
                    .map(Field::getName)
                    .collect(Collectors.toList());
            List<Object> fieldValues = new ArrayList<>(fieldNames.size());

            for (String fieldName : fieldNames) {
                Object fieldValue = getFieldValue(toPersist, fieldName);
                fieldValues.add(fieldValue);
            }
            
            connectionManager.sendUpdate(Insert.table(entityMapper.getTable())
                    .fields(fieldNames.toArray(new String[0]))
                    .values(fieldValues));
        });
    }

    protected void execute(String query){
        rethrowChecked(() -> this.connectionManager.sendStatement(query));
    }

    protected void execute(WriteQuery query){
        rethrowChecked(() -> this.connectionManager.sendUpdate(query));
    }

    public List<E> buildListFromQuery(ReadQuery readQuery) {
        return buildListFromQuery(readQuery.toString());
    }

    public Optional<E> buildObjectFromQuery(ReadQuery readQuery){
        return buildObjectFromQuery(readQuery.toString());
    }

    @SneakyThrows
    public Optional<E> buildObjectFromQuery(String readQuery){
        try {
            ResultSet resultSet = connectionManager.sendQuery(readQuery);
            if(!resultSet.next()){
                return Optional.empty();
            }

            Class<? extends E> mappingClass = entityMapper().getMappingClass(resultSet);
            E deserialized = objectDeserializerResulset.deserialize(resultSet, mappingClass);

            return Optional.of(deserialized);
        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    @SneakyThrows
    public List<E> buildListFromQuery(String readQuery) {
        try {
            ResultSet resultSet = connectionManager.sendQuery(readQuery);
            if(!resultSet.next()){
                return Collections.EMPTY_LIST;
            }

            Class<? extends E> mappingClass = entityMapper().getMappingClass(resultSet);
            List<E> toReturn = new ArrayList<>();

            do {
                toReturn.add(objectDeserializerResulset.deserialize(resultSet, mappingClass));
            }while (resultSet.next());

            return toReturn;
        } catch (SQLException e) {
            e.printStackTrace();

            return Collections.EMPTY_LIST;
        }
    }
}
