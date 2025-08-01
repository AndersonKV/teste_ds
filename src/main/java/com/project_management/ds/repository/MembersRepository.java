package com.project_management.ds.repository;

import com.project_management.ds.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembersRepository extends JpaRepository<Member, Long> {

}
