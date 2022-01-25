package valueobjects;

import es.jaime.connection.DatabaseConnection;
import es.jaime.mapper.EntityMapper;
import es.jaime.repository.DataBaseRepositoryValueObjects;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.function.Function;

public class Example extends DataBaseRepositoryValueObjects<Cuenta, CuentaId> {
    public Example() {
        super(new DatabaseConnectity());
    }

    public static void main(String[] args) {
        Example example = new Example();

        System.out.println(example.findById(new CuentaId(3)).get().getUsername().value());

        example.all().forEach(c -> System.out.println(c.getUsername().value()));

        Cuenta cuenta = Cuenta.create(
                1,"a", "121212", 1, "USER"
        );

        example.save(cuenta);

        example.deleteById(new CuentaId(1));
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
        return Cuenta.create(
                result.getInt("id"),
                result.getString("username"),
                result.getString("password"),
                result.getInt("active"),
                result.getString("roles")
        );
    }

    @Override
    public Function<CuentaId, Object> idValueObjectToIdPrimitive() {
        return CuentaId::value;
    }

    @Override
    public Map<String, Object> toValueObjects(Cuenta aggregate) {
        return Map.of(
                "id", aggregate.getId(),
                "username", aggregate.getUsername(),
                "password", aggregate.getPassword(),
                "active", aggregate.getActive(),
                "roles", aggregate.getRoles()
        );
    }

    @Override
    public Map<String, Object> toPrimitives(Cuenta aggregate) {
        return Map.of(
                "id", aggregate.getId().value(),
                "username", aggregate.getUsername().value(),
                "password", aggregate.getPassword().value(),
                "active", aggregate.getActive().value(),
                "roles", aggregate.getRoles().value()
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
