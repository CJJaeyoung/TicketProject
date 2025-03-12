package com.ticket.repository;

import com.ticket.entity.ChatMessage;
import com.ticket.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomOrderByIdAsc(ChatRoom chatRoom);
} 