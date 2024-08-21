package com.example.majorLink.global.config;

import com.example.majorLink.global.jwt.JwtAuthenticationFilter;
import com.example.majorLink.global.jwt.JwtService;
import com.example.majorLink.global.oauth2.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler OAuth2AuthenticationFailureHandler;
    private final OAuthLoginService oAuthLoginService;
    private final JwtService jwtService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(AbstractHttpConfigurer::disable) // http form login 비활성화
                .csrf(AbstractHttpConfigurer::disable) // csrf 필터 비활성화 -> cookies 사용하지 않으므로 위험 없음
                .cors(cors -> cors
                        .configurationSource(request -> {
                            CorsConfiguration config = new CorsConfiguration();
                            config.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:3001", "http://127.0.0.1:8080"));
                            config.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PUT"));
                            config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Auth-Token")); // 허용할 헤더 설정
                            return config;
                        })
                )
                .formLogin(AbstractHttpConfigurer::disable) // basic login 비활성화
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // session 사용 X
                .addFilterBefore(new JwtAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(configure ->
                        configure
                                .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                                        .userService(oAuthLoginService))
                                .authorizationEndpoint(authorizationEndpointConfig -> authorizationEndpointConfig // auth 로그인 페이지 return
                                        .baseUri("/oauth/authorize"))
                                .successHandler(oAuth2AuthenticationSuccessHandler)
                                .failureHandler(OAuth2AuthenticationFailureHandler)
                );

        return http.build();
    }
}
