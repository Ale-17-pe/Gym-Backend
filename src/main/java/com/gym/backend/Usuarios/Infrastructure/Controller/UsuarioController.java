package com.gym.backend.Usuarios.Infrastructure.Controller;

import com.gym.backend.Usuarios.Application.Dto.CrearUsuarioRequest;
import com.gym.backend.Usuarios.Application.Dto.UsuarioResponse;
import com.gym.backend.Usuarios.Application.Mapper.UsuarioMapper;
import com.gym.backend.Usuarios.Domain.Enum.Genero;
import com.gym.backend.Usuarios.Domain.Enum.Rol;
import com.gym.backend.Usuarios.Domain.Usuario;
import com.gym.backend.Usuarios.Domain.UsuarioUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioUseCase useCase;
    private final UsuarioMapper mapper;

    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(@RequestBody CrearUsuarioRequest request) {
        Usuario usuario = mapper.toDomainFromCreateRequest(request);
        Usuario creado = useCase.crear(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(creado));
    }

    @GetMapping
    public List<UsuarioResponse> listar() {
        return useCase.listar().stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/activos")
    public List<UsuarioResponse> listarActivos() {
        return useCase.listarActivos().stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/inactivos")
    public List<UsuarioResponse> listarInactivos() {
        return useCase.listarInactivos().stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(useCase.obtener(id)));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioResponse> obtenerPorEmail(@PathVariable String email) {
        return ResponseEntity.ok(mapper.toResponse(useCase.obtenerPorEmail(email)));
    }

    @GetMapping("/dni/{dni}")
    public ResponseEntity<UsuarioResponse> obtenerPorDni(@PathVariable String dni) {
        return ResponseEntity.ok(mapper.toResponse(useCase.obtenerPorDni(dni)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizar(@PathVariable Long id, @RequestBody CrearUsuarioRequest request) {
        Usuario usuarioExistente = useCase.obtener(id);
        Usuario usuarioActualizado = mapper.toDomainFromCreateRequest(request);
        usuarioActualizado = Usuario.builder()
                .id(id)
                .nombre(usuarioActualizado.getNombre())
                .apellido(usuarioActualizado.getApellido())
                .genero(usuarioActualizado.getGenero())
                .email(usuarioExistente.getEmail())
                .dni(usuarioExistente.getDni())
                .telefono(usuarioActualizado.getTelefono())
                .direccion(usuarioActualizado.getDireccion())
                .password(usuarioActualizado.getPassword())
                .rol(usuarioActualizado.getRol())
                .activo(usuarioActualizado.getActivo())
                .build();
        Usuario actualizado = useCase.actualizar(usuarioActualizado);
        return ResponseEntity.ok(mapper.toResponse(actualizado));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<UsuarioResponse> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(useCase.desactivar(id)));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<UsuarioResponse> activar(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(useCase.activar(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        useCase.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/rol/{rol}")
    public List<UsuarioResponse> listarPorRol(@PathVariable String rol) {
        Rol rolEnum = Rol.valueOf(rol.toUpperCase());
        return useCase.listarPorRol(rolEnum).stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/genero/{genero}")
    public List<UsuarioResponse> listarPorGenero(@PathVariable String genero) {
        Genero generoEnum = Genero.valueOf(genero.toUpperCase());
        return useCase.listarPorGenero(generoEnum).stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/{id}/verificar-activo")
    public ResponseEntity<Void> verificarUsuarioActivo(@PathVariable Long id) {
        useCase.verificarUsuarioActivo(id);
        return ResponseEntity.ok().build();
    }
}