# 🎟 **Spring Boot 티켓팅 홈페이지 프로젝트**

> 간편한 티켓 예약 및 관리 서비스를 제공하는 티켓팅 홈페이지입니다.

---

## ✨ 프로젝트 소개

본 프로젝트는 **Spring Boot** 기반의 개인 프로젝트로, 공연이나 스포츠 이벤트 등 다양한 티켓 예약 및 관리를 위한 웹서비스입니다.

- 간편한 회원가입 및 로그인
- 실시간 좌석 예약 시스템
- 예약한 티켓의 관리 및 확인 기능
- 관리자 1:1 상담
- 자주 묻는 질문 QnA 답변

---

## 🚀 주요 기능

- **회원 관리**: 회원가입, 로그인, 마이페이지
- **티켓 예매**: 실시간 좌석 선택 및 예약
- **예약 관리**: 예약 확인, 수정 및 취소
- **관리자 페이지**: 회원 조회/수정, 공연 등록, 배너 등록, 채팅 관리

---

## 🛠 기술 스택

### 환경
- 운영체제: Windows 11
- IDE: IntelliJ
- 빌드 도구: Maven

### 언어 및 프레임워크
- Java 22
- Spring Boot
- Spring Security
- JPA

### 데이터베이스
- MySQL
- Redis

### 프론트엔드
- HTML
- CSS
- JavaScript
- Bootstrap
- Thymeleaf

### REST API
- PortOne
- KOPIS
- KakaoMap
- OAuth

---

## 🗂 프로젝트 구조

```text
TicketProject
└── Ticket
    ├── src
    │   ├── main
    │   │   ├── java
    │   │   │   └── com.ticket
    │   │   │       ├── TicketApplication.java
    │   │   │       ├── config
    │   │   │       │   ├── AuditConfig.java
    │   │   │       │   ├── AuditorAwareImpl.java
    │   │   │       │   ├── CorsConfig.java
    │   │   │       │   ├── CustomAuthenticationEntryPoint.java
    │   │   │       │   ├── CustomOAuth2UserService.java
    │   │   │       │   ├── MailConfig.java
    │   │   │       │   ├── OAuthAttributes.java
    │   │   │       │   ├── RedisConfig.java
    │   │   │       │   ├── SecurityConfig.java
    │   │   │       │   ├── SecurityUtil.java
    │   │   │       │   ├── WebClientConfig.java
    │   │   │       │   ├── WebMvcConfig.java
    │   │   │       │   └── WebSocketConfig.java
    │   │   │       ├── constant
    │   │   │       │   ├── Genre.java
    │   │   │       │   └── Role.java
    │   │   │       ├── controller
    │   │   │       │   ├── AdminController.java
    │   │   │       │   ├── ChatbotController.java
    │   │   │       │   ├── ChatController.java
    │   │   │       │   ├── ItemController.java
    │   │   │       │   ├── KopisController.java
    │   │   │       │   ├── MainController.java
    │   │   │       │   ├── MemberController.java
    │   │   │       │   ├── PaymentController.java
    │   │   │       │   └── TicketingController.java
    │   │   │       ├── dto
    │   │   │       │   ├── BannerImgDto.java
    │   │   │       │   ├── BannerFormDto.java
    │   │   │       │   ├── ChatNotification.java
    │   │   │       │   ├── ChatRoomDto.java
    │   │   │       │   ├── ItemCrawlDto.java
    │   │   │       │   ├── ItemCrawlSearchDto.java
    │   │   │       │   ├── ItemFormDto.java
    │   │   │       │   ├── ItemImgDto.java
    │   │   │       │   ├── ItemSearchDto.java
    │   │   │       │   ├── KopisDto.java
    │   │   │       │   ├── KopisDtoWrapper.java
    │   │   │       │   ├── MemberDto.java
    │   │   │       │   ├── MemberFormDto.java
    │   │   │       │   ├── MemberSearchDto.java
    │   │   │       │   ├── MemberUpdateFormDto.java
    │   │   │       │   ├── PaymentDto.java
    │   │   │       │   └── SessionUser.java
    │   │   │       ├── entity
    │   │   │       │   ├── BannerImg.java
    │   │   │       │   ├── Banners.java
    │   │   │       │   ├── BaseEntity.java
    │   │   │       │   ├── BaseTimeEntity.java
    │   │   │       │   ├── ChatMessage.java
    │   │   │       │   ├── ChatRoom.java
    │   │   │       │   ├── Item.java
    │   │   │       │   ├── ItemCrawl.java
    │   │   │       │   ├── ItemImg.java
    │   │   │       │   ├── Member.java
    │   │   │       │   └── Payment.java
    │   │   │       ├── exception
    │   │   │       │   └── OutOfStockException.java
    │   │   │       ├── repository
    │   │   │       └── service
    │   │   │           ├── BannerService.java
    │   │   │           ├── ChatbotService.java
    │   │   │           ├── ChatService.java
    │   │   │           ├── FileService.java
    │   │   │           ├── ItemImgService.java
    │   │   │           ├── ItemService.java
    │   │   │           ├── KopisService.java
    │   │   │           ├── MailService.java
    │   │   │           ├── MemberService.java
    │   │   │           ├── PaymentService.java
    │   │   │           └── TicketingService.java
    │   │   └── resources
    │   │       ├── static
    │   │       ├── templates
    │   │             ├── application.properties
    │   │       └── application-oauth.properties
    └── pom.xml
```

---
