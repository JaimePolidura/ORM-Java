package es.jaime.integration.novalueobjects.jugador;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
public final class Jugador {
    @Getter private final UUID jugadorId;
    @Getter private final String nombre;
    @Getter private final double pixelcoins;
    @Getter private final int nventas;
    @Getter private final double ingresos;
    @Getter private final double gastos;
    @Getter private final int ninpagosDeuda;
    @Getter private final int npagosDeuda;
    @Getter private final int numeroVerificacionCuenta;

    public Jugador withNombre(String nombre){
        return new Jugador(jugadorId, nombre, pixelcoins, nventas, ingresos, gastos,
                ninpagosDeuda, npagosDeuda, numeroVerificacionCuenta);
    }

    public Jugador withNumeroCuenta(int numeroVerificacionCuenta){
        return new Jugador(jugadorId, nombre, pixelcoins, nventas, ingresos, gastos,
                ninpagosDeuda, npagosDeuda, numeroVerificacionCuenta);
    }

    public Jugador incrementGastosBy(double gastos){
        return new Jugador(jugadorId, nombre, pixelcoins, nventas, ingresos, this.gastos + gastos,
                ninpagosDeuda, npagosDeuda, numeroVerificacionCuenta);
    }

    public Jugador incrementIngresosBy(double ingresos){
        return new Jugador(jugadorId, nombre, pixelcoins, nventas, this.ingresos + ingresos, gastos,
                ninpagosDeuda, npagosDeuda, numeroVerificacionCuenta);
    }

    public Jugador incrementNVentas(){
        return new Jugador(jugadorId, nombre, pixelcoins, nventas + 1, ingresos, gastos,
                ninpagosDeuda, npagosDeuda, numeroVerificacionCuenta);
    }

    public Jugador incrementNInpago(){
        return new Jugador(jugadorId, nombre, pixelcoins, nventas, ingresos, gastos,
                ninpagosDeuda + 1, npagosDeuda, numeroVerificacionCuenta);
    }

    public Jugador incrementNPagos(){
        return new Jugador(jugadorId, nombre, pixelcoins, nventas, ingresos, gastos,
                ninpagosDeuda, npagosDeuda + 1, numeroVerificacionCuenta);
    }

    public Jugador incrementPixelcoinsBy(double pixelcoins){
        return new Jugador(jugadorId, nombre, this.pixelcoins + pixelcoins, nventas, ingresos, gastos,
                ninpagosDeuda, npagosDeuda, numeroVerificacionCuenta);
    }

    public Jugador decrementPixelcoinsBy(double pixelcoins){
        return new Jugador(jugadorId, nombre, this.pixelcoins - pixelcoins, nventas, ingresos, gastos,
                ninpagosDeuda, npagosDeuda, numeroVerificacionCuenta);
    }
}
