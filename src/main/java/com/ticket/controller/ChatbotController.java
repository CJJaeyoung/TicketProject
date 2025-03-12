package com.ticket.controller;

import com.ticket.service.ChatbotService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/api")
public class ChatbotController {

    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @GetMapping("/chatbot")
    public String chatbotPage() {
        return "chat/chatbot";
    }

    @PostMapping("/chatbot/query")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> query(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        Map<String, Object> response = chatbotService.processQuery(question);
        return ResponseEntity.ok(response);
    }
}