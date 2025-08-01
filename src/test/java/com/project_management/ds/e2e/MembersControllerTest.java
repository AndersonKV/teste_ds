package com.project_management.ds.e2e;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project_management.ds.controller.MembersController;
import com.project_management.ds.dto.MemberRequest;
import com.project_management.ds.model.Cargo;
import com.project_management.ds.model.Member;
import com.project_management.ds.repository.MembersRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;


import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MembersControllerTest {
    @Autowired
    private MembersController membersController;

    @Autowired
    private MembersRepository membersRepository;

    @Autowired
    private TestRestTemplate defaultRestTemplate;

    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;


    @Test
    void shouldCreateGerente() {
        MemberRequest request = new MemberRequest();
        request.setNome("gerente 1");
        request.setAtribuicao(Cargo.GERENTE.name());

        var response = membersController.create(request);

        Assert.assertEquals(response.getNome(), request.getNome());
        Assert.assertEquals(response.getAtribuicao().name(), Cargo.GERENTE.name());
    }

    @Test
    void shouldCreateFuncionario() {
        MemberRequest request = new MemberRequest();
        request.setNome("funcionario 1");
        request.setAtribuicao(Cargo.FUNCIONARIO.name());

        var response = membersController.create(request);

        Assert.assertEquals(response.getNome(), request.getNome());
        Assert.assertEquals(response.getAtribuicao().name(), Cargo.FUNCIONARIO.name());
    }


    @Test
    void shouldFindMemberById() {
        MemberRequest request = new MemberRequest();
        request.setNome("funcionario 1");
        request.setAtribuicao(Cargo.FUNCIONARIO.name());

        var response = membersController.create(request);
        var getMember = membersController.findById(response.getId());

        Member memberBody = getMember.getBody();
        Assert.assertEquals(memberBody.getNome(), request.getNome());
        Assert.assertEquals(memberBody.getAtribuicao().name(), Cargo.FUNCIONARIO.name());
    }

    @Test
    void shouldReturnList() {
        MemberRequest request = new MemberRequest();
        request.setNome("funcionario 1");
        request.setAtribuicao(Cargo.FUNCIONARIO.name());

        membersController.create(request);
        var getMembers = membersController.list();
        Assert.assertEquals(getMembers.size(), 1);
    }

    @Test
    void shouldNotFindMemberById() {
        MemberRequest request = new MemberRequest();
        request.setNome("funcionario 1");
        request.setAtribuicao(Cargo.FUNCIONARIO.name());

        membersController.create(request);
        try {
            membersController.findById(0l);
        } catch (Exception ex) {
            Assert.assertEquals(ex.getMessage(), "Id não encontrado.");
        }

    }


    @Test
    void shouldFailInCreateMemberWithEmptyColumn() throws JsonProcessingException {
        restTemplate = defaultRestTemplate.withBasicAuth("admin", "1234");

        MemberRequest request = new MemberRequest();


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<MemberRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/members", entity, String.class
        );

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> errors = objectMapper.readValue(response.getBody(), Map.class);

        Assertions.assertEquals("Atribuição vazio.", errors.get("atribuicao"));
        Assertions.assertEquals("Nome vazio.", errors.get("nome"));

    }

    @Test
    void shouldFailInCreateMemberWithCargoNameWrong() throws JsonProcessingException {
        restTemplate = defaultRestTemplate.withBasicAuth("admin", "1234");

        MemberRequest request = new MemberRequest();
        request.setAtribuicao("teste");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<MemberRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/members", entity, String.class
        );

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> errors = objectMapper.readValue(response.getBody(), Map.class);

        Assertions.assertEquals("Atribuição inválida. Use FUNCIONARIO ou GERENTE.", errors.get("atribuicao"));

    }

}
