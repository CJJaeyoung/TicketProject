![Image](https://github.com/user-attachments/assets/099d1ec3-50e9-4b7c-be76-b6b73146eb53)

# 🎫 **Spring Boot 티켓팅 홈페이지 프로젝트**

간편한 티켓 예약 및 관리를 제공하는 실시간 티켓팅 플랫폼, EzTicket


<br>
<br>

## ✨ 프로젝트 소개

본 프로젝트는 Spring Boot 기반의 1인 개인 프로젝트로,
실제 서비스 환경에 가까운 기능과 구조를 구현하는 것을 목표로 개발되었습니다.

‘이지티켓(EzTicket)’은 공연, 전시, 콘서트 등의 이벤트를 쉽고 빠르게 예매할 수 있도록 설계된 티켓팅 플랫폼입니다.
실시간 좌석 선택, 대기열 시스템, 소셜 로그인, 결제 연동 등
사용자의 편의성과 서비스의 안정성을 모두 고려한 구조로 개발되었습니다.

또한 Redis 기반의 대기열 처리, 외부 API(KOPIS, KakaoMap 등) 연동,
관리자 페이지 구현 등을 통해 실제 운영 서비스 수준의 기술 경험을 쌓는 데 집중하였습니다.



<br>
<br>

## 🚀 주요 기능

- **회원 관리**: 회원가입, 소셜 로그인(OAuth), 마이페이지
- **티켓 예매**: 공연 상세 조회, 실시간 예매
- **예약 관리**: 예약 확인, 수정 및 취소
- **편의 기능**: 실시간 1:1 채팅, 자주 묻는 질문(Q&A) 응답 기능
- **관리자 페이지**: 회원 정보 관리, 공연 등록, 배너 이미지 관리, 채팅 내역 확인

<br>
<br>

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

<br>
<br>

## ⚙️ 기술 선정 이유
- Spring Boot : 빠르고 효율적인 애플리케이션 개발을 위해 사용하였으며, 자동 설정 및 내장 서버 지원을 통해 개발 및 배포 속도를 높이고, 마이크로서비스 아키텍처와의 뛰어난 호환성으로 확장성과 유지보수성을 강화하였습니다.

- Redis : 빠른 데이터 처리와 뛰어난 동시성 제어 능력을 바탕으로, 티켓팅 과정에서 좌석 중복 예약을 효과적으로 방지할 수 있었으며, 캐싱 기능을 통해 서버 부하를 줄이고 서비스의 응답 속도와 안정성을 높일 수 있어 선택하게 되었습니다. 

- MySQL : 안정적이고 고성능의 관계형 데이터베이스 관리 시스템(RDBMS)으로, 대규모 데이터 처리와 트랜잭션 관리에 적합하며, 높은 확장성과 다양한 플랫폼에서의 호환성 덕분에 신뢰성 있는 데이터 저장 솔루션을 제공하였습니다.

- Websocket : 실시간으로 소통할 수 있는 데이터를 주고 받을 수 있는 1:1 고객 상담 채팅 기능이 필요하다. 그러기 위해선 단방향 통신이 아닌 이벤트가 발생하는 즉시 데이터를 전송하는 구조인 양방향 통신을 지원하는 웹 소켓을 활용했습니다.
<br>

API
- Kopis : 공식적으로 관리되는 KOPIS의 신뢰성 높은 공연 데이터를 활용하여, 사용자에게 정확하고 최신의 공연 정보를 제공하기 위해 선정했습니다.

- KakaoMap : 국내 위치 데이터가 풍부하고 정확하며, 실시간으로 위치 정보를 제공하여 사용자들에게 최적의 장소 안내 및 접근성을 제공합니다.

- PortOne : 안전한 결제 시스템과 개인정보 보호 및 결제 정보 보안을 지키기 위해 사용하였으며, 사용자에게 다양한 결제 수단을 제공합니다.

- OAuth : 사용자 비밀번호를 직접 처리하지 않고, 안전한 토큰 기반 인증을 제공하여 보안을 강화하고, 여러 서비스에서 중앙 집중식 인증을 통해 사용자 편의성을 높이며, 세분화된 권한 부여로 데이터 접근을 최소화하여 보안 리스크를 줄였습니다.
<br>

CI/CD
- AWS : 사용자가 필요에 따라 손쉽게 리소스를 확장하거나 축소할 수 있으며, 전 세계 여러 리전에 데이터 센터를 운영해 지역별로 높은 가용성을 유지할 수 있어 자연 재해나 데이터 센터 장애와 같은 상황에서도 비즈니스를 안전하게 보호할 수 있기 때문에 선택했습니다.

<br>
<br>

## 🗂 프로젝트 구조

<details><summary>📊 데이터베이스
</summary>

![Image](https://github.com/user-attachments/assets/f526250d-9119-4d98-af80-27c73f41b403)
## 


</details>


<details><summary>📦 디렉토리 구조
</summary>

    
```
📂TicketProject
└── 📂Ticket
    ├── 📂src
    │   ├── 📂main
    │   │   ├── 📂java
    │   │   │   └── 📂com.ticket
    │   │   │       ├── TicketApplication.java
    │   │   │       ├── 📂config
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
    │   │   │       ├── 📂constant
    │   │   │       │   ├── Genre.java
    │   │   │       │   └── Role.java
    │   │   │       ├── 📂controller
    │   │   │       │   ├── AdminController.java
    │   │   │       │   ├── ChatbotController.java
    │   │   │       │   ├── ChatController.java
    │   │   │       │   ├── ItemController.java
    │   │   │       │   ├── KopisController.java
    │   │   │       │   ├── MainController.java
    │   │   │       │   ├── MemberController.java
    │   │   │       │   ├── PaymentController.java
    │   │   │       │   └── TicketingController.java
    │   │   │       ├── 📂dto
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
    │   │   │       ├── 📂entity
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
    │   │   │       ├── 📂exception
    │   │   │       │   └── OutOfStockException.java
    │   │   │       ├── 📂repository
    │   │   │       │   ├── BannerImgRepository.java
    │   │   │       │   ├── BannersRepository.java
    │   │   │       │   ├── ChatMessageRepository.java
    │   │   │       │   ├── ChatRoomRepository.java
    │   │   │       │   ├── ItemCrawlRepository.java
    │   │   │       │   ├── ItemCrawlRepositoryCustom.java
    │   │   │       │   ├── ItemCrawlRepositoryImpl.java
    │   │   │       │   ├── ItemImgRepository.java
    │   │   │       │   ├── ItemRepository.java
    │   │   │       │   ├── ItemRepositoryCustom.java
    │   │   │       │   ├── ItemRepositoryImpl.java
    │   │   │       │   ├── MemberRepository.java
    │   │   │       │   ├── MemberRepositoryCustom.java
    │   │   │       │   ├── MemberRepositoryImpl.java
    │   │   │       │   └── PaymentRepository.java
    │   │   │       └── 📂service
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
    │   │   └── 📂resources
    │   │       ├── 📂static
    │   │       │   ├── 📂css
    │   │       │   │   └── layout.css
    │   │       │   ├── 📂img
    │   │       │   │   ├── bannersample.jpg
    │   │       │   │   ├── correction_btn.png
    │   │       │   │   ├── delete_btn.png
    │   │       │   │   └── ezticket.png
    │   │       │   ├── google-logo.png
    │   │       │   ├── kakao-logo.png
    │   │       │   └── naver-logo.png
    │   │       ├── 📂templates
    │   │       │   ├── 📂banner
    │   │       │   │   ├── bannerForm.html
    │   │       │   │   └── bannerList.html
    │   │       │   ├── 📂chat
    │   │       │   │   ├── chatbot.html
    │   │       │   │   ├── chating.html
    │   │       │   │   └── chatlist.html
    │   │       │   ├── 📂fragments
    │   │       │   │   ├── footer.html
    │   │       │   │   └── header.html
    │   │       │   ├── 📂item
    │   │       │   │   ├── crawlDetail.html
    │   │       │   │   ├── itemDetail.html
    │   │       │   │   ├── itemForm.html
    │   │       │   │   └── itemList.html
    │   │       │   ├── 📂layouts
    │   │       │   │   └── layout.html
    │   │       │   ├── 📂members
    │   │       │   │   ├── find-email.html
    │   │       │   │   ├── find-password.html
    │   │       │   │   ├── memberForm.html
    │   │       │   │   ├── memberList.html
    │   │       │   │   ├── memberLoginForm.html
    │   │       │   │   ├── memberMyPage.html
    │   │       │   │   └── reset-password.html
    │   │       │   ├── 📂payment
    │   │       │   │   ├── failure.html
    │   │       │   │   ├── orderList.html
    │   │       │   │   └── success.html
    │   │       │   └── main.html
    │   │       ├── application.properties
    │   │       └── application-oauth.properties
    └── pom.xml
```
</details>

<br>
<br>

## 🐞 트러블 슈팅

<details><summary>🎯 가격 데이터 파싱 및 버튼 UI 출력</summary> <br>

##


## 📌 문제 설명
공연 등록 시 "VIP석 110,000원, R석 90,000원"과 같은 문자열 형태의 가격 데이터를 사용자가 직관적으로 선택할 수 있도록 버튼 형태로 출력해야 했습니다.
하지만 이 문자열을 프론트에서 바로 활용하기에는 가공이 어려웠고, 좌석 유형과 가격 정보를 구조화된 형태로 분리할 필요가 있었습니다.

<br>

## 🔍 문제 발생 경과
처음에는 단순히 문자열을 출력하거나 split()으로 처리하려 했지만,

1. 좌석 타입과 가격을 명확히 구분하기 어려움

2. 문자열 파싱 오류 발생 시 프론트 출력 실패

3. 좌석 유형이 추가될 경우 확장성이 떨어지는 문제 발생

이로 인해 파싱 로직은 백엔드에서 처리하고,
프론트에는 가공된 데이터를 전달하는 방식으로 구조를 전환하게 되었습니다.

<br>


## 🔧 해결 방안 및 코드 설명
🔹 Java – 가격 문자열 파싱 함수 작성
문자열을 서버에서 파싱하여 좌석 타입과 가격을 Map<String, String> 형태로 반환

```
public List<Map<String, String>> parsePriceData(String rawPriceData) {
    List<Map<String, String>> priceList = new ArrayList<>();
    if (rawPriceData == null || rawPriceData.isBlank()) {
        return priceList;
    }
    String[] priceEntries = rawPriceData.split(", ");

    for (String entry : priceEntries) {
        String[] parts = entry.split(" ");
        if (parts.length == 2) {
            Map<String, String> priceMap = new HashMap<>();
            priceMap.put("seatType", parts[0]);
            priceMap.put("price", parts[1]);
            priceList.add(priceMap);
        }
    }
    return priceList;
}
```
🔹 Thymeleaf – 버튼 UI 렌더링
```
<div class="price-button-container">
    <div th:each="entry : ${priceOptions}">
        <button class="price-button"
                th:text="${entry['seatType']} + ' ' + ${entry['price']}"
                th:data-price="${entry['price']}"
                onclick="selectPrice(this)">
        </button>
    </div>
</div>
<p><strong>선택한 가격: </strong><span id="selectedPrice">없음</span></p>
```
🔹 JavaScript – 선택된 가격 표시
```
function selectPrice(button) {
    document.querySelectorAll('.price-button').forEach(btn => btn.classList.remove('selected'));

    button.classList.add('selected');

    let priceText = button.getAttribute("data-price").replace(/,/g, "").replace("원", "");
    let price = parseInt(priceText, 10);

    document.getElementById('selectedPrice').innerText = price.toLocaleString() + '원';
}
```
<br>

## ✅ 결론
- 복잡하게 구성된 가격 문자열을 구조화된 데이터로 변환함으로써, 프론트엔드에서의 UI 구현이 훨씬 단순해졌습니다.

- 좌석 유형 추가나 가격 구조 변경에도 유연하게 대응할 수 있어 확장성과 유지보수성이 높아졌습니다.

- 단순한 파싱 문제가 아니라, 백엔드-프론트 간 데이터 전달 설계의 중요성을 경험할 수 있었던 문제였습니다.


##
</details>


<br>
<br>

## 💡 구현 과정과 인사이트
핵심 기능 중 하나로 Redis 기반의 대기 시스템을 구현하여, 사용자 트래픽이 몰릴 때 서버 부하를 줄이고 선착순으로 티켓을 예매할 수 있도록 설계했습니다. 하지만 실제 테스트 환경을 구축하는 과정은 쉽지 않았습니다.
동시 접속자가 많아질 때의 성능을 시뮬레이션하고자 했지만, 현실적으로 대량의 요청을 생성하고 관리하는 데 한계가 있었고, 이를 통해 부하 테스트 환경을 체계적으로 마련하는 것의 중요성을 느낄 수 있었습니다.

또한 티켓 가격 입력 및 UI 구성에서도 많은 고민이 있었습니다. 공연마다 좌석 유형(S석, A석, 스탠딩 등)과 가격이 다양하고 복수의 가격이 존재할 수 있어, 효율적인 입력 및 출력 방식의 설계가 필요했습니다. 특히, 가격을 버튼 형태로 제공할 때 사용자가 직관적으로 선택할 수 있는 UI 설계에 어려움이 있었고, 반복적인 테스트를 통해 개선해 나갔습니다.

이번 프로젝트를 통해 Redis를 활용한 대기열 시스템 구축, 가격 데이터를 동적으로 처리하는 방식, UI와의 연결 설계, 그리고 성능 테스트의 필요성까지 다양한 기술적 경험을 할 수 있었습니다.
무엇보다 단순한 기능 구현을 넘어서 실제 운영 환경을 고려한 설계와 사용자 중심의 서비스 제공이 얼마나 중요한지 다시 한번 깨닫는 계기가 되었습니다.

앞으로는 더 다양한 케이스를 고려한 유연한 구조와 개선된 UI를 적용하며, 실사용에 가까운 환경을 시뮬레이션해보는 연습도 함께 이어나가고자 합니다.

<br>
<br>

## 🔗 배포 주소

> 현재 해당 프로젝트는 로컬 개발 기반이며, 추후 AWS 기반으로 배포 예정입니다.
