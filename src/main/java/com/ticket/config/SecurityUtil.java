package com.ticket.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

public class SecurityUtil {
   public static String getCurrentUserEmail() {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      
      if (authentication instanceof OAuth2AuthenticationToken) {
         OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
         OAuth2User oAuth2User = oauthToken.getPrincipal();
         String registrationId = oauthToken.getAuthorizedClientRegistrationId();
         
         if ("google".equals(registrationId)) {
            return oAuth2User.getAttribute("email");
         } else if ("kakao".equals(registrationId)) {
            Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
            return (String) kakaoAccount.get("email");
         } else if ("naver".equals(registrationId)) {
            Map<String, Object> response = oAuth2User.getAttribute("response");
            return (String) response.get("email");
         }
      } else if (authentication instanceof UsernamePasswordAuthenticationToken) {
         return authentication.getName();
      }
      
      return null;
   }
}
