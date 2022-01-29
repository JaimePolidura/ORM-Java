package es.jaime.repository;

import es.jaime.configuration.DatabaseConfiguration;
import es.jaime.mapper.EntityMapper;
import es.jaime.utils.IntrospectionUtils;
import es.jaimetruman.delete.Delete;
import es.jaimetruman.insert.Insert;
import es.jaimetruman.insert.InsertOptionFinal;
import es.jaimetruman.select.Select;
import es.jaimetruman.update.Update;
import es.jaimetruman.update.UpdateOptionInitial;
import lombok.SneakyThrows;

import java.util.*;
import java.util.function.Function;

public abstract class DataBaseRepositoryValueObjects<T, I> extends Repostitory<T, I> {
    protected final DatabaseConfiguration databaseConnection;

    private final EntityMapper entityMapper;
    private final String table;
    private final String idField;
    private final List<String> fieldsNames;
    private final InsertOptionFinal insertQueryOnSave;
    private final UpdateOptionInitial updateQueryOnSave;

    protected DataBaseRepositoryValueObjects(DatabaseConfiguration databaseConnection) {
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
    protected Optional<T> findById(I id) {
        return buildObjectFromQuery(
                Select.from(table).where(idField).equal(idValueObjectToIdPrimitive().apply(id))
        );
    }

    @Override
    @SneakyThrows
    protected void deleteById(I id) {
        databaseConnection.sendUpdate(
                Delete.from(table).where(idField).equal(idValueObjectToIdPrimitive().apply(id))
        );
    }

    @Override
    protected void save(T toPersist) {
        I idValueObject = (I) toValueObjects(toPersist).get(idField);
        boolean exists = findById(idValueObject).isPresent();

        if(exists){
            Object idPrimitive = idValueObjectToIdPrimitive().apply(idValueObject);

            super.updateExistingObject(toPersist, idPrimitive, updateQueryOnSave, fieldsNames);
        }else{
            super.persistNewObject(toPersist, fieldsNames, insertQueryOnSave);
        }
    }

    @Override
    protected DatabaseConfiguration databaseConnection() {
        return this.databaseConnection;
    }

    protected abstract Function<I, Object> idValueObjectToIdPrimitive();
    protected abstract Map<String, Object> toValueObjects(T aggregate);
}
