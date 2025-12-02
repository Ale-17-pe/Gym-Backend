package com.gym.backend.Shared.Dashboard;

import com.gym.backend.Shared.Dashboard.DTO.DashboardStatsDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Obtiene todas las estadísticas del dashboard
     * Solo accesible por ADMINISTRADOR y RECEPCIONISTA
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        log.info("📊 Obteniendo estadísticas del dashboard");

        try {
            DashboardStatsDTO stats = dashboardService.getDashboardStats();
            log.info("✅ Estadísticas obtenidas exitosamente");
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("❌ Error al obtener estadísticas: {}", e.getMessage());
            throw new RuntimeException("Error al obtener estadísticas del dashboard", e);
        }
    }
}
