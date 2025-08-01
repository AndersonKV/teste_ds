package com.project_management.ds.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "Objeto de requisição para criação e atualização de projetos")
public class ProjectRequest {

    @NotBlank(message = "O nome do projeto é obrigatório e não pode estar em branco.")
    private String nome;

    @NotNull(message = "A data do projeto é obrigatória.")
    @Schema(description = "Data de início do projeto", example = "2025-08-01", required = true)
    private LocalDate dataInicio;

    @NotNull(message = "A previsão de término do projeto é obrigatória.")
    @Schema(description = "Previsão de término do projeto", example = "2025-12-31", required = true)
    private LocalDate previsaoTermino;

    @NotNull(message = "O orçamento total do projeto é obrigatório.")
    @Schema(description = "Orçamento total disponível para o projeto", example = "100000.00", required = true)
    private BigDecimal orcamentoTotal;

    @Schema(description = "Descrição detalhada do projeto", example = "Este projeto visa desenvolver um sistema de controle interno para empresas.")
    private String descricao;

    @NotNull(message = "O ID do gerente é obrigatório.")
    private Long gerenteId;

    private List<Long> membrosIds;
}
