package org.example.mymedsbackend.security;

import org.example.mymedsbackend.user.UserRepo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password4j.Argon2Password4jPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final SecurityLogService securityLog;

    public SecurityConfig(UserDetailsService userDetailsService, SecurityLogService securityLog) {
        this.userDetailsService = userDetailsService;
        this.securityLog = securityLog;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserRepo userRepo) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/me").authenticated()
                        .requestMatchers("/api/users", "/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/medications/my").hasRole("PATIENT")
                        .requestMatchers(HttpMethod.GET, "/api/medications/patients/**").hasRole("DOCTOR")
                        .requestMatchers(HttpMethod.POST, "/api/medications/**").hasRole("DOCTOR")
                        .requestMatchers(HttpMethod.PUT, "/api/medications/**").hasRole("DOCTOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/medications/**").hasRole("DOCTOR")
                        .requestMatchers("/api/alerts/**").hasAnyRole("DOCTOR", "PATIENT")
                        .requestMatchers(HttpMethod.GET, "/api/logs/all").hasAnyRole("DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/logs").hasAnyRole("PATIENT", "DOCTOR", "ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/doctor/**").hasRole("DOCTOR")

                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex.accessDeniedHandler((request, response, denied) -> {
                    securityLog.recordAsCurrentUser(
                            "Unauthorized access attempt",
                            "Access",
                            "Blocked",
                            request.getMethod() + " " + request.getRequestURI());

                    response.sendError(HttpStatus.FORBIDDEN.value(), "Unauthorized");
                }))
                .httpBasic(basic -> basic
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))

                .addFilterBefore(new DeviceKeyAuthenticationFilter(userRepo), AbstractPreAuthenticatedProcessingFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("http://localhost:*", "http://127.0.0.1:*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}