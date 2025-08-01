package com.project_management.ds.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Avaliação de risco dos projetos")
public enum AvaliacaoRisco {
    BAIXO_RISCO, MEDIO_RISCO,ALTO_RISCO

}
