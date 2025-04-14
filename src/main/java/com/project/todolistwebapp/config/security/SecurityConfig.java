package com.project.todolistwebapp.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    @Bean
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                .httpBasic(withDefaults())
                .exceptionHandling(customizer -> customizer
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.FORBIDDEN))
                        .accessDeniedPage("/access-denied"))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/img/**", "/", "/home", "/login",
                                "/access-denied").permitAll()
                        .requestMatchers("/users/**").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers("/**").hasAuthority("ADMIN")
                        .anyRequest().authenticated());
        return http.build();
    }
}
