package com.ticket.repository;

import com.ticket.dto.MemberSearchDto;
import com.ticket.entity.Member;

import java.util.List;

public interface MemberRepositoryCustom {
    List<Member> searchMembers(MemberSearchDto memberSearchDto);
}
