package com.gym.backend.Reportes.Infrastructure.Jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReportesJpaRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Object[]> ingresosMensuales() {
        return em.createNativeQuery("""
                SELECT TO_CHAR(fecha_pago, 'TMMonth') AS mes, SUM(monto) AS total
                FROM pagos
                WHERE estado = 'CONFIRMADO'
                GROUP BY 1
                ORDER BY MIN(fecha_pago)
                """).getResultList();
    }

    public List<Object[]> membresiasPorEstado() {
        return em.createNativeQuery("""
                SELECT estado, COUNT(*)
                FROM membresias
                GROUP BY estado
                """).getResultList();
    }

    public List<Object[]> asistenciasDiarias() {
        return em.createNativeQuery("""
                SELECT TO_CHAR(fecha_hora, 'YYYY-MM-DD') AS fecha, COUNT(*)
                FROM asistencias
                GROUP BY 1
                ORDER BY fecha DESC
                """).getResultList();
    }

    public List<Object[]> topPlanes() {
        return em.createNativeQuery("""
                SELECT p.nombre_plan, COUNT(*)
                FROM membresias m
                JOIN planes p ON p.id = m.plan_id
                GROUP BY p.nombre_plan
                ORDER BY COUNT(*) DESC
                LIMIT 5
                """).getResultList();
    }
}
