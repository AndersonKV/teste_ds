package com.project_management.ds.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Cargos possíveis para membros")
public enum Cargo {
    FUNCIONARIO,
    GERENTE
}
