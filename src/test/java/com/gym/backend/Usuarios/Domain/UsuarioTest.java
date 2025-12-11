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
 * Tests unitarios para la entidad de dominio Usuario (normalizada)
 */
@DisplayName("Usuario Domain Tests")
class UsuarioTest {

    private Usuario.UsuarioBuilder usuarioBuilder;
    private Persona persona;

    @BeforeEach
    void setUp() {
        // Crear Persona primero
        persona = Persona.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Pérez")
                .dni("12345678")
                .genero(Genero.MASCULINO)
                .telefono("987654321")
                .direccion("Av. Principal 123")
                .fechaNacimiento(LocalDate.of(1990, 5, 15))
                .build();

        // Usuario solo tiene datos de auth
        usuarioBuilder = Usuario.builder()
                .id(1L)
                .email("juan.perez@example.com")
                .password("password123")
                .activo(true)
                .emailVerificado(false)
                .persona(persona);
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
        @DisplayName("Email inválido lanza UsuarioValidationException")
        void emailInvalido_LanzaExcepcion() {
            Usuario usuario = usuarioBuilder.email("email-invalido").build();

            UsuarioValidationException exception = assertThrows(
                    UsuarioValidationException.class,
                    usuario::validar);

            assertTrue(exception.getMessage().contains("Email"));
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
    }

    @Nested
    @DisplayName("Métodos de Negocio")
    class MetodosNegocioTests {

        @Test
        @DisplayName("esAdministrador retorna true cuando tiene rol ADMINISTRADOR")
        void esAdministrador_ConRolAdmin_RetornaTrue() {
            Usuario usuario = usuarioBuilder.build();
            usuario.agregarRol(Rol.ADMINISTRADOR);

            assertTrue(usuario.esAdministrador());
        }

        @Test
        @DisplayName("esCliente retorna true cuando tiene rol CLIENTE")
        void esCliente_ConRolCliente_RetornaTrue() {
            Usuario usuario = usuarioBuilder.build();
            usuario.agregarRol(Rol.CLIENTE);

            assertTrue(usuario.esCliente());
        }

        @Test
        @DisplayName("Puede tener múltiples roles")
        void puedeMultiplesRoles() {
            Usuario usuario = usuarioBuilder.build();
            usuario.agregarRol(Rol.ENTRENADOR);
            usuario.agregarRol(Rol.CLIENTE);

            assertTrue(usuario.esEntrenador());
            assertTrue(usuario.esCliente());
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
        @DisplayName("getNombreCompleto obtiene desde Persona")
        void getNombreCompleto_RetornaDesdaPersona() {
            Usuario usuario = usuarioBuilder.build();

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
            assertNotNull(Rol.ENTRENADOR);
            assertNotNull(Rol.CONTADOR);
        }

        @Test
        @DisplayName("getRol retorna el rol principal")
        void getRol_RetornaRolPrincipal() {
            Usuario usuario = usuarioBuilder.build();
            usuario.agregarRol(Rol.CLIENTE);

            assertEquals(Rol.CLIENTE, usuario.getRol());
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
            assertNotNull(Genero.PREFIERO_NO_DECIR);
        }
    }
}
