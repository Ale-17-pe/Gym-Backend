package com.gym.backend.Shared.Dashboard.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopClassDTO {
    private String className;
    private String instructorName;
    private Long totalAttendance;
    private Double occupancyRate;
}
