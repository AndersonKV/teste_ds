package com.project_management.ds.e2e;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project_management.ds.controller.ProjectController;
import com.project_management.ds.dto.AssociateMembersRequest;
import com.project_management.ds.dto.ProjectRequest;
import com.project_management.ds.dto.StatusUpdateRequest;
import com.project_management.ds.model.*;
import com.project_management.ds.repository.MembersRepository;
import com.project_management.ds.repository.ProjectRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ProjectControllerTest {
    @Autowired
    private ProjectController projectController;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MembersRepository membersRepository;

    @Autowired
    private TestRestTemplate defaultRestTemplate;

    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private Long gerenteId;
    private Long membroId;

    @Test
    void shouldCreateProjectSuccess() {
        var gerente = createMember(Cargo.GERENTE, "gerente 1");
        var funcionario = createMember(Cargo.FUNCIONARIO, "funcionario 1");
        Long gerenteId = gerente.getId();
        Long membroId = funcionario.getId();

        ProjectRequest request = new ProjectRequest();
        request.setNome("Sistema XPTO");
        request.setDataInicio(LocalDate.now());
        request.setPrevisaoTermino(LocalDate.now().plusMonths(6));
        request.setOrcamentoTotal(new BigDecimal("50000"));
        request.setDescricao("Sistema de controle interno.");
        request.setGerenteId(gerenteId);
        request.setMembrosIds(List.of(membroId));
        var response = projectController.create(request);


        Assert.assertEquals(response.getNome(), request.getNome());
        Assert.assertNull(response.getDataFim());
        Assert.assertNotNull(response.getAvaliacaoRisco());
    }


    @Test
    void shouldUpdateStatus() {
        var gerente = createMember(Cargo.GERENTE, "gerente 1");
        var funcionario = createMember(Cargo.FUNCIONARIO, "funcionario 1");
        Long gerenteId = gerente.getId();
        Long membroId = funcionario.getId();

        ProjectRequest request = new ProjectRequest();
        request.setNome("Sistema XPTO");
        request.setDataInicio(LocalDate.now());
        request.setPrevisaoTermino(LocalDate.now().plusMonths(6));
        request.setOrcamentoTotal(new BigDecimal("50000"));
        request.setDescricao("Sistema de controle interno.");
        request.setGerenteId(gerenteId);
        request.setMembrosIds(List.of(membroId));
        var response = projectController.create(request);

        StatusUpdateRequest statusUpdateRequest = new StatusUpdateRequest();
        statusUpdateRequest.setStatus(StatusProject.ANALISE_REALIZADA);

        var responseUpdate = projectController.updateStatus(response.getId(), statusUpdateRequest);

        Project update = responseUpdate.getBody();
        Assert.assertEquals(responseUpdate.getStatusCode(), HttpStatus.OK);
        Assert.assertEquals(update.getStatus(), StatusProject.ANALISE_REALIZADA);
    }

    @Test
    void shouldUpdateStatusCancelado() {
        var gerente = createMember(Cargo.GERENTE, "gerente 1");
        var funcionario = createMember(Cargo.FUNCIONARIO, "funcionario 1");
        Long gerenteId = gerente.getId();
        Long membroId = funcionario.getId();

        ProjectRequest request = new ProjectRequest();
        request.setNome("Sistema XPTO");
        request.setDataInicio(LocalDate.now());
        request.setPrevisaoTermino(LocalDate.now().plusMonths(6));
        request.setOrcamentoTotal(new BigDecimal("50000"));
        request.setDescricao("Sistema de controle interno.");
        request.setGerenteId(gerenteId);
        request.setMembrosIds(List.of(membroId));
        var response = projectController.create(request);

        StatusUpdateRequest statusUpdateRequest = new StatusUpdateRequest();
        statusUpdateRequest.setStatus(StatusProject.CANCELADO);

        var responseUpdate = projectController.updateStatus(response.getId(), statusUpdateRequest);

        Project update = responseUpdate.getBody();
        Assert.assertEquals(responseUpdate.getStatusCode(), HttpStatus.OK);
        Assert.assertEquals(update.getStatus(), StatusProject.CANCELADO);
    }


    @Test
    void shouldFailInTryUpdateStatusCancelado() {
        var gerente = createMember(Cargo.GERENTE, "gerente 1");
        var funcionario = createMember(Cargo.FUNCIONARIO, "funcionario 1");
        Long gerenteId = gerente.getId();
        Long membroId = funcionario.getId();

        ProjectRequest request = new ProjectRequest();
        request.setNome("Sistema XPTO");
        request.setDataInicio(LocalDate.now());
        request.setPrevisaoTermino(LocalDate.now().plusMonths(6));
        request.setOrcamentoTotal(new BigDecimal("50000"));
        request.setDescricao("Sistema de controle interno.");
        request.setGerenteId(gerenteId);
        request.setMembrosIds(List.of(membroId));
        var response = projectController.create(request);
        response.setStatus(StatusProject.CANCELADO);
        projectRepository.save(response);

        StatusUpdateRequest statusUpdateRequest = new StatusUpdateRequest();
        statusUpdateRequest.setStatus(StatusProject.EM_ANALISE);

        try {
            projectController.updateStatus(response.getId(), statusUpdateRequest);
        } catch (Exception ex) {
            Assert.assertEquals(ex.getMessage(), "Projeto com status CANCELADO não pode ser alterado.");
        }

    }


    @Test
    void shouldFailCreateProjectWithoutNames() throws JsonProcessingException {
        restTemplate = defaultRestTemplate.withBasicAuth("admin", "1234");

        var gerente = createMember(Cargo.GERENTE, "gerente 1");
        var funcionario = createMember(Cargo.FUNCIONARIO, "funcionario 1");
        Long gerenteId = gerente.getId();
        Long membroId = funcionario.getId();

        ProjectRequest request = new ProjectRequest();
        request.setNome("");

        request.setDescricao("Sistema de controle interno.");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ProjectRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/projects", entity, String.class
        );

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> errors = objectMapper.readValue(response.getBody(), Map.class);

        Assertions.assertEquals("O nome do projeto é obrigatório e não pode estar em branco.", errors.get("nome"));
        Assertions.assertEquals("A data do projeto é obrigatória.", errors.get("dataInicio"));
        Assertions.assertEquals("A previsão de término do projeto é obrigatória.", errors.get("previsaoTermino"));
        Assertions.assertEquals("O orçamento total do projeto é obrigatório.", errors.get("orcamentoTotal"));
        Assertions.assertEquals("O ID do gerente é obrigatório.", errors.get("gerenteId"));

    }


    @Test
    void shouldSearchListProjectByName() {
        var gerente = createMember(Cargo.GERENTE, "gerente 1");
        var funcionario = createMember(Cargo.FUNCIONARIO, "funcionario 1");
        Long gerenteId = gerente.getId();
        Long membroId = funcionario.getId();

        ProjectRequest request = new ProjectRequest();
        request.setNome("Sistema XPTO");
        request.setDataInicio(LocalDate.now());
        request.setPrevisaoTermino(LocalDate.now().plusMonths(6));
        request.setOrcamentoTotal(new BigDecimal("50000"));
        request.setDescricao("Sistema de controle interno.");
        request.setGerenteId(gerenteId);
        request.setMembrosIds(List.of(membroId));

        projectController.create(request);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Project> result = projectController.list("Sistema", StatusProject.EM_ANALISE.name(), pageable);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1, result.getContent().size());
        Assertions.assertEquals("Sistema XPTO", result.getContent().get(0).getNome());

    }


    @Test
    void shouldSearchListProjectByStatus() {
        var gerente = createMember(Cargo.GERENTE, "gerente 1");
        var funcionario = createMember(Cargo.FUNCIONARIO, "funcionario 1");
        Long gerenteId = gerente.getId();
        Long membroId = funcionario.getId();

        ProjectRequest request = new ProjectRequest();
        request.setNome("Sistema XPTO");
        request.setDataInicio(LocalDate.now());
        request.setPrevisaoTermino(LocalDate.now().plusMonths(6));
        request.setOrcamentoTotal(new BigDecimal("50000"));
        request.setDescricao("Sistema de controle interno.");
        request.setGerenteId(gerenteId);
        request.setMembrosIds(List.of(membroId));

        projectController.create(request);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Project> result = projectController.list("", StatusProject.EM_ANALISE.name(), pageable);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1, result.getContent().size());
        Assertions.assertEquals("Sistema XPTO", result.getContent().get(0).getNome());

    }

    @Test
    void shouldNotFoundListProjectByName() {
        var gerente = createMember(Cargo.GERENTE, "gerente 1");
        var funcionario = createMember(Cargo.FUNCIONARIO, "funcionario 1");
        Long gerenteId = gerente.getId();
        Long membroId = funcionario.getId();

        ProjectRequest request = new ProjectRequest();
        request.setNome("Sistema XPTO");
        request.setDataInicio(LocalDate.now());
        request.setPrevisaoTermino(LocalDate.now().plusMonths(6));
        request.setOrcamentoTotal(new BigDecimal("50000"));
        request.setDescricao("Sistema de controle interno.");
        request.setGerenteId(gerenteId);
        request.setMembrosIds(List.of(membroId));

        projectController.create(request);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Project> result = projectController.list("SDSDS", StatusProject.EM_ANALISE.name(), pageable);

        Assertions.assertTrue(result.isEmpty());

    }


    @Test
    void shouldCreateProjectWithLowRisk() {
        var gerente = createMember(Cargo.GERENTE, "gerente 1");
        var funcionario = createMember(Cargo.FUNCIONARIO, "funcionario 1");
        Long gerenteId = gerente.getId();
        Long membroId = funcionario.getId();

        ProjectRequest request = new ProjectRequest();
        request.setNome("Sistema XPTO");
        request.setDataInicio(LocalDate.now());
        request.setPrevisaoTermino(LocalDate.now().plusMonths(2));
        request.setOrcamentoTotal(new BigDecimal("50000"));
        request.setDescricao("Sistema de controle interno.");
        request.setGerenteId(gerenteId);
        request.setMembrosIds(List.of(membroId));
        var response = projectController.create(request);


        Assert.assertEquals(response.getNome(), request.getNome());
        Assert.assertNull(response.getDataFim());
        Assert.assertEquals(response.getAvaliacaoRisco(), AvaliacaoRisco.BAIXO_RISCO);
    }

    @Test
    void shouldDeleteProject() {
        var gerente = createMember(Cargo.GERENTE, "gerente 1");
        var funcionario = createMember(Cargo.FUNCIONARIO, "funcionario 1");
        Long gerenteId = gerente.getId();
        Long membroId = funcionario.getId();

        ProjectRequest request = new ProjectRequest();
        request.setNome("Sistema XPTO");
        request.setDataInicio(LocalDate.now());
        request.setPrevisaoTermino(LocalDate.now().plusMonths(2));
        request.setOrcamentoTotal(new BigDecimal("50000"));
        request.setDescricao("Sistema de controle interno.");
        request.setGerenteId(gerenteId);
        request.setMembrosIds(List.of(membroId));
        var response = projectController.create(request);
        projectController.delete(response.getId());

        var res = projectRepository.findById(response.getId());

        Assert.assertEquals(res.isPresent(), false);


    }

    @Test
    void shouldFailToDeleteProjectWithStatusIniciado() {
        var gerente = createMember(Cargo.GERENTE, "gerente 1");
        var funcionario = createMember(Cargo.FUNCIONARIO, "funcionario 1");
        Long gerenteId = gerente.getId();
        Long membroId = funcionario.getId();

        ProjectRequest request = new ProjectRequest();
        request.setNome("Sistema XPTO");
        request.setDataInicio(LocalDate.now());
        request.setPrevisaoTermino(LocalDate.now().plusMonths(2));
        request.setOrcamentoTotal(new BigDecimal("50000"));
        request.setDescricao("Sistema de controle interno.");
        request.setGerenteId(gerenteId);
        request.setMembrosIds(List.of(membroId));
        var response = projectController.create(request);
        response.setStatus(StatusProject.INICIADO);
        projectRepository.save(response);

        try {
            projectController.delete(response.getId());
        } catch (Exception ex) {
            Assert.assertEquals(ex.getMessage(), "Projetos com status iniciado, em andamento ou encerrado não podem ser excluídos.");
        }

    }

    @Test
    void shouldFailToDeleteProjectWithStatusEncerrado() {
        var gerente = createMember(Cargo.GERENTE, "gerente 1");
        var funcionario = createMember(Cargo.FUNCIONARIO, "funcionario 1");
        Long gerenteId = gerente.getId();
        Long membroId = funcionario.getId();

        ProjectRequest request = new ProjectRequest();
        request.setNome("Sistema XPTO");
        request.setDataInicio(LocalDate.now());
        request.setPrevisaoTermino(LocalDate.now().plusMonths(2));
        request.setOrcamentoTotal(new BigDecimal("50000"));
        request.setDescricao("Sistema de controle interno.");
        request.setGerenteId(gerenteId);
        request.setMembrosIds(List.of(membroId));
        var response = projectController.create(request);
        response.setStatus(StatusProject.ENCERRADO);
        projectRepository.save(response);

        try {
            projectController.delete(response.getId());
        } catch (Exception ex) {
            Assert.assertEquals(ex.getMessage(), "Projetos com status iniciado, em andamento ou encerrado não podem ser excluídos.");
        }

    }

    @Test
    void shouldFailToDeleteProjectWithStatusEmAndamento() {
        var gerente = createMember(Cargo.GERENTE, "gerente 1");
        var funcionario = createMember(Cargo.FUNCIONARIO, "funcionario 1");
        Long gerenteId = gerente.getId();
        Long membroId = funcionario.getId();

        ProjectRequest request = new ProjectRequest();
        request.setNome("Sistema XPTO");
        request.setDataInicio(LocalDate.now());
        request.setPrevisaoTermino(LocalDate.now().plusMonths(2));
        request.setOrcamentoTotal(new BigDecimal("50000"));
        request.setDescricao("Sistema de controle interno.");
        request.setGerenteId(gerenteId);
        request.setMembrosIds(List.of(membroId));
        var response = projectController.create(request);
        response.setStatus(StatusProject.EM_ANDAMENTO);
        projectRepository.save(response);

        try {
            projectController.delete(response.getId());
        } catch (Exception ex) {
            Assert.assertEquals(ex.getMessage(), "Projetos com status iniciado, em andamento ou encerrado não podem ser excluídos.");
        }

    }


    @Test
    void shouldCreateProjectWithMediumRisk() {
        var gerente = createMember(Cargo.GERENTE, "gerente 1");
        var funcionario = createMember(Cargo.FUNCIONARIO, "funcionario 1");
        Long gerenteId = gerente.getId();
        Long membroId = funcionario.getId();

        ProjectRequest request = new ProjectRequest();
        request.setNome("Sistema XPTO");
        request.setDataInicio(LocalDate.now());
        request.setPrevisaoTermino(LocalDate.now().plusMonths(3));
        request.setOrcamentoTotal(new BigDecimal("500000"));
        request.setDescricao("Sistema de controle interno.");
        request.setGerenteId(gerenteId);
        request.setMembrosIds(List.of(membroId));
        var response = projectController.create(request);


        Assert.assertEquals(response.getNome(), request.getNome());
        Assert.assertNull(response.getDataFim());
        Assert.assertEquals(response.getAvaliacaoRisco(), AvaliacaoRisco.MEDIO_RISCO);
    }


    @Test
    void shouldCreateProjectWithHighRisk() {
        var gerente = createMember(Cargo.GERENTE, "gerente 1");
        var funcionario = createMember(Cargo.FUNCIONARIO, "funcionario 1");
        Long gerenteId = gerente.getId();
        Long membroId = funcionario.getId();

        ProjectRequest request = new ProjectRequest();
        request.setNome("Sistema XPTO");
        request.setDataInicio(LocalDate.now());
        request.setPrevisaoTermino(LocalDate.now().plusMonths(7));
        request.setOrcamentoTotal(new BigDecimal("500001"));
        request.setDescricao("Sistema de controle interno.");
        request.setGerenteId(gerenteId);
        request.setMembrosIds(List.of(membroId));
        var response = projectController.create(request);


        Assert.assertEquals(response.getNome(), request.getNome());
        Assert.assertNull(response.getDataFim());
        Assert.assertEquals(response.getAvaliacaoRisco(), AvaliacaoRisco.ALTO_RISCO);
    }


    @Test
    void shouldAssociateMemberToProject() {
        var gerente = createMember(Cargo.GERENTE, "gerente 1");
        var funcionario = createMember(Cargo.FUNCIONARIO, "funcionario 1");
        var funcionario2 = createMember(Cargo.FUNCIONARIO, "funcionario 2");
        Long gerenteId = gerente.getId();
        Long membroId = funcionario.getId();

        ProjectRequest request = new ProjectRequest();
        request.setNome("Sistema XPTO");
        request.setDataInicio(LocalDate.now());
        request.setPrevisaoTermino(LocalDate.now().plusMonths(7));
        request.setOrcamentoTotal(new BigDecimal("500001"));
        request.setDescricao("Sistema de controle interno.");
        request.setGerenteId(gerenteId);
        request.setMembrosIds(List.of(membroId));
        var response = projectController.create(request);


        Assert.assertEquals(response.getNome(), request.getNome());
        Assert.assertNull(response.getDataFim());
        Assert.assertEquals(response.getAvaliacaoRisco(), AvaliacaoRisco.ALTO_RISCO);

        AssociateMembersRequest associateMembersRequest = new AssociateMembersRequest();
        associateMembersRequest.setMembrosIds(List.of(funcionario2.getId()));

        var responseAssociateMember = projectController.associateMembers(response.getId(), associateMembersRequest);
        Assert.assertEquals(responseAssociateMember.getStatusCode(), HttpStatus.OK);
        Project responseBody = responseAssociateMember.getBody();
        Assert.assertEquals(responseBody.getMembros().size(), 2);
    }

    @Test
    void shouldFailToCreateProjectWithMemberMoreThanThreeProjectActive() {
        var gerente = createMember(Cargo.GERENTE, "gerente 1");
        var funcionario = createMember(Cargo.FUNCIONARIO, "funcionario 1");

        Long gerenteId = gerente.getId();
        Long membroId = funcionario.getId();

        ProjectRequest request = new ProjectRequest();
        request.setNome("Sistema XPTO");
        request.setDataInicio(LocalDate.now());
        request.setPrevisaoTermino(LocalDate.now().plusMonths(7));
        request.setOrcamentoTotal(new BigDecimal("500001"));
        request.setDescricao("Sistema de controle interno.");
        request.setGerenteId(gerenteId);
        request.setMembrosIds(List.of(membroId));

        projectController.create(request);
        projectController.create(request);
        projectController.create(request);
        try {
            projectController.create(request);
        } catch (Exception ex) {
            Assert.assertEquals(ex.getMessage(), "O membro funcionario 1 já está em 3 projetos ativos.");
        }

    }

    @Test
    void shouldGenerateReport() {
        var gerente = createMember(Cargo.GERENTE, "gerente 1");
        var funcionario = createMember(Cargo.FUNCIONARIO, "funcionario 1");

        Long gerenteId = gerente.getId();
        Long membroId = funcionario.getId();

        ProjectRequest request = new ProjectRequest();
        request.setNome("Sistema XPTO");
        request.setDataInicio(LocalDate.now());
        request.setPrevisaoTermino(LocalDate.now().plusMonths(7));
        request.setOrcamentoTotal(new BigDecimal("500001"));
        request.setDescricao("Sistema de controle interno.");
        request.setGerenteId(gerenteId);
        request.setMembrosIds(List.of(membroId));

        projectController.create(request);

        request.setOrcamentoTotal(new BigDecimal("500000"));

        projectController.create(request);

        request.setOrcamentoTotal(new BigDecimal("500"));
        request.setPrevisaoTermino(LocalDate.now().plusMonths(2));

        projectController.create(request);

        var responseReport = projectController.report();


        Assert.assertEquals(1, responseReport.getQuantidadePorStatus().size());
        Assert.assertTrue(responseReport.getQuantidadePorStatus().containsKey("EM_ANALISE"));
        Assert.assertEquals(3L, responseReport.getQuantidadePorStatus().get("EM_ANALISE").longValue());

        Assert.assertEquals(1, responseReport.getTotalOrcadoPorStatus().size());
        Assert.assertTrue(responseReport.getTotalOrcadoPorStatus().containsKey("EM_ANALISE"));
        Assert.assertEquals(new BigDecimal("1000501.00"), responseReport.getTotalOrcadoPorStatus().get("EM_ANALISE"));

        Assert.assertEquals(0.0, responseReport.getMediaDuracaoEncerrados(), 0.001);

        Assert.assertEquals(1, responseReport.getTotalMembrosUnicos());

    }

    public Member createMember(Cargo type, String name) {
        Member member = new Member();
        member.setNome(name);
        member.setAtribuicao(type);
        member = membersRepository.save(member);
        return member;
    }
}
