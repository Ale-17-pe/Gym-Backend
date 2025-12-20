package com.gym.backend.Auth.Domain;

import com.gym.backend.Usuarios.Domain.Exceptions.UsuarioValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para AuthUseCase
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthUseCase Tests")
class AuthUseCaseTest {

    @Mock
    private AuthServicePort authPort;

    @InjectMocks
    private AuthUseCase authUseCase;

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("Login exitoso con credenciales válidas")
        void login_ConCredencialesValidas_RetornaAuthResponse() {
            // Arrange
            LoginCommand command = new LoginCommand("test@example.com", "password123");
            AuthResponse expectedResponse = new AuthResponse(
                    "jwt-token",
                    "Bearer",
                    1L,
                    "Juan Pérez",
                    "test@example.com",
                    "12345678",
                    "CLIENTE",
                    "MASCULINO",
                    true,
                    null,
                    LocalDateTime.now().plusHours(24),
                    LocalDateTime.now(),
                    false,
                    "Login exitoso");

            when(authPort.login(any(LoginCommand.class))).thenReturn(expectedResponse);

            // Act
            AuthResponse result = authUseCase.login(command);

            // Assert
            assertNotNull(result);
            assertEquals("jwt-token", result.token());
            verify(authPort, times(1)).login(command);
        }

        @Test
        @DisplayName("Login con email vacío lanza excepción")
        void login_ConEmailVacio_LanzaExcepcion() {
            LoginCommand command = new LoginCommand("", "password123");

            UsuarioValidationException exception = assertThrows(
                    UsuarioValidationException.class,
                    () -> authUseCase.login(command));

            assertEquals("Email o DNI es requerido", exception.getMessage());
            verify(authPort, never()).login(any());
        }

        @Test
        @DisplayName("Login con email nulo lanza excepción")
        void login_ConEmailNulo_LanzaExcepcion() {
            LoginCommand command = new LoginCommand(null, "password123");

            assertThrows(UsuarioValidationException.class, () -> authUseCase.login(command));
            verify(authPort, never()).login(any());
        }

        @Test
        @DisplayName("Login con password vacío lanza excepción")
        void login_ConPasswordVacio_LanzaExcepcion() {
            LoginCommand command = new LoginCommand("test@example.com", "");

            UsuarioValidationException exception = assertThrows(
                    UsuarioValidationException.class,
                    () -> authUseCase.login(command));

            assertEquals("Contraseña es requerida", exception.getMessage());
            verify(authPort, never()).login(any());
        }

        @Test
        @DisplayName("Login con password nulo lanza excepción")
        void login_ConPasswordNulo_LanzaExcepcion() {
            LoginCommand command = new LoginCommand("test@example.com", null);

            assertThrows(UsuarioValidationException.class, () -> authUseCase.login(command));
            verify(authPort, never()).login(any());
        }

        @Test
        @DisplayName("Login con espacios en blanco como email lanza excepción")
        void login_ConEspaciosEnEmail_LanzaExcepcion() {
            LoginCommand command = new LoginCommand("   ", "password123");

            assertThrows(UsuarioValidationException.class, () -> authUseCase.login(command));
            verify(authPort, never()).login(any());
        }
    }

    @Nested
    @DisplayName("Registro Tests")
    class RegistroTests {

        @Test
        @DisplayName("Registro exitoso con datos válidos")
        void registrar_ConDatosValidos_RetornaAuthResponse() {
            // Arrange
            RegisterCommand command = new RegisterCommand(
                    "Juan", // nombre
                    "Pérez", // apellido
                    "MASCULINO", // genero
                    "juan@example.com", // email
                    "12345678", // dni
                    "987654321", // telefono
                    "Av. Principal", // direccion
                    "password123", // password
                    "CLIENTE" // rol
            );
            AuthResponse expectedResponse = new AuthResponse(
                    "jwt-token",
                    "Bearer",
                    1L,
                    "Juan Pérez",
                    "juan@example.com",
                    "12345678",
                    "CLIENTE",
                    "MASCULINO",
                    true,
                    null,
                    LocalDateTime.now().plusHours(24),
                    LocalDateTime.now(),
                    false,
                    "Registro exitoso");

            when(authPort.registrar(any(RegisterCommand.class))).thenReturn(expectedResponse);

            // Act
            AuthResponse result = authUseCase.registrar(command);

            // Assert
            assertNotNull(result);
            assertEquals("jwt-token", result.token());
            verify(authPort, times(1)).registrar(command);
        }

        @Test
        @DisplayName("Registro con nombre vacío lanza excepción")
        void registrar_ConNombreVacio_LanzaExcepcion() {
            RegisterCommand command = new RegisterCommand(
                    "", // nombre vacío
                    "Pérez",
                    "MASCULINO",
                    "juan@example.com",
                    "12345678",
                    "987654321",
                    "Av. Principal",
                    "password123",
                    "CLIENTE");

            UsuarioValidationException exception = assertThrows(
                    UsuarioValidationException.class,
                    () -> authUseCase.registrar(command));

            assertEquals("Nombre es requerido", exception.getMessage());
            verify(authPort, never()).registrar(any());
        }

        @Test
        @DisplayName("Registro con DNI vacío lanza excepción")
        void registrar_ConDniVacio_LanzaExcepcion() {
            RegisterCommand command = new RegisterCommand(
                    "Juan",
                    "Pérez",
                    "MASCULINO",
                    "juan@example.com",
                    "", // dni vacío
                    "987654321",
                    "Av. Principal",
                    "password123",
                    "CLIENTE");

            UsuarioValidationException exception = assertThrows(
                    UsuarioValidationException.class,
                    () -> authUseCase.registrar(command));

            assertEquals("DNI es requerido", exception.getMessage());
            verify(authPort, never()).registrar(any());
        }

        @Test
        @DisplayName("Registro con email vacío lanza excepción")
        void registrar_ConEmailVacio_LanzaExcepcion() {
            RegisterCommand command = new RegisterCommand(
                    "Juan",
                    "Pérez",
                    "MASCULINO",
                    "", // email vacío
                    "12345678",
                    "987654321",
                    "Av. Principal",
                    "password123",
                    "CLIENTE");

            UsuarioValidationException exception = assertThrows(
                    UsuarioValidationException.class,
                    () -> authUseCase.registrar(command));

            assertEquals("Email es requerido", exception.getMessage());
            verify(authPort, never()).registrar(any());
        }
    }

    @Nested
    @DisplayName("Validar Token Tests")
    class ValidarTokenTests {

        @Test
        @DisplayName("Validar token delega a authPort")
        void validarToken_DelegaAlPort() {
            String token = "valid-jwt-token";
            doNothing().when(authPort).validarToken(token);

            assertDoesNotThrow(() -> authUseCase.validarToken(token));
            verify(authPort, times(1)).validarToken(token);
        }
    }
}
