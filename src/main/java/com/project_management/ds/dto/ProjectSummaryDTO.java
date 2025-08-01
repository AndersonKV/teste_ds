package com.project_management.ds.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
public class ProjectSummaryDTO {
    private Map<String, Long> quantidadePorStatus;
    private Map<String, BigDecimal> totalOrcadoPorStatus;
    private double mediaDuracaoEncerrados;
    private long totalMembrosUnicos;
}
