package com.polyparrot.teacherservice.config;

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

import com.polyparrot.teacherservice.security.JwtAuthenticationFilter;
import com.polyparrot.teacherservice.security.JwtService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtService jwtService;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
        	.cors(cors -> {})
            .csrf(csrf -> csrf.disable())

            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authorizeHttpRequests(auth -> auth
            	    .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
            	    .requestMatchers(HttpMethod.GET, "/teachers/search").permitAll()
            	    .requestMatchers(HttpMethod.GET, "/teachers/bookings/upcoming").authenticated() // 👈 antes de {id}
            	    .requestMatchers(HttpMethod.GET, "/teachers/{id}").permitAll()
            	    .requestMatchers(HttpMethod.GET, "/teachers").permitAll()
            	    .requestMatchers(HttpMethod.GET, "/languages/**").permitAll()
            	    .requestMatchers(HttpMethod.GET, "/availability/**").permitAll()
            	    .requestMatchers(HttpMethod.DELETE, "/availability/**").permitAll()
            	    .requestMatchers(HttpMethod.POST, "/teachers").authenticated()
            	    .requestMatchers(HttpMethod.POST, "/availability").authenticated()
            	    .requestMatchers(HttpMethod.GET, "/teachers/me/bookings/upcoming").authenticated()
            	    .requestMatchers(HttpMethod.GET, "/teachers/{id}/summary").permitAll()
            	    .anyRequest().authenticated()
            	)

            .addFilterBefore(
                new JwtAuthenticationFilter(jwtService),
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
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