package com.project_management.ds.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum StatusProject {
    EM_ANALISE,
    ANALISE_REALIZADA,
    ANALISE_APROVADA,
    INICIADO,
    PLANEJADO,
    EM_ANDAMENTO,
    ENCERRADO,
    CANCELADO
}

