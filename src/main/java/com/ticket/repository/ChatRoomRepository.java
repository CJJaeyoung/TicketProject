package com.ticket.repository;

import com.ticket.entity.ChatRoom;
import com.ticket.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    ChatRoom findByMember(Member member);

    ChatRoom findByRoomId(Long roomId);

    ChatRoom findByMember_Id(Long memberId);
}
