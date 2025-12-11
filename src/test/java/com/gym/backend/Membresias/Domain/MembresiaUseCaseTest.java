package com.gym.backend.Membresias.Domain;

import com.gym.backend.HistorialMembresias.Domain.HistorialMembresiaUseCase;
import com.gym.backend.Membresias.Domain.Enum.EstadoMembresia;
import com.gym.backend.Membresias.Domain.Exceptions.MembresiaNotFoundException;
import com.gym.backend.Membresias.Domain.Exceptions.MembresiaValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para MembresiaUseCase
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MembresiaUseCase Tests")
class MembresiaUseCaseTest {

    @Mock
    private MembresiaRepositoryPort repo;

    @Mock
    private HistorialMembresiaUseCase historialMembresiaUseCase;

    @InjectMocks
    private MembresiaUseCase membresiaUseCase;

    private Membresia membresiaValida;

    @BeforeEach
    void setUp() {
        LocalDate hoy = LocalDate.now();
        membresiaValida = Membresia.builder()
                .id(1L)
                .usuarioId(100L)
                .planId(1L)
                .pagoId(1L)
                .fechaInicio(hoy)
                .fechaFin(hoy.plusDays(30))
                .estado(EstadoMembresia.ACTIVA)
                .fechaCreacion(hoy)
                .fechaActualizacion(hoy)
                .build();
    }

    @Nested
    @DisplayName("Crear Membresía Tests")
    class CrearTests {

        @Test
        @DisplayName("Crear membresía válida exitosamente")
        void crear_MembresiaValida_RetornaGuardada() {
            // Arrange
            when(repo.buscarActivaPorUsuario(any())).thenReturn(Optional.empty());
            when(repo.guardar(any(Membresia.class))).thenReturn(membresiaValida);
            // NORMALIZADO 3NF: firma con 5 argumentos
            when(historialMembresiaUseCase.registrarCambioAutomatico(
                    any(), any(), any(), any(), any())).thenReturn(null);

            // Act
            Membresia resultado = membresiaUseCase.crear(membresiaValida);

            // Assert
            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
            verify(repo, times(1)).guardar(any(Membresia.class));
        }

        @Test
        @DisplayName("Crear membresía cuando usuario ya tiene activa lanza excepción")
        void crear_UsuarioYaTieneActiva_LanzaExcepcion() {
            // Arrange
            when(repo.buscarActivaPorUsuario(100L)).thenReturn(Optional.of(membresiaValida));

            // Act & Assert
            assertThrows(MembresiaValidationException.class,
                    () -> membresiaUseCase.crear(membresiaValida));
            verify(repo, never()).guardar(any());
        }
    }

    @Nested
    @DisplayName("Suspender Membresía Tests")
    class SuspenderTests {

        @Test
        @DisplayName("Suspender membresía activa exitosamente")
        void suspender_MembresiaActiva_RetornaSuspendida() {
            // Arrange
            when(repo.buscarPorId(1L)).thenReturn(Optional.of(membresiaValida));
            when(repo.actualizar(any(Membresia.class))).thenAnswer(i -> i.getArgument(0));
            // NORMALIZADO 3NF: firma con 5 argumentos
            when(historialMembresiaUseCase.registrarCambioAutomatico(
                    any(), any(), any(), any(), any())).thenReturn(null);

            // Act
            Membresia resultado = membresiaUseCase.suspender(1L);

            // Assert
            assertEquals(EstadoMembresia.SUSPENDIDA, resultado.getEstado());
            verify(repo, times(1)).actualizar(any(Membresia.class));
        }

        @Test
        @DisplayName("Suspender membresía inexistente lanza excepción")
        void suspender_MembresiaInexistente_LanzaExcepcion() {
            // Arrange
            when(repo.buscarPorId(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(MembresiaNotFoundException.class,
                    () -> membresiaUseCase.suspender(999L));
        }
    }

    @Nested
    @DisplayName("Reactivar Membresía Tests")
    class ReactivarTests {

        @Test
        @DisplayName("Reactivar membresía suspendida exitosamente")
        void reactivar_MembresiaSuspendida_RetornaActiva() {
            // Arrange
            membresiaValida.setEstado(EstadoMembresia.SUSPENDIDA);
            when(repo.buscarPorId(1L)).thenReturn(Optional.of(membresiaValida));
            when(repo.actualizar(any(Membresia.class))).thenAnswer(i -> i.getArgument(0));
            // NORMALIZADO 3NF: firma con 5 argumentos
            when(historialMembresiaUseCase.registrarCambioAutomatico(
                    any(), any(), any(), any(), any())).thenReturn(null);

            // Act
            Membresia resultado = membresiaUseCase.reactivar(1L);

            // Assert
            assertEquals(EstadoMembresia.ACTIVA, resultado.getEstado());
        }

        @Test
        @DisplayName("Reactivar membresía vencida lanza excepción")
        void reactivar_MembresiaVencida_LanzaExcepcion() {
            // Arrange
            LocalDate hoy = LocalDate.now();
            membresiaValida.setFechaFin(hoy.minusDays(10)); // Vencida
            membresiaValida.setEstado(EstadoMembresia.VENCIDA);
            when(repo.buscarPorId(1L)).thenReturn(Optional.of(membresiaValida));

            // Act & Assert
            assertThrows(MembresiaValidationException.class,
                    () -> membresiaUseCase.reactivar(1L));
        }
    }

    @Nested
    @DisplayName("Cancelar Membresía Tests")
    class CancelarTests {

        @Test
        @DisplayName("Cancelar membresía exitosamente")
        void cancelar_Membresia_RetornaCancelada() {
            // Arrange
            when(repo.buscarPorId(1L)).thenReturn(Optional.of(membresiaValida));
            when(repo.actualizar(any(Membresia.class))).thenAnswer(i -> i.getArgument(0));
            // NORMALIZADO 3NF: firma con 5 argumentos
            when(historialMembresiaUseCase.registrarCambioAutomatico(
                    any(), any(), any(), any(), any())).thenReturn(null);

            // Act
            Membresia resultado = membresiaUseCase.cancelar(1L);

            // Assert
            assertEquals(EstadoMembresia.CANCELADA, resultado.getEstado());
        }
    }

    @Nested
    @DisplayName("Obtener y Listar Tests")
    class ObtenerListarTests {

        @Test
        @DisplayName("Obtener membresía por ID existente")
        void obtener_IdExistente_RetornaMembresia() {
            // Arrange
            when(repo.buscarPorId(1L)).thenReturn(Optional.of(membresiaValida));

            // Act
            Membresia resultado = membresiaUseCase.obtener(1L);

            // Assert
            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
        }

        @Test
        @DisplayName("Obtener membresía por ID inexistente lanza excepción")
        void obtener_IdInexistente_LanzaExcepcion() {
            // Arrange
            when(repo.buscarPorId(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(MembresiaNotFoundException.class,
                    () -> membresiaUseCase.obtener(999L));
        }

        @Test
        @DisplayName("Listar todas las membresías")
        void listar_RetornaListaDeMembresias() {
            // Arrange
            List<Membresia> membresias = Arrays.asList(membresiaValida, membresiaValida);
            when(repo.listar()).thenReturn(membresias);

            // Act
            List<Membresia> resultado = membresiaUseCase.listar();

            // Assert
            assertNotNull(resultado);
            assertEquals(2, resultado.size());
        }

        @Test
        @DisplayName("Listar membresías por usuario")
        void listarPorUsuario_RetornaListaDeUsuario() {
            // Arrange
            List<Membresia> membresias = Arrays.asList(membresiaValida);
            when(repo.listarPorUsuario(100L)).thenReturn(membresias);

            // Act
            List<Membresia> resultado = membresiaUseCase.listarPorUsuario(100L);

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
        }

        @Test
        @DisplayName("Listar membresías activas")
        void listarActivas_RetornaSoloActivas() {
            // Arrange
            List<Membresia> membresiasActivas = Arrays.asList(membresiaValida);
            when(repo.listarPorEstado(EstadoMembresia.ACTIVA)).thenReturn(membresiasActivas);

            // Act
            List<Membresia> resultado = membresiaUseCase.listarActivas();

            // Assert
            assertNotNull(resultado);
        }
    }

    @Nested
    @DisplayName("Verificar Acceso Tests")
    class VerificarAccesoTests {

        @Test
        @DisplayName("verificarAcceso retorna true para membresía activa")
        void verificarAcceso_MembresiaActiva_RetornaTrue() {
            // Arrange
            when(repo.buscarActivaPorUsuario(100L)).thenReturn(Optional.of(membresiaValida));

            // Act
            boolean resultado = membresiaUseCase.verificarAcceso(100L);

            // Assert
            assertTrue(resultado);
        }

        @Test
        @DisplayName("verificarAcceso retorna false para usuario sin membresía")
        void verificarAcceso_SinMembresia_RetornaFalse() {
            // Arrange
            when(repo.buscarActivaPorUsuario(100L)).thenReturn(Optional.empty());

            // Act
            boolean resultado = membresiaUseCase.verificarAcceso(100L);

            // Assert
            assertFalse(resultado);
        }
    }

    @Nested
    @DisplayName("Extender Membresía Tests")
    class ExtenderTests {

        @Test
        @DisplayName("Extender membresía exitosamente")
        void extender_Membresia_ExtiendeFecha() {
            // Arrange
            LocalDate fechaFinOriginal = membresiaValida.getFechaFin();
            when(repo.buscarPorId(1L)).thenReturn(Optional.of(membresiaValida));
            when(repo.actualizar(any(Membresia.class))).thenAnswer(i -> i.getArgument(0));
            // NORMALIZADO 3NF: firma con 5 argumentos
            when(historialMembresiaUseCase.registrarCambioAutomatico(
                    any(), any(), any(), any(), any())).thenReturn(null);

            // Act
            Membresia resultado = membresiaUseCase.extender(1L, 30);

            // Assert
            assertEquals(fechaFinOriginal.plusDays(30), resultado.getFechaFin());
        }
    }
}
