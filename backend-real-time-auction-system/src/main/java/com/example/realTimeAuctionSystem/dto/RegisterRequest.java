package com.example.realTimeAuctionSystem.dto;

import com.example.realTimeAuctionSystem.model.Role;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private Role role = Role.BIDDER;
    private BigDecimal walletBalance = BigDecimal.ZERO;
}
