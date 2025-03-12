package com.ticket.service;

import com.ticket.entity.ChatRoom;
import com.ticket.entity.Member;
import com.ticket.repository.ChatRoomRepository;
import com.ticket.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    public ChatRoom findByChatRoomId(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId);
        if (chatRoom == null) {
            throw new IllegalArgumentException("ChatRoom not found for roomId: " + roomId);
        }

        // ChatRoom에 연결된 Member 확인
        Member member = chatRoom.getMember();
        String memberEmail = member.getEmail();
        System.out.println("Member Email : " + memberEmail);

        return chatRoom;
    }


    @Transactional
    public ChatRoom createRoom(Long memberId) {
        try {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new IllegalArgumentException("Member not found: " + memberId));
            ChatRoom existingRoom = chatRoomRepository.findByMember(member);
            if (existingRoom != null) {
                return existingRoom;  // 기존 채팅방이 있다면 해당 객체 반환
            }
            // 새로운 채팅방 생성
            ChatRoom chatRoom = new ChatRoom();
            chatRoom.setRoomId(memberId);  // RoomId를 회원 Id로 설정
            chatRoom.setMember(member); // 채팅방과 회원 연결
            return chatRoomRepository.save(chatRoom); // 생성된 채팅방 저장 및 반환
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    public boolean validateAccess(Long roomId, Long memberId) {
        // roomId로 ChatRoom을 찾고 해당 ChatRoom의 MemberId와 주어진 memberId를 비교
        return chatRoomRepository.findById(roomId)
                .map(room -> room.getMember().getId().equals(memberId)) // memberId가 일치하면 true 반환
                .orElse(false);
    }

    public ChatRoom findByMemberId(Long memberId) {
        return chatRoomRepository.findByMember_Id(memberId);
    }

}
