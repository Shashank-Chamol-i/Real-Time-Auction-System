package com.example.realTimeAuctionSystem.security;

import com.example.realTimeAuctionSystem.service.CustomUserDetailService;
import com.example.realTimeAuctionSystem.service.CustomUserDetails;
import com.example.realTimeAuctionSystem.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final CustomUserDetailService customUserDetailService;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            final String authHeader = request.getHeader("Authorization");

            if(authHeader == null || !authHeader.startsWith("Bearer ")){
                filterChain.doFilter(request,response);
                return;
            }
            final String jwt = authHeader.substring(7);
            final String userId = jwtService.extractUserId(jwt);

            if(userId !=null && SecurityContextHolder.getContext().getAuthentication() == null){
                UserDetails userDetails = customUserDetailService.loadUserById(userId);

                if(jwtService.isTokenValid(jwt, (CustomUserDetails) userDetails)){
                    UsernamePasswordAuthenticationToken authenticationToken =new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());

                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    filterChain.doFilter(request,response);
                }

            }
    }
}
