package com.ticket.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomDto {

    private Long roomId;
    private Long memberId;
    private String sender;
    private String message;
}