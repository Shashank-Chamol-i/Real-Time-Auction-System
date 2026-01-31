package com.example.realTimeAuctionSystem.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class BidPlacedDomainEvent {
    private final String auctionEventId;
}
