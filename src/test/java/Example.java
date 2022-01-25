import es.jaime.connection.DatabaseConnection;
import es.jaime.repository.DataBaseRepositoryValueObjects;
import es.jaime.mapper.TableMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Example extends DataBaseRepositoryValueObjects<Cuenta> {
    public Example() {
        super(new DatabaseConnectity());
    }

    public static void main(String[] args) {
        Example example = new Example();

        example.findById(new CuentaId(3));

        example.all().forEach(c -> System.out.println(c.getUsername().value()));

        Cuenta cuenta = Cuenta.create(
                1,"papa", "121212", 1, "USER"
        );

        example.save(cuenta);

        example.deleteById(new CuentaId(1));
    }

    @Override
    public TableMapper mapper() {
        return TableMapper
                .table("cuentas")
                .idField("id")
                .classToMap(Cuenta.class)
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
