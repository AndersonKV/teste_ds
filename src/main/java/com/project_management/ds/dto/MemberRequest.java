package com.project_management.ds.dto;

import com.project_management.ds.model.Cargo;
import com.project_management.ds.validation.ValidCreateAssignment;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MemberRequest {
    @NotNull(message = "Nome vazio.")
    @NotEmpty(message = "Nome vazio.")
    private String nome;
    @NotNull(message = "Atribuição vazio.")
    @NotEmpty(message = "Atribuição vazio.")
    @ValidCreateAssignment(enumClass = Cargo.class, message = "Atribuição inválida. Use FUNCIONARIO ou GERENTE.")
    private String atribuicao;
}
