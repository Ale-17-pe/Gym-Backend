package com.gym.backend.Usuarios.Domain;

import com.gym.backend.Usuarios.Domain.Enum.Genero;
import com.gym.backend.Usuarios.Domain.Enum.Rol;
import com.gym.backend.Usuarios.Domain.Exceptions.UsuarioValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para la entidad de dominio Usuario
 */
@DisplayName("Usuario Domain Tests")
class UsuarioTest {

    private Usuario.UsuarioBuilder usuarioBuilder;

    @BeforeEach
    void setUp() {
        usuarioBuilder = Usuario.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan.perez@example.com")
                .dni("12345678")
                .password("password123")
                .telefono("987654321")
                .direccion("Av. Principal 123")
                .genero(Genero.MASCULINO)
                .rol(Rol.CLIENTE)
                .activo(true)
                .fechaNacimiento(LocalDate.of(1990, 5, 15));
    }

    @Nested
    @DisplayName("Validación de Usuario")
    class ValidacionTests {

        @Test
        @DisplayName("Usuario válido no lanza excepción")
        void usuarioValido_NoLanzaExcepcion() {
            Usuario usuario = usuarioBuilder.build();
            assertDoesNotThrow(usuario::validar);
        }

        @Test
        @DisplayName("Nombre vacío lanza UsuarioValidationException")
        void nombreVacio_LanzaExcepcion() {
            Usuario usuario = usuarioBuilder.nombre("").build();

            UsuarioValidationException exception = assertThrows(
                    UsuarioValidationException.class,
                    usuario::validar);

            assertEquals("Nombre es requerido", exception.getMessage());
        }

        @Test
        @DisplayName("Nombre nulo lanza UsuarioValidationException")
        void nombreNulo_LanzaExcepcion() {
            Usuario usuario = usuarioBuilder.nombre(null).build();

            assertThrows(UsuarioValidationException.class, usuario::validar);
        }

        @Test
        @DisplayName("Apellido vacío lanza UsuarioValidationException")
        void apellidoVacio_LanzaExcepcion() {
            Usuario usuario = usuarioBuilder.apellido("").build();

            UsuarioValidationException exception = assertThrows(
                    UsuarioValidationException.class,
                    usuario::validar);

            assertEquals("Apellido es requerido", exception.getMessage());
        }

        @Test
        @DisplayName("Password menor a 6 caracteres lanza excepción")
        void passwordCorto_LanzaExcepcion() {
            Usuario usuario = usuarioBuilder.password("12345").build();

            UsuarioValidationException exception = assertThrows(
                    UsuarioValidationException.class,
                    usuario::validar);

            assertEquals("Password debe tener al menos 6 caracteres", exception.getMessage());
        }

        @Test
        @DisplayName("DNI con caracteres no numéricos lanza excepción")
        void dniConLetras_LanzaExcepcion() {
            Usuario usuario = usuarioBuilder.dni("1234567A").build();

            UsuarioValidationException exception = assertThrows(
                    UsuarioValidationException.class,
                    usuario::validar);

            assertTrue(exception.getMessage().contains("DNI"));
        }

        @Test
        @DisplayName("DNI muy corto lanza excepción")
        void dniMuyCorto_LanzaExcepcion() {
            Usuario usuario = usuarioBuilder.dni("1234").build();

            assertThrows(UsuarioValidationException.class, usuario::validar);
        }
    }

    @Nested
    @DisplayName("Métodos de Negocio")
    class MetodosNegocioTests {

        @Test
        @DisplayName("esAdministrador retorna true para rol ADMINISTRADOR")
        void esAdministrador_ConRolAdmin_RetornaTrue() {
            Usuario usuario = usuarioBuilder.rol(Rol.ADMINISTRADOR).build();

            assertTrue(usuario.esAdministrador());
        }

        @Test
        @DisplayName("esAdministrador retorna false para rol CLIENTE")
        void esAdministrador_ConRolCliente_RetornaFalse() {
            Usuario usuario = usuarioBuilder.rol(Rol.CLIENTE).build();

            assertFalse(usuario.esAdministrador());
        }

        @Test
        @DisplayName("esActivo retorna true cuando activo es true")
        void esActivo_CuandoActivo_RetornaTrue() {
            Usuario usuario = usuarioBuilder.activo(true).build();

            assertTrue(usuario.esActivo());
        }

        @Test
        @DisplayName("esActivo retorna false cuando activo es false")
        void esActivo_CuandoInactivo_RetornaFalse() {
            Usuario usuario = usuarioBuilder.activo(false).build();

            assertFalse(usuario.esActivo());
        }

        @Test
        @DisplayName("esActivo retorna false cuando activo es null")
        void esActivo_CuandoNull_RetornaFalse() {
            Usuario usuario = usuarioBuilder.activo(null).build();

            assertFalse(usuario.esActivo());
        }

        @Test
        @DisplayName("getNombreCompleto retorna nombre y apellido concatenados")
        void getNombreCompleto_RetornaNombreYApellido() {
            Usuario usuario = usuarioBuilder
                    .nombre("Juan")
                    .apellido("Pérez")
                    .build();

            assertEquals("Juan Pérez", usuario.getNombreCompleto());
        }

        @Test
        @DisplayName("activar cambia activo a true")
        void activar_CambiaActivoATrue() {
            Usuario usuario = usuarioBuilder.activo(false).build();

            usuario.activar();

            assertTrue(usuario.getActivo());
        }

        @Test
        @DisplayName("desactivar cambia activo a false")
        void desactivar_CambiaActivoAFalse() {
            Usuario usuario = usuarioBuilder.activo(true).build();

            usuario.desactivar();

            assertFalse(usuario.getActivo());
        }
    }

    @Nested
    @DisplayName("Roles de Usuario")
    class RolesTests {

        @Test
        @DisplayName("Todos los roles están definidos")
        void todosLosRolesExisten() {
            assertNotNull(Rol.ADMINISTRADOR);
            assertNotNull(Rol.CLIENTE);
            assertNotNull(Rol.RECEPCIONISTA);
            assertNotNull(Rol.INSTRUCTOR);
            assertNotNull(Rol.CONTADOR);
        }
    }

    @Nested
    @DisplayName("Géneros de Usuario")
    class GenerosTests {

        @Test
        @DisplayName("Todos los géneros están definidos")
        void todosLosGenerosExisten() {
            assertNotNull(Genero.MASCULINO);
            assertNotNull(Genero.FEMENINO);
        }
    }
}
