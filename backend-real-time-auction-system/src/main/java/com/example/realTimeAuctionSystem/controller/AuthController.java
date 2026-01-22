package com.example.realTimeAuctionSystem.controller;

import com.example.realTimeAuctionSystem.dto.RegisterRequest;
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

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request){
       String response =  userService.register(request);
       return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


}
