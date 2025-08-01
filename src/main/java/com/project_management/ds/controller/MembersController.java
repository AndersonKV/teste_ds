package com.project_management.ds.controller;

import com.project_management.ds.dto.MemberRequest;
import com.project_management.ds.dto.MemberResponse;
import com.project_management.ds.exception.ProjectException;
import com.project_management.ds.model.Cargo;
import com.project_management.ds.model.Member;
import com.project_management.ds.repository.MembersRepository;
import com.project_management.ds.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Membros", description = "Operações relacionadas a membros do projeto")
public class MembersController {

    @Autowired
    private MemberService memberService;

    @PostMapping
    @Operation(summary = "Criar novo membro", description = "Cria um novo membro com nome e atribuição (GERENTE ou FUNCIONARIO)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Membro criado com sucesso",
                    content = @Content(schema = @Schema(implementation = MemberResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content)
    })
    public MemberResponse create(@Valid @RequestBody MemberRequest request) {
        return this.memberService.create(request);
    }

    @GetMapping
    @Operation(summary = "Listar todos os membros", description = "Retorna todos os membros cadastrados no sistema")
    @ApiResponse(responseCode = "200", description = "Lista de membros retornada com sucesso")
    public List<Member> list() {
        return this.memberService.list();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar membro por ID", description = "Retorna um membro pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Membro encontrado com sucesso",
                    content = @Content(schema = @Schema(implementation = Member.class))),
            @ApiResponse(responseCode = "404", description = "Membro não encontrado", content = @Content)
    })
    public ResponseEntity<Member> findById(
            @Parameter(description = "ID do membro a ser buscado", example = "1")
            @PathVariable Long id) {
        var member = this.memberService.findById(id);
        return ResponseEntity.ok(member);
    }
}
