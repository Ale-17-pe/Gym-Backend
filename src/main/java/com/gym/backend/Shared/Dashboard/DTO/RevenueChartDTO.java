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
public class RevenueChartDTO {
    private List<String> months; // ["Ago", "Sep", "Oct", "Nov", "Dic", "Ene"]
    private List<Double> amounts; // [12500.50, 15200.00, ...]
}
