package com.example.realTimeAuctionSystem.controller;

import com.example.realTimeAuctionSystem.dto.AuctionResultRequest;
import com.example.realTimeAuctionSystem.dto.UserBidRequest;
import com.example.realTimeAuctionSystem.service.BiddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bid")
@RequiredArgsConstructor
public class BidController {
 private final BiddingService biddingService;

    @PostMapping("/place")
    public ResponseEntity<?> placeBid(@RequestBody UserBidRequest request){
        String response = biddingService.placeBid(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    
}
