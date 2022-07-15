package es.jaime.integration.novalueobjects;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.jaime.configuration.DatabaseConfiguration;
import es.jaime.mapper.EntityMapper;
import es.jaime.repository.DataBaseRepository;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class Repository extends DataBaseRepository<Model, UUID> {
    private static final ObjectMapper MAPPER = new ObjectMapper();

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

    public void deleteById(UUID id){

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
        try {
            return new Model(
                    UUID.fromString(rs.getString("id")),
                    rs.getString("keymodel"),
                    rs.getString("valuemodel"),
                    MAPPER.readValue(rs.getString("lista"), new TypeReference<List<String>>(){})
            );
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
