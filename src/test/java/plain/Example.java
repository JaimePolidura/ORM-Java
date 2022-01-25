package plain;

import es.jaime.connection.DatabaseConnection;
import es.jaime.mapper.EntityMapper;
import es.jaime.repository.DataBaseRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class Example extends DataBaseRepository<Cuenta> {
    public Example() {
        super(new DatabaseConnectity());
    }

    public static void main(String[] args) {
        Example example = new Example();

        System.out.println(example.findById(3).get().getUsername());

        example.all().forEach(c -> System.out.println(c.getUsername()));

        Cuenta cuenta = new Cuenta(
                1,"pep", "121212", 1, "USER"
        );

        example.save(cuenta);

        example.deleteById(1);
    }

    @Override
    protected DatabaseConnection databaseConnection() {
        return new DatabaseConnectity();
    }

    @Override
    protected EntityMapper<Cuenta> entityMapper() {
        return EntityMapper
                .table("cuentas")
                .idField("id")
                .classToMap(Cuenta.class)
                .build();
    }

    @Override
    public Cuenta buildObjectFromResultSet(ResultSet result) throws SQLException {
        return new Cuenta(
                result.getInt("id"),
                result.getString("username"),
                result.getString("password"),
                result.getInt("active"),
                result.getString("roles")
        );
    }

    @Override
    public Map<String, Object> toPrimitives(Cuenta aggregate) {
        return Map.of(
                "id", aggregate.getId(),
                "username", aggregate.getUsername(),
                "password", aggregate.getPassword(),
                "active", aggregate.getActive(),
                "roles", aggregate.getRoles()
        );
    }

    private static class DatabaseConnectity extends DatabaseConnection {
        @Override
        protected String url() {
            return String.format(
                    "jdbc:mysql://localhost:3306/%s?user=%s&password=%s&useSSL=false&allowPublicKeyRetrieval=true",
                    "pixelcoins2",
                    "root",
                    ""
            );
        }
    }
}
