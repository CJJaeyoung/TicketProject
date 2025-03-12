package com.ticket.dto;

import com.ticket.constant.Role;
import com.ticket.entity.Member;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberDto {
    private Long id;
    private String name;
    private String email;
    private String tel;
    private Role role;

    public static MemberDto fromEntity(Member member) {
        if (member == null) {
            return null;
        }

        MemberDto dto = new MemberDto();
        dto.setId(member.getId());
        dto.setName(member.getName());
        dto.setEmail(member.getEmail());
        dto.setTel(member.getTel());
        dto.setRole(member.getRole());
        return dto;
    }
    
    
}
