package com.gym.backend.Reportes.Infrastructure.Jpa;


import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportesJpaRepository {

    @Query(value = """
            SELECT 
                TO_CHAR(fecha_pago, 'TMMonth') AS mes,
                SUM(monto) AS total
            FROM pagos
            WHERE estado = 'CONFIRMADO'
            GROUP BY 1
            ORDER BY MIN(fecha_pago)
            """, nativeQuery = true)
    List<Object[]> ingresosMensuales();

    @Query(value = """
            SELECT estado, COUNT(*) 
            FROM membresias
            GROUP BY estado
            """, nativeQuery = true)
    List<Object[]> membresiasPorEstado();

    @Query(value = """
            SELECT 
                TO_CHAR(fecha_hora, 'YYYY-MM-DD') AS fecha,
                COUNT(*) 
            FROM asistencias
            GROUP BY 1
            ORDER BY fecha DESC
            """, nativeQuery = true)
    List<Object[]> asistenciasDiarias();

    @Query(value = """
            SELECT p.nombre_plan, COUNT(*) 
            FROM membresias m
            JOIN planes p ON p.id = m.plan_id
            GROUP BY p.nombre_plan
            ORDER BY COUNT(*) DESC
            LIMIT 5
            """, nativeQuery = true)
    List<Object[]> topPlanes();
}