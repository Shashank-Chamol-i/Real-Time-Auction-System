package com.example.realTimeAuctionSystem.service;

import com.example.realTimeAuctionSystem.dto.AuthenticationRequest;
import com.example.realTimeAuctionSystem.dto.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailService customUserDetailService;


    public AuthenticationResponse authenticate(AuthenticationRequest request){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);
        return new  AuthenticationResponse(token);
    }

}
