import es.jaime.connection.DatabaseConnection;
import es.jaime.repository.DatabaseRepository;
import es.jaime.mapper.TableMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Example extends DatabaseRepository<Cuenta> {
    public Example() {
        super(new DatabaseConnectity());
    }

    public static void main(String[] args) {
        Example example = new Example();

        System.out.println(example.findById(3));

        example.all().forEach(System.out::println);
    }

    @Override
    public TableMapper<Cuenta> mapper() {
        return TableMapper
                .table("cuentas")
                .idField("id");
    }

    @Override
    public Cuenta buildObject(ResultSet result) throws SQLException {
        return new Cuenta(
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
