package com.project_management.ds.dto;

import com.project_management.ds.model.Cargo;
import lombok.Data;

@Data
public class MemberResponse {
    private Long id;
    private String nome;
    private Cargo atribuicao;

    public MemberResponse(Long id, String nome, Cargo atribuicao) {
        this.id = id;
        this.nome = nome;
        this.atribuicao = atribuicao;
    }
}
