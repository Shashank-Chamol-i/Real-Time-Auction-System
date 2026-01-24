package com.example.realTimeAuctionSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoserRequest {
    private String auctionId;
    private String  winnerId;
}
