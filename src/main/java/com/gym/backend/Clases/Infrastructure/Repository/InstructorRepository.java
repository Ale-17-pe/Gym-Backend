package com.gym.backend.Clases.Infrastructure.Repository;

import com.gym.backend.Clases.Infrastructure.Entity.InstructorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstructorRepository extends JpaRepository<InstructorEntity, Long> {

    Optional<InstructorEntity> findByUsuarioId(Long usuarioId);

    List<InstructorEntity> findByActivoTrue();

    boolean existsByUsuarioId(Long usuarioId);
}
