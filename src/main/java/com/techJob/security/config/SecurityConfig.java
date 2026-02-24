package com.techJob.security.config;

import com.techJob.exception.auth.JwtAccessDeniedHandler;
import com.techJob.exception.auth.JwtAuthEntryPoint;
import com.techJob.security.constants.SecurityConstants;
import com.techJob.security.jwt.JwtAuthenticationFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final WebCsrfFilter webCsrfFilter;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          WebCsrfFilter webCsrfFilter,
                          JwtAuthEntryPoint jwtAuthEntryPoint,
                          JwtAccessDeniedHandler jwtAccessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.webCsrfFilter = webCsrfFilter;
        this.jwtAuthEntryPoint = jwtAuthEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // ========================
            // CSRF & Session Management
            // ========================
            .csrf(csrf -> csrf.disable()) // CSRF handled manually for Web
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // ========================
            // Exception Handling
            // ========================
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(jwtAuthEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
            )

            // ========================
            // Authorization
            // ========================
            .authorizeHttpRequests(auth -> auth
                // Swagger / OpenAPI
                .requestMatchers(SecurityConstants.SWAGGER_PATH + "**", SecurityConstants.API_DOCS_PATH + "**").permitAll()

                .requestMatchers(SecurityConstants.AUTH_API_PATH+"logout").authenticated()
                // API Authentication
                .requestMatchers(SecurityConstants.AUTH_API_PATH + "**").permitAll()
                
                // Web Authentication
                .requestMatchers(SecurityConstants.LOGIN_PATH, SecurityConstants.SIGNUP_PATH).permitAll()
                
                // أي شيء آخر يحتاج مصادقة
                .anyRequest().authenticated()
           );

        // ========================
        // Filters
        // ========================
        // JWT Filter قبل UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // CSRF Filter فقط للويب
        http.addFilterBefore(webCsrfFilter, JwtAuthenticationFilter.class);

        return http.build();
    }

    // ========================
    // AuthenticationManager Bean
    // ========================
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // ========================
    // Password Encoder Bean
    // ========================
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

// Security improvement: All paths, header names, and cookie names are centralized via SecurityConstants for maintainability and error reduction.