package com.ticket.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberSearchDto {

    private String searchBy;
    private String name;
    private String tel;
    private String searchQuery = "";

}
