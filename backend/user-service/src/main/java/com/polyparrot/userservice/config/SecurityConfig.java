package com.polyparrot.userservice.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy; 
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.polyparrot.userservice.security.JwtAuthenticationFilter;
import com.polyparrot.userservice.security.JwtService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtService jwtService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    	http
    	.cors(cors -> {})
        .csrf(csrf -> csrf.disable())
        
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )

        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/auth/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/v3/api-docs/**"
            ).permitAll()
            
            .requestMatchers(HttpMethod.GET, "/users/*").permitAll()
            .anyRequest().authenticated()
        )

        .httpBasic(httpBasic -> httpBasic.disable()) 
        .addFilterBefore(
            new JwtAuthenticationFilter(jwtService),
            UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(List.of("http://localhost:4200"));
		config.setAllowedMethods(List.of("*"));
		config.setAllowedHeaders(List.of("*"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}
}