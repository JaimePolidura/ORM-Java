package es.jaime.repository;

import es.jaime.connection.ConnectionManager;
import lombok.*;
import org.junit.Assert;
import org.junit.Test;

import java.sql.ResultSet;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

public final class RepositoryTest {
//    @Test
//    @SneakyThrows
//    public void findById_empty(){
//        UUID jugadorId = UUID.randomUUID();
//
//        ConnectionManager connectionManager = mock(ConnectionManager.class);
//        RepositoryTested repositoryTested = new RepositoryTested(connectionManager);
//        ResultSet resultSet = mock(ResultSet.class);
//
//        when(connectionManager.sendQuery(any(String.class))).thenReturn(resultSet);
//
//        Optional<Jugador> optional = repositoryTested.findById(jugadorId);
//
//        Assert.assertFalse(optional.isPresent());
//    }
//
//    @Test
//    @SneakyThrows
//    public void findById_notEmpty(){
//        UUID jugadorId = UUID.randomUUID();
//
//        ConnectionManager connectionManager = mock(ConnectionManager.class);
//        RepositoryTested repositoryTested = new RepositoryTested(connectionManager);
//        ResultSet resultSet = mock(ResultSet.class);
//        when(resultSet.getString("jugadorId")).thenReturn(jugadorId.toString());
//        when(resultSet.getString("nombre")).thenReturn("Jaime");
//        when(resultSet.getDouble("dinero")).thenReturn(10.0d);
//
//        when(connectionManager.sendQuery(any(String.class))).thenReturn(resultSet);
//
//        Optional<Jugador> optional = repositoryTested.findById(jugadorId);
//
//        Assert.assertTrue(optional.isPresent());
//        Assert.assertTrue(optional.get().nombre.equalsIgnoreCase("Jaime"));
//        Assert.assertEquals(10.0d, optional.get().dinero, 0.0);
//        Assert.assertEquals(optional.get().jugadorId, jugadorId);
//    }

    public static class RepositoryTested extends Repository<Jugador, UUID, Object> {
        public RepositoryTested(ConnectionManager connectionManager) {
            super(connectionManager);
        }

        @Override
        public EntityMapper<Jugador, Object> entityMapper() {
            return EntityMapper.builder()
                    .table("jugadores")
                    .idField("jugadorId")
                    .classesToMap(Jugador.class)
                    .build();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class Jugador {
        @Getter private UUID jugadorId;
        @Getter private String nombre;
        @Getter private double dinero;
    }
}
