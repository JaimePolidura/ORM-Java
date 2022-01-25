package es.jaime.repository;

import es.jaime.connection.DatabaseConnection;
import es.jaime.mapper.TableMapper;
import es.jaimetruman.delete.Delete;
import es.jaimetruman.insert.Insert;
import es.jaimetruman.insert.InsertOptionFinal;
import es.jaimetruman.select.Select;
import es.jaimetruman.select.SelectOptionCompare;
import es.jaimetruman.update.Update;
import es.jaimetruman.update.UpdateOptionFull1;
import es.jaimetruman.update.UpdateOptionInitial;
import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static es.jaime.utils.ReflectionUtils.*;

public abstract class DatabaseRepository<T> extends Repostitory<T>{
    protected final DatabaseConnection dataBaseConnection;

    protected final SelectOptionCompare selectQueryOnFindById;
    protected final InsertOptionFinal insertQueryOnSave;
    protected final UpdateOptionInitial updateQueryOnSave;

    public DatabaseRepository(DatabaseConnection dataBaseConnection) {
        this.dataBaseConnection = dataBaseConnection;

        List<String> fields = mapper().getFields();
        this.insertQueryOnSave = Insert.table(table()).fields(fields.toArray(new String[0]));
        this.updateQueryOnSave = Update.table(mapper().getTable());
        this.selectQueryOnFindById = Select.from(table()).where(idField());
    }

    public abstract TableMapper mapper();
    public abstract T buildObjectFromResultSet(ResultSet resultSet) throws SQLException;

    @Override
    protected List<T> all(){
        try{
            ResultSet resultSet = dataBaseConnection.sendQuery(Select.from(table()));
            List<T> toReturn = new ArrayList<>();

            while (resultSet.next()){
                toReturn.add(buildObjectFromResultSet(resultSet));
            }

            return toReturn;
        }catch (Exception e){
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    protected Optional<T> findById(Object id){
        System.out.println("---------------");
        try {
            ResultSet resultSet = dataBaseConnection.sendQuery(
                    this.selectQueryOnFindById.equal(id)
            );

            resultSet.next();

            return Optional.ofNullable(buildObjectFromResultSet(resultSet));
        }catch (Exception e){
            return Optional.empty();
        }
    }

    @SneakyThrows
    @Override
    protected void save(T toPersist){
        Object id = invokeGetterMethod(toPersist, idField());

        boolean exists = findById(id).isPresent();

        if(exists)
            updateExistingObject(toPersist, id);
        else
            persistNewObject(toPersist);
    }

    @SneakyThrows
    private void updateExistingObject(T toUpdate, Object id){
        UpdateOptionFull1 updateQuery = this.updateQueryOnSave.set(idField(), id);

        for(String field : getFields()){
            if(field.equalsIgnoreCase(idField())) continue;

            Object value = invokeGetterMethod(toUpdate, field);

            updateQuery = updateQuery.andSet(field, value);
        }

        dataBaseConnection.sendUpdate(
                updateQuery.where(idField()).equal(id)
        );
    }

    @SneakyThrows
    private void persistNewObject(T toPersist){
        List<Object> valuesToAddInQuery = new ArrayList<>();

        for(String field : getFields()){
            Object value = invokeGetterMethod(toPersist, field);

            valuesToAddInQuery.add(value);
        }

        dataBaseConnection.sendUpdate(
                insertQueryOnSave.values(valuesToAddInQuery.toArray(new Object[0]))
        );
    }

    @SneakyThrows
    @Override
    protected void deleteById(Object id){
        dataBaseConnection.sendUpdate(
                Delete.from(table()).where(idField()).equal(id)
        );
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
}
