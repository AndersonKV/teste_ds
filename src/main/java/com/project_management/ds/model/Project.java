package com.project_management.ds.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Schema(description = "Entidade que representa um projeto no sistema")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único do projeto", example = "1")
    private Long id;

    @Schema(description = "Nome do projeto", example = "Sistema de Gerenciamento")
    private String nome;

    @Schema(description = "Data de início do projeto", example = "2025-08-01")
    private LocalDate dataInicio;

    @Schema(description = "Data prevista para o término do projeto", example = "2025-12-31")
    private LocalDate previsaoTermino;

    @Schema(description = "Data real de término do projeto (caso já finalizado)", example = "2025-11-15")
    private LocalDate dataFim;

    @Schema(description = "Orçamento total do projeto", example = "100000.00")
    private BigDecimal orcamentoTotal;

    @Schema(description = "Descrição detalhada sobre o projeto", example = "Projeto para desenvolvimento de um novo sistema de controle interno.")
    private String descricao;

    @Schema(description = "Avaliação de risco do projeto", example = "Projeto para desenvolvimento de um novo sistema de controle interno.")
    private AvaliacaoRisco avaliacaoRisco;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Status atual do projeto", example = "EM_ANALISE")
    private StatusProject status;

    @ManyToOne
    @Schema(description = "Gerente responsável pelo projeto")
    private Member gerente;

    @ManyToMany
    @Schema(description = "Lista de membros envolvidos no projeto")
    private List<Member> membros;
}
