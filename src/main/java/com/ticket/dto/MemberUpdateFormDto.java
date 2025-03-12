package com.ticket.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberUpdateFormDto {
    
    private Long id;
    private String name;
    private String email;
    @NotBlank(message = "전화번호는 필수 입력 값입니다.")
    private String tel;

}
