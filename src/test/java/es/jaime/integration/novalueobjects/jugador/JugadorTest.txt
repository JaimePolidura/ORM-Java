package es.jaime.integration.novalueobjects.jugador;

import es.jaime.integration.novalueobjects.Model;
import es.jaime.integration.novalueobjects.Repository;
import org.testng.annotations.Test;

import java.util.UUID;

public final class JugadorTest {
    private static final String DB_LINK = "jdbc:mysql://localhost:3306/ormjavatest?user=root&password=&useSSL=false&allowPublicKeyRetrieval=true";

    private final JugadorRepository repository;

    public JugadorTest() {
        this.repository = new JugadorRepository(DB_LINK);
    }

    @Test
    public void test(){
        UUID jugadorId = UUID.randomUUID();
        Jugador jugador = new Jugador(jugadorId, "jaime", 0, 0,
                0, 0, 0, 0, 9999);

        this.repository.save(jugador);

        this.repository.save(jugador.decrementPixelcoinsBy(10)
                .incrementGastosBy(10));

        this.repository.save(jugador);
    }
}
