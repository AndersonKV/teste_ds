package com.project_management.ds.repository;

import com.project_management.ds.model.Member;
import com.project_management.ds.model.Project;
import com.project_management.ds.model.StatusProject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Page<Project> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    Page<Project> findByStatus(StatusProject status, Pageable pageable);

    Page<Project> findByNomeContainingIgnoreCaseAndStatus(String nome, StatusProject status, Pageable pageable);

    long countByStatus(StatusProject status);

    @Query("SELECT SUM(p.orcamentoTotal) FROM Project p WHERE p.status = :status")
    BigDecimal sumOrcamentoTotalByStatus(@Param("status") StatusProject status);

    @Query("""
            SELECT p FROM Project p 
            JOIN p.membros m 
            WHERE m = :member 
            AND p.status NOT IN (:statusIgnorados)
            AND (:projetoAtual IS NULL OR p.id <> :projetoAtual)
            """)
    List<Project> findProjectActiveByMember(Member member, List<StatusProject> statusIgnorados, Long projetoAtual);

    @Query("""
                SELECT p FROM Project p
                WHERE (:nome IS NULL OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
                  AND (:status IS NULL OR p.status = :status)
            """)
    Page<Project> findByFilters(
            @Param("nome") String nome,
            @Param("status") StatusProject status,
            Pageable pageable
    );

}
