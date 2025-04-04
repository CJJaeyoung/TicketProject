package com.ticket.config;

import com.ticket.constant.Role;
import com.ticket.entity.Member;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

@Getter
@Setter
@ToString
public class OAuthAttributes {
   private Map<String, Object> attributes;
   private String nameAttributeKey;
   private String name;
   private String email;
   private String picture;
   private String provider;
   
   public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name,
                          String email, String picture) {
      this.attributes = attributes;
      this.nameAttributeKey = nameAttributeKey;
      this.name = name;
      this.email = email;
      this.picture = picture;
   }
   
   public OAuthAttributes() {
   }
   
   public static OAuthAttributes of(String registrationId, String userNameAttributeName,
                                    Map<String, Object> attributes) {
      if (registrationId.equals("kakao")) {
         return ofKakao(userNameAttributeName, attributes);
      }
      if (registrationId.equals("naver")) {
         return ofNaver(userNameAttributeName, attributes);
      }
      return ofGoogle(userNameAttributeName, attributes);
   }
   
   private static OAuthAttributes ofNaver(String userNameAttributeName,
                                          Map<String, Object> attributes) {
      Map<String, Object> response = (Map<String, Object>) attributes.get("response");
      
      return new OAuthAttributes(attributes,
            userNameAttributeName,
            (String) response.get("name"),
            (String) response.get("email"),
            (String) response.get("profile_image"));
   }
   
   private static OAuthAttributes ofKakao(String userNameAttributeName,
                                          Map<String, Object> attributes) {
      Map<String, Object> kakao_account = (Map<String, Object>) attributes.get("kakao_account");
      Map<String, Object> profile = (Map<String, Object>) kakao_account.get("profile");
      
      return new OAuthAttributes(attributes,
            userNameAttributeName,
            (String) profile.get("nickname"),
            (String) kakao_account.get("email"),
            (String) profile.get("profile_image_url"));
   }
   
   private static OAuthAttributes ofGoogle(String userNameAttributeName,
                                           Map<String, Object> attributes) {
      return new OAuthAttributes(attributes,
            userNameAttributeName,
            (String) attributes.get("name"),
            (String) attributes.get("email"),
            (String) attributes.get("picture"));
   }

   public Member toEntity(PasswordEncoder passwordEncoder) {
      return Member.builder()
              .name(name)
              .email(email)
              .picture(picture)
              .password(passwordEncoder.encode("OAUTH_USER"))
              .role(Role.USER) // 기본 권한 설정
              .build();
   }
}
