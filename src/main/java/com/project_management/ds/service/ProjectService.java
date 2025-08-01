package com.project_management.ds.service;

import com.project_management.ds.dto.ProjectRequest;
import com.project_management.ds.dto.ProjectSummaryDTO;
import com.project_management.ds.model.Project;
import com.project_management.ds.model.StatusProject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProjectService {
    Project create(ProjectRequest request);
    void delete(Long id);
    ProjectSummaryDTO report();
    Project updateStatus(Long id, StatusProject status);
    Project associateMembers(Long projectId, List<Long> membersIds);
    Page<Project> list(String nome, String status, Pageable pageable);
}