package com.awbd.lab7.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.awbd.lab7.services.security.JpaUserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("mysql")
public class SecurityJpaConfig {

    private final JpaUserDetailsService userDetailsService;

    public SecurityJpaConfig(JpaUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeRequests(auth -> auth
                                .requestMatchers("/product/form").hasRole("ADMIN")
                                .requestMatchers("/", "/webjars/**", "/login", "/resources/**").permitAll()
                                .requestMatchers("/product/*").authenticated()//.hasAnyRole("ADMIN", "GUEST")
                                .requestMatchers("/categories/*").hasAnyRole("ADMIN", "GUEST")
                                .anyRequest().authenticated()
                )
                .userDetailsService(userDetailsService)
                .formLogin(formLogin ->
                        formLogin
                                .loginPage("/login")
                                .permitAll()
                                .loginProcessingUrl("/perform_login")
                )
                .exceptionHandling(ex -> ex.accessDeniedPage("/access_denied"))
                .build();
    }

}