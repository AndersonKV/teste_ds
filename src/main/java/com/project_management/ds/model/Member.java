package com.project_management.ds.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Schema(description = "Entidade que representa um membro do projeto, podendo ser um gerente ou um funcionário.")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Nome do membro", example = "João da Silva")
    private String nome;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Atribuição do membro no projeto, como FUNCIONARIO ou GERENTE", example = "GERENTE")
    private Cargo atribuicao;
}
