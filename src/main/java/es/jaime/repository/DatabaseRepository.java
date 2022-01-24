package es.jaime.repository;

import es.jaime.connection.DatabaseConnection;
import es.jaime.mapper.TableMapper;
import es.jaimetruman.delete.Delete;
import es.jaimetruman.insert.Insert;
import es.jaimetruman.insert.InsertOptionFinal;
import es.jaimetruman.select.Select;
import es.jaimetruman.update.Update;
import es.jaimetruman.update.UpdateOptionFull1;
import es.jaimetruman.update.UpdateOptionInitial;
import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static es.jaime.utils.ReflectionUtils.*;

public abstract class DatabaseRepository<T> {
    private final DatabaseConnection dataBaseConnection;
    private final InsertOptionFinal insertQueryOnSave;
    private final UpdateOptionInitial updateQueryOnSave;

    public DatabaseRepository(DatabaseConnection dataBaseConnection) {
        this.dataBaseConnection = dataBaseConnection;

        this.insertQueryOnSave = Insert.table(table())
                .fields(mapper().getFields().toArray(new String[0]));
        this.updateQueryOnSave = Update.table(mapper().getTable());
    }

    public abstract TableMapper<T> mapper();
    public abstract T buildObject(ResultSet resultSet) throws SQLException;

    protected List<T> all(){
        try{
            ResultSet resultSet = dataBaseConnection.executeQuery(Select.from(table()));
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
    protected void save(T toPersist){
        Object id = invokeGetterMethod(toPersist, idField());
        boolean exists = findById(id).isPresent();

        if(exists){
            updateExistingObject(toPersist, id);
        }else{
            persistNewObject(toPersist);
        }
    }

    @SneakyThrows
    private void updateExistingObject(T toUpdate, Object id){
        UpdateOptionInitial updateQuery = this.updateQueryOnSave;

        UpdateOptionFull1 updateOptionFull1 = updateQuery.set(idField(), id);

        for(String field : getFields()){
            //TODO IMprove this shity code
            if(field.equalsIgnoreCase(idField())) continue;

            if(usingValueObjects()){
                updateOptionFull1 = updateOptionFull1.andSet(field, invokeValueObjectMethodGetter(toUpdate, field, mapper().getValueObjectField()));
            }else{
                updateOptionFull1 = updateOptionFull1.andSet(field, invokeGetterMethod(toUpdate, field));
            }
        }

        String query = updateOptionFull1.where(idField())
                .equal(id)
                .build();

        dataBaseConnection.executeUpdate(query);
    }

    @SneakyThrows
    private void persistNewObject(T toPersist){
        List<Object> valuesToAddInQuery = new ArrayList<>();

        for(String field : getFields()){
            Object value;

            if(usingValueObjects()){
                value = invokeValueObjectMethodGetter(toPersist, field, mapper().getValueObjectField());
            }else{
                value = invokeGetterMethod(toPersist, field);
            }

            valuesToAddInQuery.add(value);
        }

        String insertQuery = this.insertQueryOnSave.values(valuesToAddInQuery.toArray(new Object[0]));
        dataBaseConnection.executeUpdate(insertQuery);
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

    private List<String> getFields(){
        return this.mapper().getFields();
    }

    private boolean usingValueObjects(){
        return this.mapper().isUsingValueObjects();
    }
}
