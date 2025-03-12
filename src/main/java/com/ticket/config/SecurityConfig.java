package com.ticket.config;

import com.ticket.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
   
   @Autowired
   MemberService memberService;
   
   @Autowired
   private CustomOAuth2UserService customOAuth2UserService;
   
   @Bean
   public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
      http.csrf(csrf -> csrf
                  .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // CSRF 토큰을 쿠키에 저장
                  .ignoringRequestMatchers(
                        "/ws/**", "/topic/**", "/app/**", "/members/login",
                        "/create-room", "/chat/**", "/admin/**", "/check-email", "/api/chatbot/**",
                        "/members/find-email", "/members/find-password", "/members/reset-password",
                        "/payment/complete", "/ticket/join-waiting", "/ticket/request-reservation"
                  )
            )
            .authorizeHttpRequests(auth -> auth
                  .requestMatchers("/css/**", "/js/**", "/img/**", "/images/**", "favicon.ico", "/error", "/check-email",
                        "/google-logo.png", "/kakao-logo.png", "/naver-logo.png", "/ws/**", "/topic/**", "/app/**").permitAll()
                  .requestMatchers("/", "/members/**", "/find-email", "/members/find-password", "/subscribe/**", "/images/**", "/mail/**", "/chat/**",
                        "/api/chatbot/**", "/movies/**", "/banner/**", "/itemImg/**", "/item/**", "/ticket/**").permitAll()
                  .requestMatchers("/", "/performance/detail/**", "/api/kopis/**", "/payment/**").permitAll() // 🔹 인증 없이 접근 가능하게 설정
                  .requestMatchers("/admin/**").hasRole("ADMIN")
                  .anyRequest().authenticated()
            )
            .formLogin(formLogin -> formLogin
                  .loginPage("/members/login")
                  .defaultSuccessUrl("/")
                  .usernameParameter("email")
                  .failureUrl("/members/login/error")
            )
            .logout(logout -> logout
                  .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout"))
                  .logoutSuccessUrl("/")
                  .invalidateHttpSession(true)
                  .deleteCookies("JSESSIONID")
            )
            .oauth2Login(oauthLogin -> oauthLogin
                  .defaultSuccessUrl("/")
                  .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                        .userService(customOAuth2UserService))
            );
      
      http.exceptionHandling(exception -> exception
            .authenticationEntryPoint(new CustomAuthenticationEntryPoint()));
      
      return http.build();
   }
   
   
   @Bean
   public static PasswordEncoder passwordEncoder() {
      return PasswordEncoderFactories.createDelegatingPasswordEncoder();
   }
   
   @Autowired
   public void configure(AuthenticationManagerBuilder auth) throws Exception {
      auth.userDetailsService(memberService).passwordEncoder(passwordEncoder());
   }
}
