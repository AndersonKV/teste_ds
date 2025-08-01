package com.project_management.ds.dto;

import com.project_management.ds.model.StatusProject;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class ProjectResponse {
    private Long id;
    private String nome;
    private LocalDate dataInicio;
    private LocalDate previsaoTermino;
    private LocalDate dataFim;
    private BigDecimal orcamentoTotal;
    private String descricao;
    private StatusProject status;
    private String classificacaoRisco;
    private MemberRequest gerente;
    private List<MemberRequest> membros;


}
