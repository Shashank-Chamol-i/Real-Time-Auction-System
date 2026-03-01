package com.example.realTimeAuctionSystem.controller;

import com.example.realTimeAuctionSystem.dto.AuthenticationRequest;
import com.example.realTimeAuctionSystem.dto.AuthenticationResponse;
import com.example.realTimeAuctionSystem.dto.RegisterRequest;
import com.example.realTimeAuctionSystem.service.AuthenticationService;
import com.example.realTimeAuctionSystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request){
       String response =  userService.register(request);
       return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request){
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }



}
