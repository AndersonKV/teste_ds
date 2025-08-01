package com.project_management.ds.service;

import com.project_management.ds.dto.MemberResponse;
import com.project_management.ds.model.Member;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    MemberResponse toDto(Member member);
}
