package com.ticket.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class MemberFormDto {
    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;
    @Email
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    private String email;
    @NotEmpty(message = "비밀번호는 필수 입력 값입니다.")
    @Length(min = 4,max = 16,message = "비밀번호는 4자이상, 16자 이하로 입력해주세요.")
//        @Pattern(regexp="(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}",
//         message = "비밀번호는 영문 대,소문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 8자 ~ 20자의 비밀번호여야 합니다.")
    private String password;
    @NotBlank(message = "비밀번호 재확인을 입력해주세요.")
    private String confirmPassword;
    @NotBlank(message = "전화번호는 필수 입력 값입니다.")
    private String tel;

    public boolean isPasswordMatch() {
        return password != null && password.equals(confirmPassword);
    }

}
