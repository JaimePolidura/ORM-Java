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

public abstract class DataBaseRepository<T, I> extends Repostitory<T, I> {
    protected final DatabaseConfiguration databaseConnection;

    private final EntityMapper entityMapper;
    private final String table;
    private final String idField;
    private final List<String> fieldsNames;
    private final InsertOptionFinal insertQueryOnSave;
    private final UpdateOptionInitial updateQueryOnSave;

    protected DataBaseRepository(DatabaseConfiguration databaseConnection) {
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
                Select.from(table).where(idField).equal(id.toString())
        );
    }

    @Override
    @SneakyThrows
    protected void deleteById(I id) {
        databaseConnection.sendUpdate(
                Delete.from(table).where(idField).equal(id.toString())
        );
    }

    @Override
    protected void save(T toPersist) {
        I id = (I) toPrimitives(toPersist).get(idField);
        boolean exists = findById(id).isPresent();

        if(exists)
            super.updateExistingObject(toPersist, id, updateQueryOnSave, fieldsNames);
        else
            super.persistNewObject(toPersist, fieldsNames, insertQueryOnSave);
    }

    @Override
    protected DatabaseConfiguration databaseConnection() {
        return this.databaseConnection;
    }
}
