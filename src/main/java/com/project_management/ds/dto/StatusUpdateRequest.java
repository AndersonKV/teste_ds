package com.project_management.ds.dto;

import com.project_management.ds.model.StatusProject;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatusUpdateRequest {

    @NotNull
    @Schema(description = "Novo status do projeto", example = "ANALISE_REALIZADA", implementation = StatusProject.class)
    private StatusProject status;
}
