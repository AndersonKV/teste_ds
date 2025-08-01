package com.project_management.ds.controller;
import com.project_management.ds.dto.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.project_management.ds.model.Project;
import com.project_management.ds.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/projects")
@Tag(name = "Projetos", description = "Gerenciamento de projetos")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Operation(summary = "Cria um novo projeto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Projeto criado com sucesso",
                    content = @Content(schema = @Schema(implementation = Project.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public Project create(@RequestBody @Valid ProjectRequest request) {
        return projectService.create(request);
    }

    @PutMapping("/{id}/membros")
    @Operation(summary = "Associa membros a um projeto", description = "Substitui a lista de membros do projeto pelos IDs fornecidos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Membros associados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Projeto ou membro não encontrado")
    })
    public ResponseEntity<Project> associateMembers(
            @PathVariable Long id,
            @Valid @RequestBody AssociateMembersRequest request
    ) {
        Project response = projectService.associateMembers(id, request.getMembrosIds());
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Busca um projeto pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Projeto encontrado"),
            @ApiResponse(responseCode = "404", description = "Projeto não encontrado")
    })
    @GetMapping("/relatorio")
    public ProjectSummaryDTO report() {
        return projectService.report();
    }

    @Operation(summary = "Exclui um projeto pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Projeto excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Projeto não encontrado")
    })
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        projectService.delete(id);
    }

    @Operation(summary = "Lista projetos com filtros opcionais de nome e status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de projetos retornada com sucesso")
    })
    @GetMapping
    public Page<Project> list(
            @Parameter(description = "Filtra por nome do projeto", example = "Sistema")
            @RequestParam(required = false) String nome,
            @Parameter(description = "Filtra por status do projeto", example = "INICIADO")
            @RequestParam(required = false) String status,
            @Parameter(hidden = true) @ParameterObject Pageable pageable
    ) {
        return projectService.list(nome, status, pageable);
    }



    @Operation(summary = "Atualiza o status do projeto",
            description = "Atualiza o status do projeto seguindo a sequência lógica permitida ou cancela o projeto.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Transição de status inválida"),
            @ApiResponse(responseCode = "404", description = "Projeto não encontrado")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<Project> updateStatus(
            @Parameter(description = "ID do projeto") @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request
    ) {
        Project response = projectService.updateStatus(id, request.getStatus());
        return ResponseEntity.ok(response);
    }
}
