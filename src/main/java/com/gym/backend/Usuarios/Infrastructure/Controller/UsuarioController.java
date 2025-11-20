package com.gym.backend.Usuarios.Infrastructure.Controller;

import com.gym.backend.Usuarios.Application.Dto.ActualizarUsuarioRequest;
import com.gym.backend.Usuarios.Application.Dto.CrearUsuarioRequest;
import com.gym.backend.Usuarios.Application.Dto.UsuarioResponse;
import com.gym.backend.Usuarios.Application.Mapper.UsuarioMapper;
import com.gym.backend.Usuarios.Domain.Enum.Genero;
import com.gym.backend.Usuarios.Domain.Enum.Rol;
import com.gym.backend.Usuarios.Domain.Usuario;
import com.gym.backend.Usuarios.Domain.UsuarioUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioUseCase useCase;
    private final UsuarioMapper mapper;

    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(@RequestBody CrearUsuarioRequest request) {
        log.info("Creando usuario: {}", request.getEmail());
        var usuario = mapper.toDomainFromCreateRequest(request);
        var creado = useCase.crear(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(creado));
    }

    @GetMapping
    public List<UsuarioResponse> listar() {
        return useCase.listar().stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/paginated")
    public Page<UsuarioResponse> listarPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return useCase.listarPaginated(pageable).map(mapper::toResponse);
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
    public ResponseEntity<UsuarioResponse> actualizar(@PathVariable Long id,
                                                      @RequestBody ActualizarUsuarioRequest request) {
        log.info("Actualizando usuario ID: {}", id);
        var actualizado = useCase.actualizar(id, request);
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