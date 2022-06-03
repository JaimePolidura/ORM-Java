package es.jaime.integration.novalueobjects;

import es.jaime.configuration.DatabaseConfiguration;
import es.jaime.mapper.EntityMapper;
import es.jaime.repository.DataBaseRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class Repository extends DataBaseRepository<Model, UUID> {
    public Repository(String url) {
        super(new DatabaseConfiguration() {
            @Override
            protected String url() {
                return url;
            }
        });
    }

    public void save(Model model){
        super.save(model);
    }

    public Optional<Model> findById(UUID id){
        return super.findById(id);
    }

    public List<Model> findAll(){
        return super.all();
    }

    public void deleteById(int id){

    }

    @Override
    protected EntityMapper<Model> entityMapper() {
        return EntityMapper.table("model")
                .idField("id")
                .classToMap(Model.class)
                .build();
    }

    @Override
    public Model buildObjectFromResultSet(ResultSet rs) throws SQLException {
        return new Model(
                UUID.fromString(rs.getString("id")),
                rs.getString("keymodel"),
                rs.getString("valuemodel")
        );
    }
}