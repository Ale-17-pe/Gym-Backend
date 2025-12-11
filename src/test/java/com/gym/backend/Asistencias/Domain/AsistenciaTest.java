package com.gym.backend.Asistencias.Domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para la entidad de dominio Asistencia
 */
@DisplayName("Asistencia Domain Tests")
class AsistenciaTest {

    private Asistencia.AsistenciaBuilder asistenciaBuilder;

    @BeforeEach
    void setUp() {
        // NORMALIZADO 3NF: sin usuarioId directo
        asistenciaBuilder = Asistencia.builder()
                .id(1L)
                .membresiaId(50L)
                .fechaHora(LocalDateTime.now())
                .tipo("ENTRADA")
                .estado("REGISTRADA")
                .observaciones("Entrada normal")
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now());
    }

    @Nested
    @DisplayName("Tipo de Asistencia")
    class TipoAsistenciaTests {

        @Test
        @DisplayName("esEntrada retorna true cuando tipo es ENTRADA")
        void esEntrada_CuandoTipoEntrada_RetornaTrue() {
            Asistencia asistencia = asistenciaBuilder.tipo("ENTRADA").build();

            assertTrue(asistencia.esEntrada());
        }

        @Test
        @DisplayName("esEntrada retorna false cuando tipo es SALIDA")
        void esEntrada_CuandoTipoSalida_RetornaFalse() {
            Asistencia asistencia = asistenciaBuilder.tipo("SALIDA").build();

            assertFalse(asistencia.esEntrada());
        }

        @Test
        @DisplayName("esSalida retorna true cuando tipo es SALIDA")
        void esSalida_CuandoTipoSalida_RetornaTrue() {
            Asistencia asistencia = asistenciaBuilder.tipo("SALIDA").build();

            assertTrue(asistencia.esSalida());
        }

        @Test
        @DisplayName("esSalida retorna false cuando tipo es ENTRADA")
        void esSalida_CuandoTipoEntrada_RetornaFalse() {
            Asistencia asistencia = asistenciaBuilder.tipo("ENTRADA").build();

            assertFalse(asistencia.esSalida());
        }
    }

    @Nested
    @DisplayName("Verificación de Fecha")
    class VerificacionFechaTests {

        @Test
        @DisplayName("esDelDia retorna true para asistencia del mismo día")
        void esDelDia_MismoDia_RetornaTrue() {
            LocalDateTime ahora = LocalDateTime.now();
            Asistencia asistencia = asistenciaBuilder.fechaHora(ahora).build();

            assertTrue(asistencia.esDelDia(ahora));
        }

        @Test
        @DisplayName("esDelDia retorna true para asistencia temprano en el día")
        void esDelDia_TempranoEnElDia_RetornaTrue() {
            LocalDateTime ahora = LocalDateTime.now();
            LocalDateTime tempranoHoy = ahora.toLocalDate().atTime(6, 30, 0);
            Asistencia asistencia = asistenciaBuilder.fechaHora(tempranoHoy).build();

            assertTrue(asistencia.esDelDia(ahora));
        }

        @Test
        @DisplayName("esDelDia retorna true para asistencia tarde en el día")
        void esDelDia_TardeEnElDia_RetornaTrue() {
            LocalDateTime ahora = LocalDateTime.now();
            LocalDateTime tardeHoy = ahora.toLocalDate().atTime(22, 30, 0);
            Asistencia asistencia = asistenciaBuilder.fechaHora(tardeHoy).build();

            assertTrue(asistencia.esDelDia(ahora));
        }

        @Test
        @DisplayName("esDelDia retorna false para asistencia de día anterior")
        void esDelDia_DiaAnterior_RetornaFalse() {
            LocalDateTime ahora = LocalDateTime.now();
            LocalDateTime ayer = ahora.minusDays(1);
            Asistencia asistencia = asistenciaBuilder.fechaHora(ayer).build();

            assertFalse(asistencia.esDelDia(ahora));
        }

        @Test
        @DisplayName("esDelDia retorna false para asistencia de día siguiente")
        void esDelDia_DiaSiguiente_RetornaFalse() {
            LocalDateTime ahora = LocalDateTime.now();
            LocalDateTime manana = ahora.plusDays(1);
            Asistencia asistencia = asistenciaBuilder.fechaHora(manana).build();

            assertFalse(asistencia.esDelDia(ahora));
        }
    }

    @Nested
    @DisplayName("Cambios de Estado")
    class CambiosEstadoTests {

        @Test
        @DisplayName("cancelar cambia estado a CANCELADA")
        void cancelar_CambiaEstado() {
            Asistencia asistencia = asistenciaBuilder.estado("REGISTRADA").build();

            asistencia.cancelar();

            assertEquals("CANCELADA", asistencia.getEstado());
            assertNotNull(asistencia.getFechaActualizacion());
        }

        @Test
        @DisplayName("cancelar actualiza fechaActualizacion")
        void cancelar_ActualizaFecha() {
            LocalDateTime fechaAnterior = LocalDateTime.now().minusDays(1);
            Asistencia asistencia = asistenciaBuilder
                    .estado("REGISTRADA")
                    .fechaActualizacion(fechaAnterior)
                    .build();

            asistencia.cancelar();

            assertTrue(asistencia.getFechaActualizacion().isAfter(fechaAnterior));
        }
    }

    @Nested
    @DisplayName("Propiedades de Asistencia")
    class PropiedadesTests {

        @Test
        @DisplayName("Asistencia se crea con todos los campos")
        void crearAsistencia_TodosLosCampos() {
            Asistencia asistencia = asistenciaBuilder.build();

            assertNotNull(asistencia.getId());
            // ELIMINADO 3NF: usuarioId se obtiene de la membresia
            assertNotNull(asistencia.getMembresiaId());
            assertNotNull(asistencia.getFechaHora());
            assertNotNull(asistencia.getTipo());
            assertNotNull(asistencia.getEstado());
            assertNotNull(asistencia.getObservaciones());
        }

        @Test
        @DisplayName("Asistencia permite modificar observaciones")
        void modificarObservaciones() {
            Asistencia asistencia = asistenciaBuilder.build();

            asistencia.setObservaciones("Nueva observación");

            assertEquals("Nueva observación", asistencia.getObservaciones());
        }
    }
}
