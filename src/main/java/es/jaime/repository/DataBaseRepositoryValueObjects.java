package es.jaime.repository;

import es.jaime.connection.DatabaseConnection;
import es.jaimetruman.update.UpdateOptionFull1;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static es.jaime.utils.ReflectionUtils.*;

public abstract class DataBaseRepositoryValueObjects<T> extends DatabaseRepository<T> {
    public DataBaseRepositoryValueObjects(DatabaseConnection dataBaseConnection) {
        super(dataBaseConnection);
    }

    @Override
    protected Optional<T> findById(Object id) {
        return super.findById(invokeMethod(id, valueObjectField()));
    }

    @Override
    protected void save(T toPersist) {
        Object idValueObject = invokeGetterMethod(toPersist, idField());

        boolean exists = findById(idValueObject).isPresent();

        if(exists)
            updateExistingObject(toPersist, idValueObject);
        else
            persistNewObject(toPersist);
    }

    @SneakyThrows
    private void updateExistingObject(T toUpdate, Object idValueObject){
        Object id = invokeMethod(idValueObject, valueObjectField());
        UpdateOptionFull1 updateQuery = this.updateQueryOnSave.set(idField(), id);

        for(String field : getFields()){
            if(field.equalsIgnoreCase(idField())) continue;

            Object valueObjectValue = invokeValueObjectMethodGetter(toUpdate, field, valueObjectField());

            updateQuery = updateQuery.andSet(field, valueObjectValue);
        }

        dataBaseConnection.sendUpdate(
                updateQuery.where(idField()).equal(id)
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

    private String idField(){
        return this.mapper().getIdField();
    }

    private List<String> getFields(){
        return this.mapper().getFields();
    }

    private String valueObjectField() {
        return this.mapper().getValueObjectField();
    }
}
