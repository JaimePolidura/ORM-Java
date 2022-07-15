package es.jaime.integration.novalueobjects.jugador;

import es.jaime.configuration.DatabaseConfiguration;
import es.jaime.mapper.EntityMapper;
import es.jaime.repository.DataBaseRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public final class JugadorRepository extends DataBaseRepository<Jugador, UUID> {

    public JugadorRepository(String url) {
        super(new DatabaseConfiguration() {
            @Override
            protected String url() {
                return url;
            }
        });
    }

    @Override
    protected void save(Jugador toPersist) {
        super.save(toPersist);
    }

    @Override
    protected EntityMapper<Jugador> entityMapper() {
        return EntityMapper.table("jugadores")
                .idField("jugadorId")
                .classToMap(Jugador.class)
                .build();
    }

    @Override
    public Jugador buildObjectFromResultSet(ResultSet rs) throws SQLException {
        return new Jugador(
                UUID.fromString(rs.getString("jugadorId")),
                rs.getString("nombre"),
                rs.getDouble("pixelcoins"),
                rs.getInt("nventas"),
                rs.getDouble("ingresos"),
                rs.getDouble("gastos"),
                rs.getInt("ninpagosDeuda"),
                rs.getInt("npagosDeuda"),
                rs.getInt("numeroVerificacionCuenta")
        );
    }
}
