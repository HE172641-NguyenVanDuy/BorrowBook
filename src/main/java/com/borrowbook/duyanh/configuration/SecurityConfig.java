package com.borrowbook.duyanh.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
//import org.springframework.security.oauth2.jwt.JwtDecoder;
//import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
//import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
//import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

//@Configuration
//@EnableMethodSecurity
//@EnableWebSecurity
//public class SecurityConfig {
//
//    private final String[] PUBLIC_ENDPOINTS = { "/auth/token", "/auth/introspect"};
//
//    @Value("${jwt.secret}")
//    private String SIGNER_KEY;
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(request ->
//                        request.requestMatchers(HttpMethod.POST,PUBLIC_ENDPOINTS).permitAll()
//                                //.requestMatchers(HttpMethod.GET,"/users").hasRole("ADMIN")
//                                .anyRequest().authenticated()
//                );
//        http.oauth2ResourceServer(
//                oauth2 -> oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder())
//                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
//                        )
//                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
//        );
//        http.csrf(configurer -> configurer.disable());
//        return http.build();
//    }
//
//    @Bean
//    JwtDecoder jwtDecoder() {
//        SecretKey secretKeySpec = new SecretKeySpec(SIGNER_KEY.getBytes(),"HS512");
//        return NimbusJwtDecoder.withSecretKey(secretKeySpec)
//                .macAlgorithm(MacAlgorithm.HS512).build();
//    }
//
//    @Bean
//    JwtAuthenticationConverter jwtAuthenticationConverter() {
//        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
//        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
//        JwtAuthenticationConverter jwt = new JwtAuthenticationConverter();
//        jwt.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
//        return jwt;
//    }
//
//    @Bean
//    PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder(12);
//    }
//}