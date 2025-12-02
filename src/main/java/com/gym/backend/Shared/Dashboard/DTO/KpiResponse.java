package com.gym.backend.Shared.Dashboard.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KpiResponse {
    private String title;
    private Long value;
    private Double changePercentage;
    private String unit;
    private String icon;
    private String gradient;
}
