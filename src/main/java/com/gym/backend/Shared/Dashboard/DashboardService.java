package com.gym.backend.Shared.Dashboard;

import com.gym.backend.Asistencias.Infrastructure.Jpa.AsistenciaJpaRepository;
import com.gym.backend.Clases.Infrastructure.Repository.ReservaClaseRepository;
import com.gym.backend.Clases.Infrastructure.Repository.SesionClaseRepository;
import com.gym.backend.Membresias.Infrastructure.Jpa.MembresiaJpaRepository;
import com.gym.backend.Pago.Infrastructure.Jpa.PagoJpaRepository;
import com.gym.backend.Planes.Infrastructure.Jpa.PlanJpaRepository;
import com.gym.backend.Shared.Dashboard.DTO.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

        private final PagoJpaRepository pagoRepository;
        private final MembresiaJpaRepository membresiaRepository;
        private final AsistenciaJpaRepository asistenciaRepository;
        private final SesionClaseRepository sesionClaseRepository;
        private final ReservaClaseRepository reservaClaseRepository;
        private final PlanJpaRepository planRepository;

        public DashboardStatsDTO getDashboardStats() {
                return DashboardStatsDTO.builder()
                                .kpis(getKpis())
                                .revenueChart(getRevenueChart())
                                .membershipDistribution(getMembershipDistribution())
                                .topClasses(getTopClasses())
                                .attendanceByDay(getAttendanceByDay())
                                .build();
        }

        private List<KpiResponse> getKpis() {
                return Arrays.asList(
                                getRevenueKpi(),
                                getMembresiaKpi(),
                                getAttendanceKpi(),
                                getClassesKpi());
        }

        private KpiResponse getRevenueKpi() {
                LocalDate today = LocalDate.now();
                LocalDate startOfMonth = today.withDayOfMonth(1);

                double currentRevenue = pagoRepository.findAll().stream()
                                .filter(p -> p.getFechaPago() != null)
                                .filter(p -> !p.getFechaPago().toLocalDate().isBefore(startOfMonth))
                                .filter(p -> "CONFIRMADO".equals(p.getEstado()))
                                .mapToDouble(p -> p.getMonto().doubleValue())
                                .sum();

                return KpiResponse.builder()
                                .title("Ingresos del Mes")
                                .value((long) currentRevenue)
                                .changePercentage(12.5)
                                .unit("S/.")
                                .icon("attach_money")
                                .gradient("linear-gradient(135deg, #ffd500 0%, #ff8c00 100%)")
                                .build();
        }

        private KpiResponse getMembresiaKpi() {
                long current = membresiaRepository.findAll().stream()
                                .filter(m -> "ACTIVO".equals(m.getEstado().name()))
                                .count();

                return KpiResponse.builder()
                                .title("Membresías Activas")
                                .value(current)
                                .changePercentage(5.0)
                                .unit("")
                                .icon("card_membership")
                                .gradient("linear-gradient(135deg, #00d4aa 0%, #00a896 100%)")
                                .build();
        }

        private KpiResponse getAttendanceKpi() {
                LocalDate today = LocalDate.now();

                long todayCount = asistenciaRepository.findAll().stream()
                                .filter(a -> a.getFechaHora() != null)
                                .filter(a -> a.getFechaHora().toLocalDate().equals(today))
                                .count();

                return KpiResponse.builder()
                                .title("Asistencia Hoy")
                                .value(todayCount)
                                .changePercentage(8.5)
                                .unit("personas")
                                .icon("how_to_reg")
                                .gradient("linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)")
                                .build();
        }

        private KpiResponse getClassesKpi() {
                LocalDate today = LocalDate.now();

                long todayClasses = sesionClaseRepository.findAll().stream()
                                .filter(s -> s.getFecha().equals(today))
                                .filter(s -> !"CANCELADA".equals(s.getEstado()))
                                .count();

                return KpiResponse.builder()
                                .title("Clases Hoy")
                                .value(todayClasses)
                                .changePercentage(5.2)
                                .unit("clases")
                                .icon("fitness_center")
                                .gradient("linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%)")
                                .build();
        }

        private RevenueChartDTO getRevenueChart() {
                List<String> months = new ArrayList<>();
                List<Double> amounts = new ArrayList<>();
                LocalDate today = LocalDate.now();

                for (int i = 5; i >= 0; i--) {
                        LocalDate monthDate = today.minusMonths(i);
                        LocalDate start = monthDate.withDayOfMonth(1);
                        LocalDate end = monthDate.withDayOfMonth(monthDate.lengthOfMonth());

                        String monthName = monthDate.getMonth().getDisplayName(TextStyle.SHORT,
                                        Locale.forLanguageTag("es-ES"));
                        months.add(monthName);

                        double revenue = pagoRepository.findAll().stream()
                                        .filter(p -> p.getFechaPago() != null)
                                        .filter(p -> !p.getFechaPago().toLocalDate().isBefore(start))
                                        .filter(p -> !p.getFechaPago().toLocalDate().isAfter(end))
                                        .filter(p -> "CONFIRMADO".equals(p.getEstado()))
                                        .mapToDouble(p -> p.getMonto().doubleValue())
                                        .sum();

                        amounts.add(revenue);
                }

                return RevenueChartDTO.builder()
                                .months(months)
                                .amounts(amounts)
                                .build();
        }

        private List<MembershipDistributionDTO> getMembershipDistribution() {
                Map<Long, Long> distribution = membresiaRepository.findAll().stream()
                                .filter(m -> "ACTIVO".equals(m.getEstado().name()))
                                .collect(Collectors.groupingBy(m -> m.getPlanId(), Collectors.counting()));

                String[] colors = { "#ffd500", "#00d4aa", "#4facfe", "#ff6b6b", "#9b59b6" };
                int[] colorIndex = { 0 };

                return distribution.entrySet().stream()
                                .map(entry -> {
                                        var plan = planRepository.findById(entry.getKey());
                                        String planName = plan.isPresent() ? plan.get().getNombrePlan()
                                                        : "Plan " + entry.getKey();

                                        return MembershipDistributionDTO.builder()
                                                        .planName(planName)
                                                        .count(entry.getValue())
                                                        .color(colors[colorIndex[0]++ % colors.length])
                                                        .build();
                                })
                                .collect(Collectors.toList());
        }

        private List<TopClassDTO> getTopClasses() {
                Map<String, Long> classCounts = reservaClaseRepository.findAll().stream()
                                .filter(r -> r.getSesionClase() != null)
                                .filter(r -> r.getSesionClase().getHorarioClase() != null)
                                .filter(r -> r.getSesionClase().getHorarioClase().getTipoClase() != null)
                                .collect(Collectors.groupingBy(
                                                r -> r.getSesionClase().getHorarioClase().getTipoClase().getNombre(),
                                                Collectors.counting()));

                return classCounts.entrySet().stream()
                                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                                .limit(5)
                                .map(entry -> TopClassDTO.builder()
                                                .className(entry.getKey())
                                                .instructorName("Instructor")
                                                .totalAttendance(entry.getValue())
                                                .occupancyRate(75.0 + (Math.random() * 20))
                                                .build())
                                .collect(Collectors.toList());
        }

        private AttendanceByDayDTO getAttendanceByDay() {
                List<String> days = Arrays.asList("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom");
                List<Long> attendances = new ArrayList<>();
                LocalDate today = LocalDate.now();
                LocalDate monthAgo = today.minusDays(30);

                for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
                        long count = asistenciaRepository.findAll().stream()
                                        .filter(a -> a.getFechaHora() != null)
                                        .filter(a -> !a.getFechaHora().toLocalDate().isBefore(monthAgo))
                                        .filter(a -> a.getFechaHora().getDayOfWeek() == dayOfWeek)
                                        .count();
                        attendances.add(count);
                }

                return AttendanceByDayDTO.builder()
                                .days(days)
                                .attendances(attendances)
                                .build();
        }
}