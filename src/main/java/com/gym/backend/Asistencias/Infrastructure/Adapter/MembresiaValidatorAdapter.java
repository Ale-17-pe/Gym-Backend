package com.gym.backend.Asistencias.Infrastructure.Adapter;

import com.gym.backend.Asistencias.Domain.MembresiaValidatorPort;
import com.gym.backend.Membresias.Infrastructure.Adapter.MembresiaRepositoryAdapter;
import org.springframework.stereotype.Component;

@Component
public class MembresiaValidatorAdapter implements MembresiaValidatorPort {

    private final MembresiaRepositoryAdapter repoMembresia;

    public MembresiaValidatorAdapter(MembresiaRepositoryAdapter repoMembresia) {
        this.repoMembresia = repoMembresia;
    }

    @Override
    public boolean tieneMembresiaActiva(Long usuarioId) {
        var m = repoMembresia.buscarActivaPorUsuario(usuarioId);
        return m != null;
    }
}