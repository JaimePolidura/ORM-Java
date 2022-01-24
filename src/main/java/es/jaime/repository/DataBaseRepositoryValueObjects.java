package es.jaime.repository;

import es.jaime.connection.DatabaseConnection;
import es.jaimetruman.insert.Insert;
import es.jaimetruman.insert.InsertOptionFinal;
import es.jaimetruman.update.Update;
import es.jaimetruman.update.UpdateOptionFull1;
import es.jaimetruman.update.UpdateOptionInitial;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static es.jaime.utils.ReflectionUtils.*;

public abstract class DataBaseRepositoryValueObjects<T> extends DatabaseRepository<T> {
    private final InsertOptionFinal insertQueryOnSave;
    private final UpdateOptionInitial updateQueryOnSave;
    private final DatabaseConnection dataBaseConnection;

    public DataBaseRepositoryValueObjects(DatabaseConnection dataBaseConnection) {
        super(dataBaseConnection);

        this.dataBaseConnection = dataBaseConnection;
        this.insertQueryOnSave = Insert.table(table())
                .fields(mapper().getFields().toArray(new String[0]));
        this.updateQueryOnSave = Update.table(mapper().getTable());
    }

    @Override
    protected Optional<T> findById(Object id) {
        return super.findById(invokeMethod(id, valueObjectField()));
    }

    @Override
    protected void save(T toPersist) {
        Object id = invokeGetterMethod(toPersist, idField());

        boolean exists = findById(id).isPresent();

        if(exists)
            updateExistingObject(toPersist, id);
        else
            persistNewObject(toPersist);
    }

    @SneakyThrows
    private void updateExistingObject(T toUpdate, Object idValueObject){
        Object id = invokeMethod(idValueObject, valueObjectField());
        UpdateOptionFull1 updateOptionFull1 = this.updateQueryOnSave.set(idField(), id);

        for(String field : getFields()){
            if(field.equalsIgnoreCase(idField())) continue;

            updateOptionFull1 = updateOptionFull1.andSet(field, invokeValueObjectMethodGetter(toUpdate, field, valueObjectField()));
        }

        dataBaseConnection.sendUpdate(
                updateOptionFull1.where(idField()).equal(id)
        );
    }

    @SneakyThrows
    private void persistNewObject(T toPersist){
        List<Object> valuesToAddInQuery = new ArrayList<>();

        for(String field : getFields()){
            Object value = invokeValueObjectMethodGetter(toPersist, field, valueObjectField());

            valuesToAddInQuery.add(value);
        }

        dataBaseConnection.sendUpdate(
                insertQueryOnSave.values(valuesToAddInQuery.toArray(new Object[0]))
        );
    }

    @Override
    protected void deleteById(Object id) {
        super.deleteById(invokeMethod(id, valueObjectField()));
    }

    private String table(){
        return this.mapper().getTable();
    }

    private String idField(){
        return this.mapper().getIdField();
    }

    private List<String> getFields(){
        return this.mapper().getFields();
    }

    private boolean usingValueObjects(){
        return this.mapper().isUsingValueObjects();
    }

    private String valueObjectField() {
        return this.mapper().getValueObjectField();
    }
}
