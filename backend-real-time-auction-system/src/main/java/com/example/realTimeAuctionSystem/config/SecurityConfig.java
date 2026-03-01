package com.example.realTimeAuctionSystem.config;

import com.example.realTimeAuctionSystem.security.JwtAuthenticationFilter;
import com.example.realTimeAuctionSystem.service.CustomUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomUserDetailService customUserDetails;
    private final JwtAuthenticationFilter authenticationFilter;


@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws  Exception{
    httpSecurity
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth->auth
                    .requestMatchers("/api/auth/**").permitAll().anyRequest().authenticated())
            .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
    return httpSecurity.build();
}

@Bean
public AuthenticationProvider authenticationProvider(){
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customUserDetails);
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
}
@Bean
public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
return configuration.getAuthenticationManager();
}
@Bean
public PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
}

}
