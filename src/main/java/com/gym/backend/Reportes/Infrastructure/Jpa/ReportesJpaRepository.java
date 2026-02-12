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

    // ========================================
    // MÉTODOS PRIVADOS DE AYUDA (DRY)
    // ========================================

    private jakarta.persistence.Query crearQueryDinamica(String selectClause, LocalDateTime inicio, LocalDateTime fin,
            String estado, String metodoPago, Long planId) {
        StringBuilder sql = new StringBuilder(selectClause);

        sql.append("""
                FROM pagos p
                JOIN membresias m ON m.pago_id = p.id
                JOIN usuario u ON u.id = m.usuario_id
                JOIN persona pe ON pe.usuario_id = u.id
                JOIN planes pl ON pl.id = m.plan_id
                WHERE p.fecha_pago BETWEEN :inicio AND :fin
                """);

        if (estado != null && !estado.isEmpty() && !"TODOS".equals(estado)) {
            sql.append(" AND p.estado = :estado");
        }

        if (metodoPago != null && !metodoPago.isEmpty() && !"TODOS".equals(metodoPago)) {
            sql.append(" AND p.metodo_pago = :metodo");
        }

        if (planId != null && planId > 0) {
            sql.append(" AND pl.id = :planId");
        }

        if (!selectClause.contains("COUNT") && !selectClause.contains("SUM")) {
            sql.append(" ORDER BY p.fecha_pago DESC");
        }

        var query = em.createNativeQuery(sql.toString());
        query.setParameter("inicio", inicio);
        query.setParameter("fin", fin);

        if (estado != null && !estado.isEmpty() && !"TODOS".equals(estado)) {
            query.setParameter("estado", estado);
        }

        if (metodoPago != null && !metodoPago.isEmpty() && !"TODOS".equals(metodoPago)) {
            query.setParameter("metodo", metodoPago);
        }

        if (planId != null && planId > 0) {
            query.setParameter("planId", planId);
        }

        return query;
    }

    private Map<String, Object> ejecutarResumen(LocalDateTime inicio, LocalDateTime fin, String estado, String metodo,
            Long planId) {
        String select = """
                SELECT
                    COALESCE(SUM(CASE WHEN p.estado = 'CONFIRMADO' THEN p.monto ELSE 0 END), 0) as total_confirmado,
                    COALESCE(SUM(CASE WHEN p.estado = 'PENDIENTE' THEN p.monto ELSE 0 END), 0) as total_pendiente,
                    COALESCE(SUM(CASE WHEN p.estado = 'CANCELADO' THEN p.monto ELSE 0 END), 0) as total_cancelado,
                    COALESCE(SUM(p.monto), 0) as total_general,
                    COUNT(*) as total_transacciones,
                    COUNT(CASE WHEN p.estado = 'CONFIRMADO' THEN 1 END) as count_confirmado,
                    COUNT(CASE WHEN p.estado = 'PENDIENTE' THEN 1 END) as count_pendiente,
                    COUNT(CASE WHEN p.estado = 'CANCELADO' THEN 1 END) as count_cancelado
                """;

        Object[] result = (Object[]) crearQueryDinamica(select, inicio, fin, estado, metodo, planId).getSingleResult();

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
                    COUNT(*)::INTEGER as cantidad,
                    0.0 as total_ingresos
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
                                    EXTRACT(MONTH FROM fecha_creacion)::INTEGER AS mes,
                                    EXTRACT(YEAR FROM fecha_creacion)::INTEGER AS anio,
                                    COUNT(*)::INTEGER as cantidad
                                FROM usuario
                                WHERE fecha_creacion >= CURRENT_DATE - INTERVAL '12 months'
                                GROUP BY EXTRACT(YEAR FROM fecha_creacion), EXTRACT(MONTH FROM fecha_creacion)
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
                    pe.nombre || ' ' || pe.apellido as usuario,
                    COUNT(a.id) as asistencias,
                    p.nombre_plan as plan
                FROM asistencias a
                JOIN usuario u ON u.id = a.usuario_id
                JOIN persona pe ON pe.usuario_id = u.id
                JOIN membresias m ON m.id = a.membresia_id
                JOIN planes p ON p.id = m.plan_id
                WHERE a.fecha_hora BETWEEN :inicio AND :fin
                GROUP BY u.id, pe.nombre, pe.apellido, p.nombre_plan
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

        try {
            // Total usuarios
            Long totalUsuarios = ((Number) em.createNativeQuery("SELECT COUNT(*) FROM usuario WHERE activo = true")
                    .getSingleResult()).longValue();
            stats.put("totalUsuarios", totalUsuarios);
            stats.put("totalUsuariosActivos", totalUsuarios);
        } catch (Exception e) {
            stats.put("totalUsuarios", 0L);
            stats.put("totalUsuariosActivos", 0L);
        }

        try {
            // Total membresías activas
            Long totalMembresias = ((Number) em
                    .createNativeQuery("SELECT COUNT(*) FROM membresias WHERE estado = 'ACTIVA'")
                    .getSingleResult()).longValue();
            stats.put("totalMembresias", totalMembresias);
            stats.put("membresiasActivas", totalMembresias);
        } catch (Exception e) {
            stats.put("totalMembresias", 0L);
            stats.put("membresiasActivas", 0L);
        }

        try {
            // Total asistencias hoy
            Long asistenciasHoy = ((Number) em.createNativeQuery("""
                    SELECT COUNT(*) FROM asistencias
                    WHERE DATE(fecha_hora) = CURRENT_DATE
                    """).getSingleResult()).longValue();
            stats.put("asistenciasHoy", asistenciasHoy);
        } catch (Exception e) {
            stats.put("asistenciasHoy", 0L);
        }

        try {
            // Ingresos del mes
            Object result = em.createNativeQuery("""
                    SELECT COALESCE(SUM(monto), 0) FROM pagos
                    WHERE estado = 'CONFIRMADO'
                        AND fecha_pago >= DATE_TRUNC('month', CURRENT_DATE)
                    """).getSingleResult();
            Double ingresosMes = result != null ? ((Number) result).doubleValue() : 0.0;
            stats.put("ingresosMes", ingresosMes);
            stats.put("ingresosMesActual", ingresosMes);
            stats.put("ingresosTotal", ingresosMes);
        } catch (Exception e) {
            stats.put("ingresosMes", 0.0);
            stats.put("ingresosMesActual", 0.0);
            stats.put("ingresosTotal", 0.0);
        }

        try {
            // Plan más popular
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

        try {
            // Nuevos usuarios últimos 30 días
            Long nuevosUsuarios = ((Number) em.createNativeQuery("""
                    SELECT COUNT(*) FROM usuario
                    WHERE fecha_creacion >= CURRENT_DATE - INTERVAL '30 days'
                    """).getSingleResult()).longValue();
            stats.put("nuevosUsuariosUltimos30Dias", nuevosUsuarios);
            stats.put("usuariosNuevos", nuevosUsuarios);
        } catch (Exception e) {
            stats.put("nuevosUsuariosUltimos30Dias", 0L);
            stats.put("usuariosNuevos", 0L);
        }

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
                SELECT COUNT(*) FROM usuario
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
        String select = """
                SELECT
                    p.fecha_pago,
                    pe.nombre || ' ' || pe.apellido as usuario,
                    pe.dni,
                    pl.nombre_plan,
                    p.monto,
                    p.metodo_pago,
                    p.estado,
                    p.codigo_pago,
                    p.observaciones
                """;
        return crearQueryDinamica(select, filtro.getFechaInicio(), filtro.getFechaFin(),
                filtro.getEstado(), filtro.getMetodoPago(), filtro.getPlanId()).getResultList();
    }

    public Map<String, Object> resumenIngresos(com.gym.backend.Reportes.Domain.DTO.FiltroIngresoDTO filtro) {
        return ejecutarResumen(filtro.getFechaInicio(), filtro.getFechaFin(),
                filtro.getEstado(), filtro.getMetodoPago(), filtro.getPlanId());
    }

    // ========== NUEVOS MÉTODOS ==========

    /**
     * Comparativa: Mes actual vs mes anterior
     */
    public Map<String, Object> comparativaMensual() {
        Map<String, Object> result = new HashMap<>();

        try {
            // Ingresos mes actual
            Object ingActual = em.createNativeQuery("""
                    SELECT COALESCE(SUM(monto), 0) FROM pagos
                    WHERE estado = 'CONFIRMADO'
                        AND fecha_pago >= DATE_TRUNC('month', CURRENT_DATE)
                        AND fecha_pago < DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '1 month'
                    """).getSingleResult();
            Double ingresosMesActual = ingActual != null ? ((Number) ingActual).doubleValue() : 0.0;

            // Ingresos mes anterior
            Object ingAnterior = em.createNativeQuery("""
                    SELECT COALESCE(SUM(monto), 0) FROM pagos
                    WHERE estado = 'CONFIRMADO'
                        AND fecha_pago >= DATE_TRUNC('month', CURRENT_DATE) - INTERVAL '1 month'
                        AND fecha_pago < DATE_TRUNC('month', CURRENT_DATE)
                    """).getSingleResult();
            Double ingresosMesAnterior = ingAnterior != null ? ((Number) ingAnterior).doubleValue() : 0.0;

            // Nuevos usuarios mes actual
            Long usuariosMesActual = ((Number) em.createNativeQuery("""
                    SELECT COUNT(*) FROM usuario
                    WHERE fecha_creacion >= DATE_TRUNC('month', CURRENT_DATE)
                    """).getSingleResult()).longValue();

            // Nuevos usuarios mes anterior
            Long usuariosMesAnterior = ((Number) em.createNativeQuery("""
                    SELECT COUNT(*) FROM usuario
                    WHERE fecha_creacion >= DATE_TRUNC('month', CURRENT_DATE) - INTERVAL '1 month'
                        AND fecha_creacion < DATE_TRUNC('month', CURRENT_DATE)
                    """).getSingleResult()).longValue();

            // Asistencias mes actual
            Long asistenciasMesActual = ((Number) em.createNativeQuery("""
                    SELECT COUNT(*) FROM asistencias
                    WHERE fecha_hora >= DATE_TRUNC('month', CURRENT_DATE)
                    """).getSingleResult()).longValue();

            // Asistencias mes anterior
            Long asistenciasMesAnterior = ((Number) em.createNativeQuery("""
                    SELECT COUNT(*) FROM asistencias
                    WHERE fecha_hora >= DATE_TRUNC('month', CURRENT_DATE) - INTERVAL '1 month'
                        AND fecha_hora < DATE_TRUNC('month', CURRENT_DATE)
                    """).getSingleResult()).longValue();

            // Calcular porcentajes
            double porcentajeIngresos = calcularPorcentajeCambio(ingresosMesAnterior, ingresosMesActual);
            double porcentajeUsuarios = calcularPorcentajeCambio(usuariosMesAnterior.doubleValue(),
                    usuariosMesActual.doubleValue());
            double porcentajeAsistencias = calcularPorcentajeCambio(asistenciasMesAnterior.doubleValue(),
                    asistenciasMesActual.doubleValue());

            result.put("ingresosMesActual", ingresosMesActual);
            result.put("ingresosMesAnterior", ingresosMesAnterior);
            result.put("porcentajeIngresos", porcentajeIngresos);

            result.put("usuariosMesActual", usuariosMesActual);
            result.put("usuariosMesAnterior", usuariosMesAnterior);
            result.put("porcentajeUsuarios", porcentajeUsuarios);

            result.put("asistenciasMesActual", asistenciasMesActual);
            result.put("asistenciasMesAnterior", asistenciasMesAnterior);
            result.put("porcentajeAsistencias", porcentajeAsistencias);
        } catch (Exception e) {
            log.error("Error en comparativaMensual", e);
            result.put("ingresosMesActual", 0.0);
            result.put("ingresosMesAnterior", 0.0);
            result.put("porcentajeIngresos", 0.0);
            result.put("usuariosMesActual", 0L);
            result.put("usuariosMesAnterior", 0L);
            result.put("porcentajeUsuarios", 0.0);
            result.put("asistenciasMesActual", 0L);
            result.put("asistenciasMesAnterior", 0L);
            result.put("porcentajeAsistencias", 0.0);
        }

        return result;
    }

    /**
     * Asistencias de los últimos 7 días (día por día)
     */
    public List<Map<String, Object>> asistenciasSemanal() {
        List<Object[]> results = em.createNativeQuery("""
                SELECT
                    TO_CHAR(fecha_hora, 'dy DD/MM') AS dia,
                    DATE(fecha_hora) AS fecha,
                    COUNT(*) as cantidad
                FROM asistencias
                WHERE fecha_hora >= CURRENT_DATE - INTERVAL '6 days'
                GROUP BY TO_CHAR(fecha_hora, 'dy DD/MM'), DATE(fecha_hora)
                ORDER BY fecha
                """).getResultList();

        return results.stream()
                .map(r -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("dia", r[0]);
                    map.put("fecha", r[1]);
                    map.put("cantidad", ((Number) r[2]).longValue());
                    return map;
                })
                .toList();
    }

    /**
     * Membresías que vencen en los próximos N días
     */
    public Map<String, Object> renovacionesProximas(int dias) {
        Map<String, Object> result = new HashMap<>();

        try {
            // Membresías que vencen próximamente
            List<Object[]> proximas = em.createNativeQuery("""
                    SELECT
                        pe.nombre || ' ' || pe.apellido as usuario,
                        u.email,
                        pe.telefono,
                        p.nombre_plan,
                        m.fecha_fin,
                        CAST(m.fecha_fin - CURRENT_DATE AS INTEGER) as dias_restantes
                    FROM membresias m
                    JOIN usuario u ON u.id = m.usuario_id
                    JOIN persona pe ON pe.usuario_id = u.id
                    JOIN planes p ON p.id = m.plan_id
                    WHERE m.estado = 'ACTIVA'
                        AND m.fecha_fin BETWEEN CURRENT_DATE AND CURRENT_DATE + :dias
                    ORDER BY m.fecha_fin
                    """)
                    .setParameter("dias", dias)
                    .getResultList();

            // Total de renovaciones próximas
            Long totalProximas = ((Number) em.createNativeQuery("""
                    SELECT COUNT(*) FROM membresias
                    WHERE estado = 'ACTIVA'
                        AND fecha_fin BETWEEN CURRENT_DATE AND CURRENT_DATE + :dias
                    """)
                    .setParameter("dias", dias)
                    .getSingleResult()).longValue();

            // Tasa de renovación histórica (últimos 3 meses)
            Object[] tasaRenovacion = (Object[]) em.createNativeQuery("""
                    SELECT
                        COUNT(CASE WHEN estado = 'ACTIVA' THEN 1 END) as renovadas,
                        COUNT(*) as total
                    FROM membresias
                    WHERE fecha_fin >= CURRENT_DATE - INTERVAL '90 days'
                        AND fecha_fin < CURRENT_DATE
                    """).getSingleResult();

            long totalVencidas = tasaRenovacion[1] != null ? ((Number) tasaRenovacion[1]).longValue() : 0;
            long renovadas = tasaRenovacion[0] != null ? ((Number) tasaRenovacion[0]).longValue() : 0;
            double porcentajeRenovacion = totalVencidas > 0 ? (renovadas * 100.0 / totalVencidas) : 0;

            // Proyección de ingresos
            Object proyResult = em.createNativeQuery("""
                    SELECT COALESCE(SUM(p.precio), 0)
                    FROM membresias m
                    JOIN planes p ON p.id = m.plan_id
                    WHERE m.estado = 'ACTIVA'
                        AND m.fecha_fin BETWEEN CURRENT_DATE AND CURRENT_DATE + :dias
                    """)
                    .setParameter("dias", dias)
                    .getSingleResult();
            Double proyeccionIngresos = proyResult != null ? ((Number) proyResult).doubleValue() : 0.0;

            List<Map<String, Object>> detalles = proximas.stream()
                    .map(r -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("usuario", r[0]);
                        map.put("email", r[1]);
                        map.put("telefono", r[2]);
                        map.put("plan", r[3]);
                        map.put("fechaVencimiento", r[4]);
                        map.put("diasRestantes", r[5] != null ? ((Number) r[5]).intValue() : 0);
                        return map;
                    })
                    .toList();

            result.put("total", totalProximas);
            result.put("detalles", detalles);
            result.put("tasaRenovacion", porcentajeRenovacion);
            result.put("proyeccionIngresos", proyeccionIngresos);
            result.put("diasConsiderados", dias);
        } catch (Exception e) {
            log.error("Error en renovacionesProximas", e);
            result.put("total", 0L);
            result.put("detalles", new java.util.ArrayList<>());
            result.put("tasaRenovacion", 0.0);
            result.put("proyeccionIngresos", 0.0);
            result.put("diasConsiderados", dias);
        }

        return result;
    }

    /**
     * Calcula el porcentaje de cambio entre dos valores
     */
    private double calcularPorcentajeCambio(double valorAnterior, double valorActual) {
        if (valorAnterior == 0) {
            return valorActual > 0 ? 100.0 : 0.0;
        }
        return ((valorActual - valorAnterior) / valorAnterior) * 100.0;
    }

    // ========================================
    // MÉTODOS PARA EXPORTACIÓN
    @SuppressWarnings("unchecked")
    public List<com.gym.backend.Reportes.Application.DTO.IngresoDetalladoDTO> obtenerIngresosDetallados(
            com.gym.backend.Reportes.Application.DTO.FiltroIngresoDTO filtro) {

        String sql = """
                SELECT
                    p.fecha_pago,
                    pe.dni,
                    pe.nombre || ' ' || pe.apellido as nombre_completo,
                    pl.nombre_plan,
                    p.monto,
                    p.metodo_pago,
                    p.estado,
                    p.codigo_pago
                FROM pagos p
                JOIN membresias m ON m.pago_id = p.id
                JOIN usuario u ON m.usuario_id = u.id
                JOIN persona pe ON pe.usuario_id = u.id
                JOIN planes pl ON m.plan_id = pl.id
                WHERE 1=1
                """;

        if (filtro.getFechaInicio() != null) {
            sql += " AND p.fecha_pago >= :fechaInicio";
        }
        if (filtro.getFechaFin() != null) {
            sql += " AND p.fecha_pago <= :fechaFin";
        }
        if (filtro.getEstado() != null && !filtro.getEstado().isBlank()) {
            sql += " AND p.estado = :estado";
        }
        if (filtro.getMetodoPago() != null && !filtro.getMetodoPago().isBlank()) {
            sql += " AND p.metodo_pago = :metodoPago";
        }
        if (filtro.getPlanId() != null) {
            sql += " AND pl.id = :planId";
        }

        sql += " ORDER BY p.fecha_pago DESC";

        var query = em.createNativeQuery(sql);

        if (filtro.getFechaInicio() != null) {
            query.setParameter("fechaInicio", filtro.getFechaInicio());
        }
        if (filtro.getFechaFin() != null) {
            query.setParameter("fechaFin", filtro.getFechaFin());
        }
        if (filtro.getEstado() != null && !filtro.getEstado().isBlank()) {
            query.setParameter("estado", filtro.getEstado());
        }
        if (filtro.getMetodoPago() != null && !filtro.getMetodoPago().isBlank()) {
            query.setParameter("metodoPago", filtro.getMetodoPago());
        }
        if (filtro.getPlanId() != null) {
            query.setParameter("planId", filtro.getPlanId());
        }

        List<Object[]> results = query.getResultList();
        return results.stream().map(row -> {
            // Manejo robusto de fechas nulas
            java.time.LocalDateTime fechaPago = null;
            if (row[0] != null) {
                if (row[0] instanceof java.sql.Timestamp) {
                    fechaPago = ((java.sql.Timestamp) row[0]).toLocalDateTime();
                } else if (row[0] instanceof java.time.LocalDateTime) {
                    fechaPago = (java.time.LocalDateTime) row[0];
                }
            }

            return com.gym.backend.Reportes.Application.DTO.IngresoDetalladoDTO.builder()
                    .fechaPago(fechaPago)
                    .usuarioDni((String) row[1])
                    .usuarioNombre((String) row[2])
                    .planNombre((String) row[3])
                    .monto(row[4] != null ? ((Number) row[4]).doubleValue() : 0.0)
                    .metodoPago((String) row[5])
                    .estado((String) row[6])
                    .codigoPago((String) row[7])
                    .build();
        }).collect(java.util.stream.Collectors.toList());
    }

    public com.gym.backend.Reportes.Application.DTO.ResumenIngresoDTO obtenerResumenIngresos(
            com.gym.backend.Reportes.Application.DTO.FiltroIngresoDTO filtro) {

        String sql = """
                SELECT
                    COALESCE(SUM(CASE WHEN p.estado = 'CONFIRMADO' THEN p.monto ELSE 0 END), 0) as total_confirmado,
                    COALESCE(SUM(CASE WHEN p.estado = 'PENDIENTE' THEN p.monto ELSE 0 END), 0) as total_pendiente,
                    COALESCE(SUM(CASE WHEN p.estado = 'CANCELADO' THEN p.monto ELSE 0 END), 0) as total_cancelado,
                    COALESCE(SUM(p.monto), 0) as total_general,
                    COALESCE(COUNT(*), 0) as cantidad_transacciones,
                    COALESCE(SUM(CASE WHEN p.estado = 'CONFIRMADO' THEN 1 ELSE 0 END), 0) as cantidad_confirmadas,
                    COALESCE(SUM(CASE WHEN p.estado = 'PENDIENTE' THEN 1 ELSE 0 END), 0) as cantidad_pendientes,
                    COALESCE(SUM(CASE WHEN p.estado = 'CANCELADO' THEN 1 ELSE 0 END), 0) as cantidad_canceladas
                FROM pagos p
                JOIN membresias m ON m.pago_id = p.id
                JOIN planes pl ON m.plan_id = pl.id
                WHERE 1=1
                """;

        if (filtro.getFechaInicio() != null) {
            sql += " AND p.fecha_pago >= :fechaInicio";
        }
        if (filtro.getFechaFin() != null) {
            sql += " AND p.fecha_pago <= :fechaFin";
        }
        if (filtro.getEstado() != null && !filtro.getEstado().isBlank()) {
            sql += " AND p.estado = :estado";
        }
        if (filtro.getMetodoPago() != null && !filtro.getMetodoPago().isBlank()) {
            sql += " AND p.metodo_pago = :metodoPago";
        }
        if (filtro.getPlanId() != null) {
            sql += " AND pl.id = :planId";
        }

        var query = em.createNativeQuery(sql);

        if (filtro.getFechaInicio() != null) {
            query.setParameter("fechaInicio", filtro.getFechaInicio());
        }
        if (filtro.getFechaFin() != null) {
            query.setParameter("fechaFin", filtro.getFechaFin());
        }
        if (filtro.getEstado() != null && !filtro.getEstado().isBlank()) {
            query.setParameter("estado", filtro.getEstado());
        }
        if (filtro.getMetodoPago() != null && !filtro.getMetodoPago().isBlank()) {
            query.setParameter("metodoPago", filtro.getMetodoPago());
        }
        if (filtro.getPlanId() != null) {
            query.setParameter("planId", filtro.getPlanId());
        }

        try {
            Object[] result = (Object[]) query.getSingleResult();

            // Verificación extra por si result es null (raro con agregaciones pero posible)
            if (result == null) {
                return com.gym.backend.Reportes.Application.DTO.ResumenIngresoDTO.builder()
                        .totalConfirmado(0.0)
                        .totalPendiente(0.0)
                        .totalCancelado(0.0)
                        .totalGeneral(0.0)
                        .cantidadTransacciones(0)
                        .cantidadConfirmadas(0)
                        .cantidadPendientes(0)
                        .cantidadCanceladas(0)
                        .build();
            }

            return com.gym.backend.Reportes.Application.DTO.ResumenIngresoDTO.builder()
                    .totalConfirmado(result[0] != null ? ((Number) result[0]).doubleValue() : 0.0)
                    .totalPendiente(result[1] != null ? ((Number) result[1]).doubleValue() : 0.0)
                    .totalCancelado(result[2] != null ? ((Number) result[2]).doubleValue() : 0.0)
                    .totalGeneral(result[3] != null ? ((Number) result[3]).doubleValue() : 0.0)
                    .cantidadTransacciones(result[4] != null ? ((Number) result[4]).intValue() : 0)
                    .cantidadConfirmadas(result[5] != null ? ((Number) result[5]).intValue() : 0)
                    .cantidadPendientes(result[6] != null ? ((Number) result[6]).intValue() : 0)
                    .cantidadCanceladas(result[7] != null ? ((Number) result[7]).intValue() : 0)
                    .build();
        } catch (jakarta.persistence.NoResultException e) {
            return com.gym.backend.Reportes.Application.DTO.ResumenIngresoDTO.builder().build();
        }
    }
}