package com.example.realTimeAuctionSystem.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBidRequest {
    private String auctionId;
    private String userId;
    private BigDecimal amount;
    private String idempotencyKey;

}
