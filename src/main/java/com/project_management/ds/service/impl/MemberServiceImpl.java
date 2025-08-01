package com.project_management.ds.service.impl;

import com.project_management.ds.dto.MemberRequest;
import com.project_management.ds.dto.MemberResponse;
import com.project_management.ds.exception.ProjectException;
import com.project_management.ds.model.Cargo;
import com.project_management.ds.model.Member;
import com.project_management.ds.repository.MembersRepository;
import com.project_management.ds.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberServiceImpl implements MemberService {
    @Autowired
    private MembersRepository membersRepository;

    @Override
    public MemberResponse create(MemberRequest request) {
        Member member = new Member();
        member.setNome(request.getNome());
        member.setAtribuicao(Cargo.valueOf(request.getAtribuicao().toUpperCase()));
        member = membersRepository.save(member);
        return new MemberResponse(member.getId(), member.getNome(), member.getAtribuicao());
    }

    @Override
    public List<Member> list() {
        return this.membersRepository.findAll();
    }

    @Override
    public Member findById(Long id) {
        return membersRepository.findById(id).orElseThrow(() -> new ProjectException("Id não encontrado."));
    }
}
