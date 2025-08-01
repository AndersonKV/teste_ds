package com.project_management.ds.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class AssociateMembersRequest {
    @NotEmpty
    @Schema(description = "Lista de IDs dos membros a serem associados ao projeto", example = "[0,0,0]")
    private List<Long> membrosIds;
}
