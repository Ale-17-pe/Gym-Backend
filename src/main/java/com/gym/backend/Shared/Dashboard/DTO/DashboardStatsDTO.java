package com.gym.backend.Shared.Dashboard.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private List<KpiResponse> kpis;
    private RevenueChartDTO revenueChart;
    private List<MembershipDistributionDTO> membershipDistribution;
    private List<TopClassDTO> topClasses;
    private AttendanceByDayDTO attendanceByDay;
}
