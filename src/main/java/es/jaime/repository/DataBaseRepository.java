package es.jaime.repository;

import es.jaime.connection.DatabaseConnection;
import es.jaime.mapper.EntityMapper;
import es.jaime.utils.IntrospectionUtils;
import es.jaimetruman.delete.Delete;
import es.jaimetruman.insert.Insert;
import es.jaimetruman.insert.InsertOptionFinal;
import es.jaimetruman.select.Select;
import es.jaimetruman.update.Update;
import es.jaimetruman.update.UpdateOptionFull1;
import es.jaimetruman.update.UpdateOptionInitial;
import lombok.SneakyThrows;

import java.util.*;

public abstract class DataBaseRepository<T> extends Repostitory<T> {
    protected final DatabaseConnection databaseConnection;

    private final EntityMapper entityMapper;
    private final String table;
    private final String idField;
    private final List<String> fieldsNames;
    protected final InsertOptionFinal insertQueryOnSave;
    protected final UpdateOptionInitial updateQueryOnSave;

    protected DataBaseRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
        this.entityMapper = entityMapper();
        this.table = entityMapper.getTable();
        this.idField = entityMapper.getIdField();
        this.fieldsNames = IntrospectionUtils.getFieldsNames(entityMapper.getClassToMap());
        this.insertQueryOnSave = Insert.table(table).fields(fieldsNames.toArray(new String[0]));
        this.updateQueryOnSave = Update.table(table);
    }

    @Override
    @SneakyThrows
    protected List<T> all() {
        return buildListFromQuery(Select.from(entityMapper.getTable()));
    }

    @Override
    protected Optional<T> findById(Object id) {
        return buildObjectFromQuery(
                Select.from(table).where(idField).equal(id)
        );
    }

    @Override
    @SneakyThrows
    protected void deleteById(Object id) {
        databaseConnection.sendUpdate(
                Delete.from(table).where(idField).equal(id)
        );
    }

    @Override
    protected void save(T toPersist) {
        Object id = toPrimitives(toPersist).get(idField);
        boolean exists = findById(id).isPresent();

        if(exists){
            updateExistingObject(toPersist, id);
        }else{
            persistNewObject(toPersist);
        }
    }

    @SneakyThrows
    private void updateExistingObject(T toUpdate, Object id){
        UpdateOptionFull1 updateQuery = this.updateQueryOnSave.set(idField, id);
        Map<String, Object> primitives = toPrimitives(toUpdate);

        for(String fieldName : fieldsNames){
            if(fieldName.equalsIgnoreCase(idField)) continue;

            Object value = primitives.get(fieldName);

            updateQuery = updateQuery.andSet(fieldName, value);
        }

        databaseConnection.sendUpdate(
                updateQuery.where(idField).equal(id)
        );
    }

    @SneakyThrows
    private void persistNewObject(T toPersist){
        List<Object> valuesToAddInQuery = new ArrayList<>();
        Map<String, Object> toPrimitves = toPrimitives(toPersist);

        for(String fieldName : fieldsNames){
            Object value = toPrimitves.get(fieldName);

            valuesToAddInQuery.add(value);
        }

        databaseConnection.sendUpdate(
                insertQueryOnSave.values(valuesToAddInQuery.toArray(new Object[0]))
        );
    }

    public abstract Map<String, Object> toPrimitives(T aggregate);
}
