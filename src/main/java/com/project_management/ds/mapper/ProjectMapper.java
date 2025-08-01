package com.project_management.ds.mapper;
import com.project_management.ds.dto.ProjectRequest;
import com.project_management.ds.dto.ProjectResponse;
import com.project_management.ds.model.AvaliacaoRisco;
import com.project_management.ds.model.Project;
import com.project_management.ds.service.MemberMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = MemberMapper.class)
public interface ProjectMapper {

    Project toEntity(ProjectRequest request);

    ProjectResponse toDto(Project project);

    default AvaliacaoRisco calcularRisco(Project project) {
        long meses = java.time.temporal.ChronoUnit.MONTHS.between(
                project.getDataInicio(), project.getPrevisaoTermino()
        );

        boolean curtoPrazo = meses <= 3;
        boolean medioPrazo = meses > 3 && meses <= 6;

        if (project.getOrcamentoTotal().compareTo(new java.math.BigDecimal("100000")) <= 0 && curtoPrazo) {
            return AvaliacaoRisco.BAIXO_RISCO;
        } else if (
                (project.getOrcamentoTotal().compareTo(new java.math.BigDecimal("100001")) >= 0 &&
                        project.getOrcamentoTotal().compareTo(new java.math.BigDecimal("500000")) <= 0) || medioPrazo
        ) {
            return AvaliacaoRisco.MEDIO_RISCO;
        } else {
            return AvaliacaoRisco.ALTO_RISCO;
        }
    }
}
