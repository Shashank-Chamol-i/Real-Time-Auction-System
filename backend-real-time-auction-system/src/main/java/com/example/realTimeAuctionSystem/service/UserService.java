package com.example.realTimeAuctionSystem.service;

import com.example.realTimeAuctionSystem.dto.RegisterRequest;
import com.example.realTimeAuctionSystem.exception.EmailAlreadyExist;
import com.example.realTimeAuctionSystem.exception.IllegalStateException;
import com.example.realTimeAuctionSystem.model.Role;
import com.example.realTimeAuctionSystem.model.Users;
import com.example.realTimeAuctionSystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public String register(RegisterRequest request){
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new EmailAlreadyExist("Email Already Exist :");
        }
        if(request.getRole()!= Role.BIDDER){
            throw new IllegalStateException("Public Users cannot be ADMIN");
        }

        Users user = Users.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(request.getPassword())
                .role(request.getRole())
                .walletBalance(request.getWalletBalance().add(BigDecimal.ZERO))
                .active(true)
                .build();

        Users savedUser = userRepository.save(user);

        return savedUser.getUsername()+": Successfully Register \n\nBidderId : "+savedUser.getId();
    }
}
