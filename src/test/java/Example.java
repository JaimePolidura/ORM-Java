import es.jaime.connection.DatabaseConnection;
import es.jaime.repository.DataBaseRepositoryValueObjects;
import es.jaime.mapper.TableMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Example extends DataBaseRepositoryValueObjects<Cuenta> {
    public Example() {
        super(new DatabaseConnectity());
    }

    public static void main(String[] args) {
        Example example = new Example();

        System.out.println(example.findById(new CuentaId(3)).get().getUsername().value());

        example.all().forEach(c -> System.out.println(c.getUsername().value()));

        Cuenta cuenta = Cuenta.create(
                1,"papa", "121212", 1, "USER"
        );

        example.save(cuenta);

        example.deleteById(new CuentaId(1));
    }

    @Override
    public TableMapper<Cuenta> mapper() {
        return TableMapper
                .table("cuentas")
                .idField("id")
                .fields("id", "username", "password", "active", "roles")
                .usingValueObjects("value")
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
