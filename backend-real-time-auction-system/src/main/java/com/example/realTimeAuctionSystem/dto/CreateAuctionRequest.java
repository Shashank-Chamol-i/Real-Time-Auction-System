package com.example.realTimeAuctionSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAuctionRequest {

    private String itemName;
    private BigDecimal baseAmount;
    private Instant startTime;
    private Instant endTime;
}
