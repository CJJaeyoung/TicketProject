package com.ticket.config;


import com.ticket.dto.SessionUser;
import com.ticket.entity.Member;
import com.ticket.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    
    private final HttpSession httpSession;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2UserService oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(oAuth2UserRequest);
        
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        
        String userNameAttributeName = oAuth2UserRequest.getClientRegistration().getProviderDetails()
              .getUserInfoEndpoint().getUserNameAttributeName();
        
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName
              , oAuth2User.getAttributes());
        
        Member member = saveOrUpdate(attributes);
        httpSession.setAttribute("user", new SessionUser(member));
        
        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
              , attributes.getAttributes()
              , attributes.getNameAttributeKey()
        );
    }
    
    
    private Member saveOrUpdate(OAuthAttributes attributes) {
        Member member = memberRepository.findByEmail(attributes.getEmail())
              .map(entity -> {
                  entity.update(attributes.getName(), attributes.getPicture());
                  return entity;
              })
              .orElse(attributes.toEntity(passwordEncoder));
        
        // 비밀번호가 없는 경우 기본값 설정
        if (member.getPassword() == null || member.getPassword().isEmpty()) {
            member.setPassword("OAUTH_USER");
        }
        
        return memberRepository.save(member);
    }
}