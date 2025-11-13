package com.gym.backend.Planes.Infrastructure.Jpa;

import com.gym.backend.Planes.Infrastructure.Entity.PlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanJpaRepository extends JpaRepository<PlanEntity, Long> {
}
