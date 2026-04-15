package com.polyparrot.notificationservice.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationSseFilter extends OncePerRequestFilter {

 private final JwtService jwtService;

 @Override
 protected void doFilterInternal(HttpServletRequest request,
         HttpServletResponse response,
         FilterChain chain) throws ServletException, IOException {

     String path = request.getServletPath();
     if (path.contains("/subscribe")) {
         String token = request.getParameter("token");
         if (token != null) {
             String email = jwtService.extractEmail(token);
             if (email != null) {
                 AuthenticatedUser authUser = new AuthenticatedUser(
                     jwtService.extractUserId(token),
                     email,
                     jwtService.extractRole(token)
                 );
                 UsernamePasswordAuthenticationToken authToken =
                     new UsernamePasswordAuthenticationToken(
                         authUser, null,
                         List.of(new SimpleGrantedAuthority("ROLE_" + authUser.getRole()))
                     );
                 SecurityContextHolder.getContext().setAuthentication(authToken);
             }
         }
     }
     chain.doFilter(request, response);
 }
}