package com.example.realTimeAuctionSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoserResponse {
    private String userId;
    private String auctionId;
    private BigDecimal amount;

}
