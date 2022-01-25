package es.jaime.repository;

import es.jaime.connection.DatabaseConnection;
import es.jaime.mapper.EntityMapper;
import es.jaimetruman.ReadQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class Repostitory<T> {
    protected abstract List<T> all();
    protected abstract Optional<T> findById(Object id);
    protected abstract void save(T toPersist);
    protected abstract void deleteById(Object id);

    protected abstract DatabaseConnection databaseConnection();
    protected abstract EntityMapper<T> entityMapper();
    public abstract T buildObjectFromResultSet(ResultSet resultSet) throws SQLException;

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
}
