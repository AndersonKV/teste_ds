package com.project_management.ds.service;

import com.project_management.ds.dto.MemberRequest;
import com.project_management.ds.dto.MemberResponse;
import com.project_management.ds.model.Member;

import java.util.List;

public interface MemberService {
    MemberResponse create(MemberRequest request);

    List<Member> list();

    Member findById(Long id);
}
