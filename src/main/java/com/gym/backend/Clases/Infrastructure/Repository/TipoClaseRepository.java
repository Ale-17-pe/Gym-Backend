package com.gym.backend.Clases.Infrastructure.Repository;

import com.gym.backend.Clases.Infrastructure.Entity.TipoClaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipoClaseRepository extends JpaRepository<TipoClaseEntity, Long> {

    List<TipoClaseEntity> findByActivoTrue();

    List<TipoClaseEntity> findByNivelAndActivoTrue(String nivel);
}
