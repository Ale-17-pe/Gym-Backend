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
public class AttendanceByDayDTO {
    private List<String> days; // ["Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"]
    private List<Long> attendances; // [120, 145, 130, ...]
}
