package com.project_management.ds.service.impl;

import com.project_management.ds.dto.ProjectRequest;
import com.project_management.ds.dto.ProjectSummaryDTO;
import com.project_management.ds.exception.ProjectException;
import com.project_management.ds.mapper.ProjectMapper;
import com.project_management.ds.model.Cargo;
import com.project_management.ds.model.Member;
import com.project_management.ds.model.Project;
import com.project_management.ds.model.StatusProject;
import com.project_management.ds.repository.MembersRepository;
import com.project_management.ds.repository.ProjectRepository;
import com.project_management.ds.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private MembersRepository membersRepository;

    @Autowired
    private ProjectMapper projectMapper;

    private List<StatusProject> ordem = List.of(
            StatusProject.EM_ANALISE,
            StatusProject.ANALISE_REALIZADA,
            StatusProject.ANALISE_APROVADA,
            StatusProject.INICIADO,
            StatusProject.PLANEJADO,
            StatusProject.EM_ANDAMENTO,
            StatusProject.ENCERRADO
    );

    private void valid(Project projeto) {
        this.valid(projeto, false);
    }

    private void valid(Project projeto, boolean ignorarValidacaoDeStatus) {
        if (projeto.getMembros() == null || projeto.getMembros().isEmpty()) {
            throw new ProjectException("O projeto deve ter pelo menos um membro.");
        }

        if (projeto.getMembros().size() > 10) {
            throw new ProjectException("O projeto não pode ter mais de 10 membros.");
        }

        for (Member member : projeto.getMembros()) {
            if (!Cargo.FUNCIONARIO.equals(member.getAtribuicao())) {
                throw new ProjectException("Apenas membros com atribuição 'funcionário' podem ser alocados.");
            }

            List<Project> projetosAtivos = projectRepository.findProjectActiveByMember(
                    member,
                    List.of(StatusProject.ENCERRADO, StatusProject.CANCELADO),
                    projeto.getId()
            );

            if (projetosAtivos.size() >= 3) {
                throw new ProjectException("O membro " + member.getNome() + " já está em 3 projetos ativos.");
            }

        }

        if (!ignorarValidacaoDeStatus && projeto.getId() != null) {
            Project existente = projectRepository.findById(projeto.getId())
                    .orElseThrow(() -> new ProjectException("Projeto não encontrado."));

            StatusProject statusAtual = existente.getStatus();
            StatusProject novoStatus = projeto.getStatus();

            if (!validateTransition(statusAtual, novoStatus)) {
                throw new ProjectException("Transição de status inválida: " + statusAtual + " → " + novoStatus);
            }
        }

        projeto.setAvaliacaoRisco(projectMapper.calcularRisco(projeto));
    }

    private boolean validateTransition(StatusProject atual, StatusProject novo) {
        if (novo == StatusProject.CANCELADO) return true;

        int posAtual = ordem.indexOf(atual);
        int posNovo = ordem.indexOf(novo);

        return posNovo == posAtual + 1;
    }


    @Override
    public Project create(ProjectRequest request) {
        Project projeto = new Project();
        projeto.setNome(request.getNome());
        projeto.setDataInicio(request.getDataInicio());
        projeto.setPrevisaoTermino(request.getPrevisaoTermino());
        projeto.setOrcamentoTotal(request.getOrcamentoTotal());
        projeto.setDescricao(request.getDescricao());
        projeto.setStatus(StatusProject.EM_ANALISE);

        Member gerente = membersRepository.findById(request.getGerenteId())
                .orElseThrow(() -> new ProjectException("Gerente não encontrado."));
        projeto.setGerente(gerente);

        List<Member> membros = membersRepository.findAllById(request.getMembrosIds());
        if (membros.size() != request.getMembrosIds().size()) {
            throw new ProjectException("Um ou mais membros não foram encontrados.");
        }

        projeto.setMembros(membros);

        valid(projeto);

        Project salvo = projectRepository.save(projeto);

        return salvo;
    }

    @Override
    public void delete(Long id) {
        Optional<Project> project = projectRepository.findById(id);
        if (project.isEmpty()) throw new ProjectException("Projeto não foi encontrado.");

        StatusProject currentStatus = project.get().getStatus();

        if (currentStatus == StatusProject.INICIADO ||
                currentStatus == StatusProject.EM_ANDAMENTO ||
                currentStatus == StatusProject.ENCERRADO) {
            throw new ProjectException("Projetos com status iniciado, em andamento ou encerrado não podem ser excluídos.");
        }

        projectRepository.deleteById(id);
    }
    @Transactional
    @Override
    public ProjectSummaryDTO report() {
        List<Project> projetos = projectRepository.findAll();

        Map<String, Long> quantidadePorStatus = projetos.stream()
                .collect(Collectors.groupingBy(p -> p.getStatus().name(), Collectors.counting()));

        Map<String, BigDecimal> totalOrcadoPorStatus = projetos.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getStatus().name(),
                        Collectors.reducing(BigDecimal.ZERO, Project::getOrcamentoTotal, BigDecimal::add)
                ));

        double mediaDuracao = projetos.stream()
                .filter(p -> p.getStatus() == StatusProject.ENCERRADO && p.getDataInicio() != null && p.getDataFim() != null)
                .mapToLong(p -> java.time.temporal.ChronoUnit.DAYS.between(p.getDataInicio(), p.getDataFim()))
                .average()
                .orElse(0);

        Set<Long> membrosUnicos = projetos.stream()
                .flatMap(p -> p.getMembros().stream())
                .map(Member::getId)
                .collect(Collectors.toSet());

        return new ProjectSummaryDTO(quantidadePorStatus, totalOrcadoPorStatus, mediaDuracao, membrosUnicos.size());
    }

    @Transactional
    @Override
    public Project updateStatus(Long id, StatusProject novoStatus) {
        Project projeto = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectException("Projeto não foi encontrado."));

        StatusProject statusAtual = projeto.getStatus();

        if (statusAtual == StatusProject.CANCELADO) {
            throw new ProjectException("Projeto com status CANCELADO não pode ser alterado.");
        }

        if (novoStatus == StatusProject.CANCELADO) {
            projeto.setStatus(novoStatus);
            projectRepository.save(projeto);
            return projeto;
        }

        int indexAtual = ordem.indexOf(statusAtual);
        int indexNovo = ordem.indexOf(novoStatus);

        if (indexAtual == -1 || indexNovo == -1) {
            throw new ProjectException("Status inválido.");
        }

        if (indexNovo != indexAtual + 1) {
            StatusProject proximoPermitido = (indexAtual + 1 < ordem.size()) ? ordem.get(indexAtual + 1) : null;
            String msg = "Transição de status inválida: " + statusAtual + " → " + novoStatus;
            if (proximoPermitido != null) {
                msg += ". O próximo status permitido é: " + proximoPermitido;
            } else {
                msg += ". O projeto já está no último status possível.";
            }
            throw new ProjectException(msg);
        }

        if(novoStatus == StatusProject.ENCERRADO) {
            projeto.setDataFim(LocalDate.now());
        }

        projeto.setStatus(novoStatus);
        projectRepository.save(projeto);
        return projeto;
    }
    @Transactional
    @Override
    public Project associateMembers(Long projectId, List<Long> membersIds) {
        Project projeto = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectException("Projeto não encontrado."));

        List<Member> membrosAtuais = projeto.getMembros();
        if (membrosAtuais == null) {
            membrosAtuais = new ArrayList<>();
        }

        List<Member> novosMembros = membersRepository.findAllById(membersIds);

        for (Member m : novosMembros) {
            if (!membrosAtuais.contains(m)) {
                membrosAtuais.add(m);
            }
        }

        projeto.setMembros(membrosAtuais);


        valid(projeto, true);

        projectRepository.save(projeto);

        return projeto;
    }

    @Transactional
    @Override
    public Page<Project> list(String nome, String status, Pageable pageable) {
        StatusProject statusEnum = null;
        if (status != null) {
            try {
                statusEnum = StatusProject.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ProjectException("Status inválido: " + status);
            }
        }

        Page<Project> projetos = projectRepository.findByFilters(nome, statusEnum, pageable);
        return projetos;
    }
}
