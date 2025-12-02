package com.gym.backend.Reportes.Infrastructure.Jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class ReportesJpaRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Object[]> ingresosMensuales() {
        return em
                .createNativeQuery(
                        """
                                SELECT
                                    TO_CHAR(fecha_pago, 'TMMonth YYYY') AS mes,
                                    COALESCE(SUM(monto), 0) AS total
                                FROM pagos
                                WHERE estado = 'CONFIRMADO'
                                    AND fecha_pago >= CURRENT_DATE - INTERVAL '12 months'
                                GROUP BY TO_CHAR(fecha_pago, 'TMMonth YYYY'), EXTRACT(YEAR FROM fecha_pago), EXTRACT(MONTH FROM fecha_pago)
                                ORDER BY EXTRACT(YEAR FROM fecha_pago) DESC, EXTRACT(MONTH FROM fecha_pago) DESC
                                """)
                .getResultList();
    }

    public List<Object[]> membresiasPorEstado() {
        return em.createNativeQuery("""
                SELECT
                    estado,
                    COUNT(*) as cantidad
                FROM membresias
                GROUP BY estado
                ORDER BY cantidad DESC
                """).getResultList();
    }

    public List<Object[]> asistenciasDiarias() {
        return em.createNativeQuery("""
                SELECT
                    TO_CHAR(fecha_hora, 'YYYY-MM-DD') AS fecha,
                    COUNT(*) as cantidad
                FROM asistencias
                WHERE fecha_hora >= CURRENT_DATE - INTERVAL '30 days'
                GROUP BY TO_CHAR(fecha_hora, 'YYYY-MM-DD')
                ORDER BY fecha DESC
                """).getResultList();
    }

    public List<Object[]> topPlanes() {
        return em.createNativeQuery("""
                SELECT
                    p.nombre_plan,
                    COUNT(*) as cantidad
                FROM membresias m
                JOIN planes p ON p.id = m.plan_id
                WHERE p.activo = true
                GROUP BY p.nombre_plan
                ORDER BY cantidad DESC
                LIMIT 10
                """).getResultList();
    }

    public List<Object[]> ingresosPorRango(LocalDateTime inicio, LocalDateTime fin) {
        return em.createNativeQuery("""
                SELECT
                    TO_CHAR(fecha_pago, 'YYYY-MM-DD') AS fecha,
                    COALESCE(SUM(monto), 0) AS total
                FROM pagos
                WHERE estado = 'CONFIRMADO'
                    AND fecha_pago BETWEEN :inicio AND :fin
                GROUP BY TO_CHAR(fecha_pago, 'YYYY-MM-DD')
                ORDER BY fecha
                """)
                .setParameter("inicio", inicio)
                .setParameter("fin", fin)
                .getResultList();
    }

    public List<Object[]> asistenciasPorRango(LocalDateTime inicio, LocalDateTime fin) {
        return em.createNativeQuery("""
                SELECT
                    TO_CHAR(fecha_hora, 'YYYY-MM-DD') AS fecha,
                    COUNT(*) as cantidad
                FROM asistencias
                WHERE fecha_hora BETWEEN :inicio AND :fin
                GROUP BY TO_CHAR(fecha_hora, 'YYYY-MM-DD')
                ORDER BY fecha
                """)
                .setParameter("inicio", inicio)
                .setParameter("fin", fin)
                .getResultList();
    }

    public List<Object[]> usuariosNuevosPorMes() {
        return em
                .createNativeQuery(
                        """
                                SELECT
                                    TO_CHAR(fecha_creacion, 'TMMonth YYYY') AS mes,
                                    COUNT(*) as cantidad
                                FROM usuarios
                                WHERE fecha_creacion >= CURRENT_DATE - INTERVAL '12 months'
                                GROUP BY TO_CHAR(fecha_creacion, 'TMMonth YYYY'), EXTRACT(YEAR FROM fecha_creacion), EXTRACT(MONTH FROM fecha_creacion)
                                ORDER BY EXTRACT(YEAR FROM fecha_creacion) DESC, EXTRACT(MONTH FROM fecha_creacion) DESC
                                """)
                .getResultList();
    }

    public List<Object[]> membresiasPorPlan() {
        return em.createNativeQuery("""
                SELECT
                    p.nombre_plan,
                    COUNT(*) as cantidad
                FROM membresias m
                JOIN planes p ON p.id = m.plan_id
                WHERE p.activo = true
                GROUP BY p.nombre_plan
                ORDER BY cantidad DESC
                """).getResultList();
    }

    public List<Object[]> pagosPorMetodo() {
        return em.createNativeQuery("""
                SELECT
                    metodo_pago,
                    COUNT(*) as cantidad,
                    COALESCE(SUM(monto), 0) as total
                FROM pagos
                WHERE estado = 'CONFIRMADO'
                GROUP BY metodo_pago
                ORDER BY total DESC
                """).getResultList();
    }

    public List<Object[]> asistenciasPorHora(LocalDate fecha) {
        return em.createNativeQuery("""
                SELECT
                    TO_CHAR(fecha_hora, 'HH24:00') AS hora,
                    COUNT(*) as cantidad
                FROM asistencias
                WHERE DATE(fecha_hora) = :fecha
                GROUP BY TO_CHAR(fecha_hora, 'HH24:00')
                ORDER BY hora
                """)
                .setParameter("fecha", fecha)
                .getResultList();
    }

    public List<Object[]> rendimientoMensual() {
        return em
                .createNativeQuery(
                        """
                                SELECT
                                    TO_CHAR(p.fecha_pago, 'TMMonth YYYY') AS mes,
                                    COALESCE(SUM(p.monto), 0) as ingresos,
                                    COUNT(DISTINCT m.id) as membresias,
                                    COUNT(DISTINCT a.id) as asistencias
                                FROM pagos p
                                LEFT JOIN membresias m ON m.pago_id = p.id
                                LEFT JOIN asistencias a ON a.membresia_id = m.id
                                WHERE p.estado = 'CONFIRMADO'
                                    AND p.fecha_pago >= CURRENT_DATE - INTERVAL '12 months'
                                GROUP BY TO_CHAR(p.fecha_pago, 'TMMonth YYYY'), EXTRACT(YEAR FROM p.fecha_pago), EXTRACT(MONTH FROM p.fecha_pago)
                                ORDER BY EXTRACT(YEAR FROM p.fecha_pago) DESC, EXTRACT(MONTH FROM p.fecha_pago) DESC
                                """)
                .getResultList();
    }

    public List<Object[]> usuariosMasActivos(LocalDateTime inicio, LocalDateTime fin) {
        return em.createNativeQuery("""
                SELECT
                    u.nombre || ' ' || u.apellido as usuario,
                    COUNT(a.id) as asistencias,
                    p.nombre_plan as plan
                FROM asistencias a
                JOIN usuarios u ON u.id = a.usuario_id
                JOIN membresias m ON m.id = a.membresia_id
                JOIN planes p ON p.id = m.plan_id
                WHERE a.fecha_hora BETWEEN :inicio AND :fin
                GROUP BY u.id, u.nombre, u.apellido, p.nombre_plan
                ORDER BY asistencias DESC
                LIMIT 20
                """)
                .setParameter("inicio", inicio)
                .setParameter("fin", fin)
                .getResultList();
    }

    public List<Object[]> planesPopulares() {
        return em.createNativeQuery("""
                SELECT
                    p.nombre_plan,
                    p.precio,
                    COUNT(m.id) as miembros,
                    COALESCE(SUM(pa.monto), 0) as ingresos_totales
                FROM planes p
                LEFT JOIN membresias m ON m.plan_id = p.id AND m.estado = 'ACTIVA'
                LEFT JOIN pagos pa ON pa.id = m.pago_id AND pa.estado = 'CONFIRMADO'
                WHERE p.activo = true
                GROUP BY p.id, p.nombre_plan, p.precio
                ORDER BY miembros DESC
                LIMIT 10
                """).getResultList();
    }

    public Map<String, Object> estadisticasGenerales() {
        Map<String, Object> stats = new HashMap<>();

        // Total usuarios
        Long totalUsuarios = (Long) em.createNativeQuery("SELECT COUNT(*) FROM usuarios WHERE activo = true")
                .getSingleResult();
        stats.put("totalUsuarios", totalUsuarios);
        stats.put("totalUsuariosActivos", totalUsuarios);

        // Total membresías activas
        Long totalMembresias = (Long) em.createNativeQuery("SELECT COUNT(*) FROM membresias WHERE estado = 'ACTIVA'")
                .getSingleResult();
        stats.put("totalMembresias", totalMembresias);

        // Total asistencias hoy
        Long asistenciasHoy = (Long) em.createNativeQuery("""
                SELECT COUNT(*) FROM asistencias
                WHERE DATE(fecha_hora) = CURRENT_DATE
                """).getSingleResult();
        stats.put("asistenciasHoy", asistenciasHoy);

        // Ingresos del mes
        Double ingresosMes = (Double) em.createNativeQuery("""
                SELECT COALESCE(SUM(monto), 0) FROM pagos
                WHERE estado = 'CONFIRMADO'
                    AND fecha_pago >= DATE_TRUNC('month', CURRENT_DATE)
                """).getSingleResult();
        stats.put("ingresosMes", ingresosMes);
        stats.put("ingresosMesActual", ingresosMes);

        // Plan más popular
        try {
            Object planPopularResult = em.createNativeQuery("""
                    SELECT p.nombre_plan FROM planes p
                    JOIN membresias m ON m.plan_id = p.id
                    GROUP BY p.nombre_plan
                    ORDER BY COUNT(*) DESC
                    LIMIT 1
                    """).getSingleResult();
            stats.put("planPopular", planPopularResult);
        } catch (Exception e) {
            stats.put("planPopular", "Sin datos");
        }

        // Nuevos usuarios últimos 30 días
        Long nuevosUsuarios = (Long) em.createNativeQuery("""
                SELECT COUNT(*) FROM usuarios
                WHERE fecha_creacion >= CURRENT_DATE - INTERVAL '30 days'
                """).getSingleResult();
        stats.put("nuevosUsuariosUltimos30Dias", nuevosUsuarios);

        stats.put("fechaGeneracion", LocalDateTime.now());

        return stats;
    }

    public Map<String, Object> estadisticasPorFecha(LocalDateTime inicio, LocalDateTime fin) {
        Map<String, Object> stats = new HashMap<>();

        // Ingresos en el período
        Double ingresos = (Double) em.createNativeQuery("""
                SELECT COALESCE(SUM(monto), 0) FROM pagos
                WHERE estado = 'CONFIRMADO'
                    AND fecha_pago BETWEEN :inicio AND :fin
                """)
                .setParameter("inicio", inicio)
                .setParameter("fin", fin)
                .getSingleResult();
        stats.put("ingresos", ingresos);

        // Asistencias en el período
        Long asistencias = (Long) em.createNativeQuery("""
                SELECT COUNT(*) FROM asistencias
                WHERE fecha_hora BETWEEN :inicio AND :fin
                """)
                .setParameter("inicio", inicio)
                .setParameter("fin", fin)
                .getSingleResult();
        stats.put("asistencias", asistencias);

        // Nuevos usuarios en el período
        Long nuevosUsuarios = (Long) em.createNativeQuery("""
                SELECT COUNT(*) FROM usuarios
                WHERE fecha_creacion BETWEEN :inicio AND :fin
                """)
                .setParameter("inicio", inicio)
                .setParameter("fin", fin)
                .getSingleResult();
        stats.put("nuevosUsuarios", nuevosUsuarios);

        // Nuevas membresías en el período
        Long nuevasMembresias = (Long) em.createNativeQuery("""
                SELECT COUNT(*) FROM membresias
                WHERE fecha_creacion BETWEEN :inicio AND :fin
                """)
                .setParameter("inicio", inicio)
                .setParameter("fin", fin)
                .getSingleResult();
        stats.put("nuevasMembresias", nuevasMembresias);
        stats.put("periodoInicio", inicio);
        stats.put("periodoFin", fin);
        stats.put("fechaGeneracion", LocalDateTime.now());

        return stats;
    }

    public List<Object[]> ingresosDetallados(com.gym.backend.Reportes.Domain.DTO.FiltroIngresoDTO filtro) {
        StringBuilder sql = new StringBuilder("""
                SELECT
                    p.fecha_pago,
                    u.nombre || ' ' || u.apellido as usuario,
                    u.dni,
                    pl.nombre_plan,
                    p.monto,
                    p.metodo_pago,
                    p.estado,
                    p.codigo_pago,
                    p.observaciones
                FROM pagos p
                JOIN membresias m ON m.pago_id = p.id
                JOIN usuarios u ON u.id = m.usuario_id
                JOIN planes pl ON pl.id = m.plan_id
                WHERE p.fecha_pago BETWEEN :inicio AND :fin
                """);

        if (filtro.getEstado() != null && !filtro.getEstado().isEmpty() && !"TODOS".equals(filtro.getEstado())) {
            sql.append(" AND p.estado = :estado");
        }

        if (filtro.getMetodoPago() != null && !filtro.getMetodoPago().isEmpty()
                && !"TODOS".equals(filtro.getMetodoPago())) {
            sql.append(" AND p.metodo_pago = :metodo");
        }

        if (filtro.getPlanId() != null && filtro.getPlanId() > 0) {
            sql.append(" AND pl.id = :planId");
        }

        sql.append(" ORDER BY p.fecha_pago DESC");

        var query = em.createNativeQuery(sql.toString());
        query.setParameter("inicio", filtro.getFechaInicio());
        query.setParameter("fin", filtro.getFechaFin());

        if (filtro.getEstado() != null && !filtro.getEstado().isEmpty() && !"TODOS".equals(filtro.getEstado())) {
            query.setParameter("estado", filtro.getEstado());
        }

        if (filtro.getMetodoPago() != null && !filtro.getMetodoPago().isEmpty()
                && !"TODOS".equals(filtro.getMetodoPago())) {
            query.setParameter("metodo", filtro.getMetodoPago());
        }

        if (filtro.getPlanId() != null && filtro.getPlanId() > 0) {
            query.setParameter("planId", filtro.getPlanId());
        }

        return query.getResultList();
    }

    public Map<String, Object> resumenIngresos(com.gym.backend.Reportes.Domain.DTO.FiltroIngresoDTO filtro) {
        StringBuilder sql = new StringBuilder("""
                SELECT
                    COALESCE(SUM(CASE WHEN p.estado = 'CONFIRMADO' THEN p.monto ELSE 0 END), 0) as total_confirmado,
                    COALESCE(SUM(CASE WHEN p.estado = 'PENDIENTE' THEN p.monto ELSE 0 END), 0) as total_pendiente,
                    COALESCE(SUM(CASE WHEN p.estado = 'CANCELADO' THEN p.monto ELSE 0 END), 0) as total_cancelado,
                    COALESCE(SUM(p.monto), 0) as total_general,
                    COUNT(*) as total_transacciones,
                    COUNT(CASE WHEN p.estado = 'CONFIRMADO' THEN 1 END) as count_confirmado,
                    COUNT(CASE WHEN p.estado = 'PENDIENTE' THEN 1 END) as count_pendiente,
                    COUNT(CASE WHEN p.estado = 'CANCELADO' THEN 1 END) as count_cancelado
                FROM pagos p
                JOIN membresias m ON m.pago_id = p.id
                JOIN planes pl ON pl.id = m.plan_id
                WHERE p.fecha_pago BETWEEN :inicio AND :fin
                """);

        if (filtro.getEstado() != null && !filtro.getEstado().isEmpty() && !"TODOS".equals(filtro.getEstado())) {
            sql.append(" AND p.estado = :estado");
        }

        if (filtro.getMetodoPago() != null && !filtro.getMetodoPago().isEmpty()
                && !"TODOS".equals(filtro.getMetodoPago())) {
            sql.append(" AND p.metodo_pago = :metodo");
        }

        if (filtro.getPlanId() != null && filtro.getPlanId() > 0) {
            sql.append(" AND pl.id = :planId");
        }

        var query = em.createNativeQuery(sql.toString());
        query.setParameter("inicio", filtro.getFechaInicio());
        query.setParameter("fin", filtro.getFechaFin());

        if (filtro.getEstado() != null && !filtro.getEstado().isEmpty() && !"TODOS".equals(filtro.getEstado())) {
            query.setParameter("estado", filtro.getEstado());
        }

        if (filtro.getMetodoPago() != null && !filtro.getMetodoPago().isEmpty()
                && !"TODOS".equals(filtro.getMetodoPago())) {
            query.setParameter("metodo", filtro.getMetodoPago());
        }

        if (filtro.getPlanId() != null && filtro.getPlanId() > 0) {
            query.setParameter("planId", filtro.getPlanId());
        }

        Object[] result = (Object[]) query.getSingleResult();
        Map<String, Object> map = new HashMap<>();
        map.put("totalConfirmado", result[0]);
        map.put("totalPendiente", result[1]);
        map.put("totalCancelado", result[2]);
        map.put("totalGeneral", result[3]);
        map.put("totalTransacciones", result[4]);
        map.put("countConfirmado", result[5]);
        map.put("countPendiente", result[6]);
        map.put("countCancelado", result[7]);

        return map;
    }
}