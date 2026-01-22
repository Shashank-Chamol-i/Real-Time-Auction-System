package com.example.realTimeAuctionSystem.controller;

import com.example.realTimeAuctionSystem.dto.CreateAuctionRequest;
import com.example.realTimeAuctionSystem.service.AuctionLifeCycleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auction")
@RequiredArgsConstructor
public class AuctionLifeCycleController {

    private final AuctionLifeCycleService auctionLifeCycleService;

    @PostMapping("/create")
    public ResponseEntity<String> createAuction(@RequestBody CreateAuctionRequest auctionRequest){
        String response =  auctionLifeCycleService.createAuction(auctionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/start/{auctionId}")
    public ResponseEntity<String> startAuction(@PathVariable String auctionId){
           String response =  auctionLifeCycleService.startAuction(auctionId);
           return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/close/{auctionId}")
    public ResponseEntity<String> closeAuction(@PathVariable String auctionId){
        String response = auctionLifeCycleService.closeAuction(auctionId);
        return  ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/settle/{auctionId}")
    public ResponseEntity<String> settleAuction(@PathVariable String auctionId){
        String response = auctionLifeCycleService.settleAuction(auctionId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
