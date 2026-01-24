package com.example.realTimeAuctionSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DebitForAuctionResponse {
    private String id;
    private BigDecimal amount;
    private Instant createdAt;
    private String transactionStatus;
    private String transactionType;
    private String userId;

}
