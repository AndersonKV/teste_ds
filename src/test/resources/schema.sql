CREATE TABLE member
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome       VARCHAR(255),
    atribuicao VARCHAR(50)
);

CREATE TABLE project
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome             VARCHAR(255),
    data_inicio      DATE,
    previsao_termino DATE,
    data_fim         DATE,
    orcamento_total  DECIMAL(19, 2),
    descricao        VARCHAR(1000),
    avaliacao_risco  VARCHAR(50),
    status           VARCHAR(50),
    gerente_id       BIGINT,
    CONSTRAINT fk_gerente FOREIGN KEY (gerente_id) REFERENCES member (id)
);

CREATE TABLE project_membros
(
    project_id BIGINT NOT NULL,
    membros_id BIGINT NOT NULL,
    PRIMARY KEY (project_id, membros_id),
    CONSTRAINT fk_project FOREIGN KEY (project_id) REFERENCES project (id),
    CONSTRAINT fk_membro FOREIGN KEY (membros_id) REFERENCES member (id)
);
