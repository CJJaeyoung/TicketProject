package com.ticket.service;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class ChatbotService {

    private final String[] greetings = {
            "안녕하세요! 티켓 예매를 도와드릴까요? 😊",
            "반갑습니다! 원하는 공연 티켓을 찾고 계신가요?",
            "어서오세요! EZTicket입니다.",
            "안녕하세요! 어떤 공연을 찾으시나요?",
            "반가워요! 티켓 관련 문의를 도와드릴게요."
    };

    public String getRandomGreeting() {
        Random random = new Random();
        return greetings[random.nextInt(greetings.length)];
    }

    public Map<String, Object> processQuery(String question) {
        question = question.replaceAll("[^a-zA-Z0-9가-힣\\s]", "")
                .toLowerCase()
                .trim();

        Map<String, Object> response = new HashMap<>();

        // 인사 관련 질문
        if (question.contains("안녕") || question.contains("하이") || question.contains("반가워")) {
            response.put("type", "faq");
            response.put("answer", getRandomGreeting());
            return response;
        }

        // 티켓 예매 방법 질문
        if (question.contains("어떻게") && (question.contains("예매") || question.contains("티켓"))) {
            response.put("type", "faq");
            response.put("answer", "공연 페이지에서 원하시는 날짜와 좌석을 선택한 후 결제를 진행하시면 됩니다.");
            return response;
        }

        // 결제 관련 질문
        if (question.contains("결제") || question.contains("방법") || question.contains("카드")) {
            response.put("type", "faq");
            response.put("answer", "신용카드, 체크카드, 간편결제(KakaoPay, 네이버페이 등)로 결제 가능합니다.");
            return response;
        }

        // 환불 및 취소 관련 질문
        if (question.contains("환불") || question.contains("취소") || question.contains("변경")) {
            response.put("type", "faq");
            response.put("answer", "예매하신 티켓은 공연 전까지 환불 및 취소가 가능합니다. 단, 공연에 따라 취소 수수료가 발생할 수 있습니다.");
            return response;
        }

        // 공연 일정 및 좌석 관련 질문
        if (question.contains("공연") || question.contains("일정") || question.contains("시간") || question.contains("좌석")) {
            response.put("type", "faq");
            response.put("answer", "공연 일정 및 좌석 정보는 각 공연 상세 페이지에서 확인하실 수 있습니다.");
            return response;
        }

        // 티켓 가격 질문
        if (question.contains("가격") || question.contains("얼마") || question.contains("티켓비")) {
            response.put("type", "faq");
            response.put("answer", "공연마다 티켓 가격이 다르며, 자세한 사항은 공연 페이지에서 확인해주세요.");
            return response;
        }

        // 로그인 및 회원가입 질문
        if (question.contains("로그인") || question.contains("가입") || question.contains("회원")) {
            response.put("type", "faq");
            response.put("answer", "우측 상단의 로그인 버튼을 클릭하여 로그인하실 수 있습니다. 회원이 아니시면 먼저 가입해주세요.");
            return response;
        }

        // 기본 응답
        response.put("type", "redirect");
        response.put("message", "죄송합니다. 질문을 이해하지 못했습니다. 더 자세한 내용은 고객센터로 문의해주세요.");

        return response;
    }
}
