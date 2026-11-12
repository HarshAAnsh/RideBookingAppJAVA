package com.ridebooking.ride_booking_backend.config;

import com.ridebooking.ride_booking_backend.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

private final JwtAuthenticationFilter jwtAuthenticationFilter;

public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
}

@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http)
        throws Exception {

    http
        .csrf(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)

        // JWT authentication does not use HTTP sessions.
        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )

        // Return 401 instead of redirecting unauthenticated requests.
        .exceptionHandling(exception ->
            exception.authenticationEntryPoint((request, response, authException) -> {
                response.setStatus(401);
                response.setContentType("application/json");
                response.getWriter().write(
                    "{\"error\":\"Unauthorized\",\"message\":\"Authentication is required\"}"
                );
            })
        )

        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers(HttpMethod.GET, "/api/health").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
            .anyRequest().authenticated()
        )

        // Validate the JWT before Spring's username/password filter.
        .addFilterBefore(
            jwtAuthenticationFilter,
            UsernamePasswordAuthenticationFilter.class
        );

    return http.build();
}

}