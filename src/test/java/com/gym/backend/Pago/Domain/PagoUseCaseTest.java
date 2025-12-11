package com.gym.backend.Pago.Domain;

import com.gym.backend.HistorialPagos.Domain.HistorialPagoUseCase;
import com.gym.backend.Pago.Domain.Enum.EstadoPago;
import com.gym.backend.Pago.Domain.Enum.MetodoPago;
import com.gym.backend.Pago.Domain.Exceptions.PagoDuplicateException;
import com.gym.backend.Pago.Domain.Exceptions.PagoNotFoundException;
import com.gym.backend.Pago.Domain.Exceptions.PagoValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para PagoUseCase
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PagoUseCase Tests")
class PagoUseCaseTest {

    @Mock
    private PagoRepositoryPort repo;

    @Mock
    private HistorialPagoUseCase historialPago;

    @InjectMocks
    private PagoUseCase pagoUseCase;

    private Pago pagoValido;

    @BeforeEach
    void setUp() {
        pagoValido = Pago.builder()
                .id(1L)
                .usuarioId(100L)
                .planId(1L)
                .monto(99.99)
                .estado(EstadoPago.PENDIENTE)
                .metodoPago(MetodoPago.EFECTIVO)
                .fechaCreacion(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("Registrar Pago Tests")
    class RegistrarTests {

        @Test
        @DisplayName("Registrar pago válido exitosamente")
        void registrar_PagoValido_RetornaPagoGuardado() {
            // Arrange
            lenient().when(repo.buscarPorReferencia(any())).thenReturn(Optional.empty());
            when(repo.guardar(any(Pago.class))).thenReturn(pagoValido);
            // NORMALIZADO 3NF: firma con 4 argumentos
            when(historialPago.registrarCambioAutomatico(
                    any(), any(), any(), any())).thenReturn(null);

            // Act
            Pago resultado = pagoUseCase.registrar(pagoValido);

            // Assert
            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
            verify(repo, times(1)).guardar(any(Pago.class));
            verify(historialPago, times(1)).registrarCambioAutomatico(
                    any(), any(), any(), any());
        }

        @Test
        @DisplayName("Registrar pago con referencia duplicada lanza excepción")
        void registrar_ReferenciaDuplicada_LanzaExcepcion() {
            // Arrange
            Pago pagoConReferencia = Pago.builder()
                    .usuarioId(100L)
                    .planId(1L)
                    .monto(99.99)
                    .metodoPago(MetodoPago.EFECTIVO)
                    .referencia("REF-DUPLICADA")
                    .build();

            when(repo.buscarPorReferencia("REF-DUPLICADA"))
                    .thenReturn(Optional.of(pagoValido));

            // Act & Assert
            assertThrows(PagoDuplicateException.class,
                    () -> pagoUseCase.registrar(pagoConReferencia));
            verify(repo, never()).guardar(any());
        }
    }

    @Nested
    @DisplayName("Confirmar Pago Tests")
    class ConfirmarTests {

        @Test
        @DisplayName("Confirmar pago pendiente exitosamente")
        void confirmar_PagoPendiente_RetornaPagoConfirmado() {
            // Arrange
            when(repo.buscarPorId(1L)).thenReturn(Optional.of(pagoValido));
            when(repo.actualizar(any(Pago.class))).thenAnswer(i -> i.getArgument(0));
            // NORMALIZADO 3NF: firma con 4 argumentos
            when(historialPago.registrarCambioAutomatico(
                    any(), any(), any(), any())).thenReturn(null);

            // Act
            Pago resultado = pagoUseCase.confirmar(1L);

            // Assert
            assertEquals(EstadoPago.CONFIRMADO, resultado.getEstado());
            verify(repo, times(1)).actualizar(any(Pago.class));
        }

        @Test
        @DisplayName("Confirmar pago ya confirmado lanza excepción")
        void confirmar_PagoYaConfirmado_LanzaExcepcion() {
            // Arrange
            Pago pagoConfirmado = Pago.builder()
                    .id(1L)
                    .usuarioId(100L)
                    .planId(1L)
                    .monto(99.99)
                    .estado(EstadoPago.CONFIRMADO)
                    .metodoPago(MetodoPago.EFECTIVO)
                    .build();

            when(repo.buscarPorId(1L)).thenReturn(Optional.of(pagoConfirmado));

            // Act & Assert
            assertThrows(PagoValidationException.class,
                    () -> pagoUseCase.confirmar(1L));
            verify(repo, never()).actualizar(any());
        }

        @Test
        @DisplayName("Confirmar pago inexistente lanza excepción")
        void confirmar_PagoInexistente_LanzaExcepcion() {
            // Arrange
            when(repo.buscarPorId(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(PagoNotFoundException.class,
                    () -> pagoUseCase.confirmar(999L));
        }
    }

    @Nested
    @DisplayName("Cancelar Pago Tests")
    class CancelarTests {

        @Test
        @DisplayName("Cancelar pago pendiente exitosamente")
        void cancelar_PagoPendiente_RetornaPagoCancelado() {
            // Arrange
            when(repo.buscarPorId(1L)).thenReturn(Optional.of(pagoValido));
            when(repo.actualizar(any(Pago.class))).thenAnswer(i -> i.getArgument(0));
            // NORMALIZADO 3NF: firma con 4 argumentos
            when(historialPago.registrarCambioAutomatico(
                    any(), any(), any(), any())).thenReturn(null);

            // Act
            Pago resultado = pagoUseCase.cancelar(1L);

            // Assert
            assertEquals(EstadoPago.CANCELADO, resultado.getEstado());
            verify(repo, times(1)).actualizar(any(Pago.class));
        }

        @Test
        @DisplayName("Cancelar pago confirmado lanza excepción")
        void cancelar_PagoConfirmado_LanzaExcepcion() {
            // Arrange
            Pago pagoConfirmado = Pago.builder()
                    .id(1L)
                    .usuarioId(100L)
                    .planId(1L)
                    .monto(99.99)
                    .estado(EstadoPago.CONFIRMADO)
                    .metodoPago(MetodoPago.EFECTIVO)
                    .build();

            when(repo.buscarPorId(1L)).thenReturn(Optional.of(pagoConfirmado));

            // Act & Assert
            assertThrows(PagoValidationException.class,
                    () -> pagoUseCase.cancelar(1L));
            verify(repo, never()).actualizar(any());
        }
    }

    @Nested
    @DisplayName("Rechazar Pago Tests")
    class RechazarTests {

        @Test
        @DisplayName("Rechazar pago exitosamente")
        void rechazar_Pago_RetornaPagoRechazado() {
            // Arrange
            when(repo.buscarPorId(1L)).thenReturn(Optional.of(pagoValido));
            when(repo.actualizar(any(Pago.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            Pago resultado = pagoUseCase.rechazar(1L);

            // Assert
            assertEquals(EstadoPago.RECHAZADO, resultado.getEstado());
            verify(repo, times(1)).actualizar(any(Pago.class));
        }
    }

    @Nested
    @DisplayName("Obtener y Listar Pagos Tests")
    class ObtenerListarTests {

        @Test
        @DisplayName("Obtener pago por ID existente")
        void obtener_IdExistente_RetornaPago() {
            // Arrange
            when(repo.buscarPorId(1L)).thenReturn(Optional.of(pagoValido));

            // Act
            Pago resultado = pagoUseCase.obtener(1L);

            // Assert
            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
        }

        @Test
        @DisplayName("Obtener pago por ID inexistente lanza excepción")
        void obtener_IdInexistente_LanzaExcepcion() {
            // Arrange
            when(repo.buscarPorId(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(PagoNotFoundException.class,
                    () -> pagoUseCase.obtener(999L));
        }

        @Test
        @DisplayName("Listar todos los pagos")
        void listar_RetornaListaDePagos() {
            // Arrange
            List<Pago> pagos = Arrays.asList(pagoValido, pagoValido);
            when(repo.listar()).thenReturn(pagos);

            // Act
            List<Pago> resultado = pagoUseCase.listar();

            // Assert
            assertNotNull(resultado);
            assertEquals(2, resultado.size());
        }

        @Test
        @DisplayName("Listar pagos por usuario")
        void listarPorUsuario_RetornaListaDeUsuario() {
            // Arrange
            List<Pago> pagos = Arrays.asList(pagoValido);
            when(repo.listarPorUsuario(100L)).thenReturn(pagos);

            // Act
            List<Pago> resultado = pagoUseCase.listarPorUsuario(100L);

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
        }

        @Test
        @DisplayName("Listar pagos pendientes")
        void listarPendientes_RetornaSoloPendientes() {
            // Arrange
            List<Pago> pagosPendientes = Arrays.asList(pagoValido);
            when(repo.listarPagosPendientes()).thenReturn(pagosPendientes);

            // Act
            List<Pago> resultado = pagoUseCase.listarPendientes();

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
        }

        @Test
        @DisplayName("Listar pagos por estado")
        void listarPorEstado_RetornaPorEstado() {
            // Arrange
            List<Pago> pagosConfirmados = Arrays.asList(pagoValido);
            when(repo.listarPorEstado(EstadoPago.CONFIRMADO))
                    .thenReturn(pagosConfirmados);

            // Act
            List<Pago> resultado = pagoUseCase.listarPorEstado(EstadoPago.CONFIRMADO);

            // Assert
            assertNotNull(resultado);
        }
    }

    @Nested
    @DisplayName("Asignar Código Tests")
    class AsignarCodigoTests {

        @Test
        @DisplayName("Asignar código a pago exitosamente")
        void asignarCodigo_AsignaCorrectamente() {
            // Arrange
            when(repo.buscarPorId(1L)).thenReturn(Optional.of(pagoValido));
            when(repo.actualizar(any(Pago.class))).thenReturn(pagoValido);

            // Act
            pagoUseCase.asignarCodigo(1L, "COD-123");

            // Assert
            verify(repo, times(1)).actualizar(any(Pago.class));
            assertEquals("COD-123", pagoValido.getCodigoPago());
        }
    }

    @Nested
    @DisplayName("Estadísticas Tests")
    class EstadisticasTests {

        @Test
        @DisplayName("Obtener ingresos totales por fecha")
        void obtenerIngresosTotalesPorFecha_RetornaTotal() {
            // Arrange
            LocalDateTime inicio = LocalDateTime.now().minusDays(30);
            LocalDateTime fin = LocalDateTime.now();
            when(repo.obtenerIngresosTotalesPorFecha(inicio, fin)).thenReturn(5000.0);

            // Act
            Double resultado = pagoUseCase.obtenerIngresosTotalesPorFecha(inicio, fin);

            // Assert
            assertEquals(5000.0, resultado);
        }

        @Test
        @DisplayName("Contar pagos por fecha")
        void contarPagosPorFecha_RetornaCantidad() {
            // Arrange
            LocalDateTime inicio = LocalDateTime.now().minusDays(30);
            LocalDateTime fin = LocalDateTime.now();
            when(repo.contarPagosPorEstadoYFecha(EstadoPago.CONFIRMADO, inicio, fin))
                    .thenReturn(10L);

            // Act
            Long resultado = pagoUseCase.contarPagosPorFecha(inicio, fin);

            // Assert
            assertEquals(10L, resultado);
        }

        @Test
        @DisplayName("Obtener estadísticas mensuales")
        void obtenerEstadisticasMensual_RetornaEstadisticas() {
            // Arrange
            when(repo.obtenerIngresosTotalesPorFecha(any(), any())).thenReturn(10000.0);
            when(repo.contarPagosPorEstadoYFecha(eq(EstadoPago.CONFIRMADO), any(), any()))
                    .thenReturn(50L);
            when(repo.contarPagosPorEstadoYFecha(eq(EstadoPago.PENDIENTE), any(), any()))
                    .thenReturn(5L);
            when(repo.contarPagosPorEstadoYFecha(eq(EstadoPago.RECHAZADO), any(), any()))
                    .thenReturn(2L);

            // Act
            PagoUseCase.EstadisticasMensual resultado = pagoUseCase.obtenerEstadisticasMensual(2024, 12);

            // Assert
            assertNotNull(resultado);
            assertEquals(2024, resultado.año());
            assertEquals(12, resultado.mes());
            assertEquals(10000.0, resultado.ingresosTotales());
            assertEquals(50L, resultado.pagosConfirmados());
            assertEquals(5L, resultado.pagosPendientes());
            assertEquals(2L, resultado.pagosRechazados());
        }
    }
}
