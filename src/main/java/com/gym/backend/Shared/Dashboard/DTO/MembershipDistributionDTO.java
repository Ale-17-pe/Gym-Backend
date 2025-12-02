package com.gym.backend.Shared.Dashboard.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipDistributionDTO {
    private String planName;
    private Long count;
    private String color;
}
