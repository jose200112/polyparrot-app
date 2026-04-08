package com.polyparrot.chatservice.security;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.RequiredArgsConstructor;

//com.polyparrot.chatservice.security.JwtAuthenticationFilter
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

 private final JwtService jwtService;

 @Override
 protected void doFilterInternal(
         HttpServletRequest request,
         HttpServletResponse response,
         FilterChain filterChain) throws ServletException, IOException {

     final String authHeader = request.getHeader("Authorization");
     if (authHeader == null || !authHeader.startsWith("Bearer ")) {
         filterChain.doFilter(request, response);
         return;
     }

     String token = authHeader.substring(7);

     Long userId = jwtService.extractUserId(token);
     String email = jwtService.extractEmail(token);
     String role  = jwtService.extractRole(token);

     if (email != null) {
         AuthenticatedUser authUser = new AuthenticatedUser(userId, email, role);

         UsernamePasswordAuthenticationToken authToken =
             new UsernamePasswordAuthenticationToken(
                 authUser,   
                 null,
                 List.of(new SimpleGrantedAuthority("ROLE_" + role))
             );

         SecurityContextHolder.getContext().setAuthentication(authToken);
     }

     filterChain.doFilter(request, response);
 }
}