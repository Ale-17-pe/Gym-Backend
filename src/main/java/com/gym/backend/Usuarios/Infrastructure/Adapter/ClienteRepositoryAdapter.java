package com.gym.backend.Usuarios.Infrastructure.Adapter;

import com.gym.backend.Usuarios.Domain.Cliente;
import com.gym.backend.Usuarios.Domain.ClienteRepositoryPort;
import com.gym.backend.Usuarios.Domain.Enum.NivelExperiencia;
import com.gym.backend.Usuarios.Domain.Enum.ObjetivoFitness;
import com.gym.backend.Usuarios.Infrastructure.Entity.ClienteEntity;
import com.gym.backend.Usuarios.Infrastructure.Entity.PersonaEntity;
import com.gym.backend.Usuarios.Infrastructure.Jpa.ClienteJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ClienteRepositoryAdapter implements ClienteRepositoryPort {

    private final ClienteJpaRepository jpa;

    @Override
    public Cliente guardar(Cliente cliente) {
        return toDomain(jpa.save(toEntity(cliente)));
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Cliente> buscarPorUsuarioId(Long usuarioId) {
        return jpa.findByUsuarioId(usuarioId).map(this::toDomain);
    }

    @Override
    public Optional<Cliente> buscarPorPersonaId(Long personaId) {
        return jpa.findByPersonaId(personaId).map(this::toDomain);
    }

    @Override
    public List<Cliente> listar() {
        return jpa.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Cliente> listarActivos() {
        return jpa.findByActivo(true).stream().map(this::toDomain).toList();
    }

    @Override
    public Page<Cliente> listarActivosPaginado(Pageable pageable) {
        return jpa.findByActivo(true, pageable).map(this::toDomain);
    }

    @Override
    public List<Cliente> buscarPorObjetivo(ObjetivoFitness objetivo) {
        return jpa.findByObjetivoFitness(objetivo).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Cliente> buscarPorNivel(NivelExperiencia nivel) {
        return jpa.findByNivelExperiencia(nivel).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Cliente> buscarPorCodigoReferido(String codigoReferido) {
        return jpa.findByCodigoReferido(codigoReferido).stream().map(this::toDomain).toList();
    }

    @Override
    public long contarActivos() {
        return jpa.countByActivoTrue();
    }

    @Override
    public void eliminar(Long id) {
        jpa.deleteById(id);
    }

    private Cliente toDomain(ClienteEntity entity) {
        if (entity == null)
            return null;

        return Cliente.builder()
                .id(entity.getId())
                .personaId(entity.getPersona() != null ? entity.getPersona().getId() : null)
                .usuarioId(entity.getUsuarioId())
                .objetivoFitness(entity.getObjetivoFitness())
                .nivelExperiencia(entity.getNivelExperiencia())
                .condicionesMedicas(entity.getCondicionesMedicas())
                .contactoEmergenciaNombre(entity.getContactoEmergenciaNombre())
                .contactoEmergenciaTelefono(entity.getContactoEmergenciaTelefono())
                .comoNosConocio(entity.getComoNosConocio())
                .codigoReferido(entity.getCodigoReferido())
                .activo(entity.getActivo())
                .fechaRegistroGym(entity.getFechaRegistroGym())
                .fechaCreacion(entity.getFechaCreacion())
                .fechaActualizacion(entity.getFechaActualizacion())
                .build();
    }

    private ClienteEntity toEntity(Cliente cliente) {
        ClienteEntity entity = ClienteEntity.builder()
                .id(cliente.getId())
                .usuarioId(cliente.getUsuarioId())
                .objetivoFitness(cliente.getObjetivoFitness())
                .nivelExperiencia(cliente.getNivelExperiencia())
                .condicionesMedicas(cliente.getCondicionesMedicas())
                .contactoEmergenciaNombre(cliente.getContactoEmergenciaNombre())
                .contactoEmergenciaTelefono(cliente.getContactoEmergenciaTelefono())
                .comoNosConocio(cliente.getComoNosConocio())
                .codigoReferido(cliente.getCodigoReferido())
                .activo(cliente.getActivo() != null ? cliente.getActivo() : true)
                .fechaRegistroGym(cliente.getFechaRegistroGym())
                .fechaCreacion(cliente.getFechaCreacion())
                .fechaActualizacion(cliente.getFechaActualizacion())
                .build();

        if (cliente.getPersonaId() != null) {
            PersonaEntity persona = new PersonaEntity();
            persona.setId(cliente.getPersonaId());
            entity.setPersona(persona);
        }

        return entity;
    }
}
