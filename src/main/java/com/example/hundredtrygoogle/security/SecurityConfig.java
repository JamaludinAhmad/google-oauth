package com.example.hundredtrygoogle.security;

import com.example.hundredtrygoogle.filters.JwtAuthenticationFilter;
import com.example.hundredtrygoogle.handlers.CustomAuthenticationEntryPoint;
import com.example.hundredtrygoogle.handlers.CustomFailureHandler;
import com.example.hundredtrygoogle.services.CustomOAuth2Service;
import com.example.hundredtrygoogle.services.CustomOidcService;
import com.example.hundredtrygoogle.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

    @Autowired
    private CustomOidcService customOidcService;

    @Autowired
    private CustomFailureHandler failureHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomOAuth2Service customOAuth2Service, JwtAuthenticationFilter jwtAuthenticationFilter, CustomAuthenticationEntryPoint customAuthenticationEntryPoint) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/register", "/auth/login", "/auth/refresh_token").permitAll()
                        .anyRequest().authenticated())
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/auth/register", "/auth/login", "/auth/refresh_token", "/users/updateMe"))
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userinfo -> userinfo
                                .oidcUserService(customOidcService)
                                .userService(customOAuth2Service))
                        .failureHandler(failureHandler))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();

        authenticationProvider.setUserDetailsService(userService);
        authenticationProvider.setPasswordEncoder(bCryptPasswordEncoder);

        return new ProviderManager(authenticationProvider);
    }
}
