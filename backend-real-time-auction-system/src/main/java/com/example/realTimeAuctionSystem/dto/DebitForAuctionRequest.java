package com.example.realTimeAuctionSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DebitForAuctionRequest {
    private String userId;
    private String auctionId;
    private BigDecimal amount;
}
