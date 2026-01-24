package com.example.realTimeAuctionSystem.controller;

import com.example.realTimeAuctionSystem.dto.DebitForAuctionRequest;
import com.example.realTimeAuctionSystem.dto.DebitForAuctionResponse;
import com.example.realTimeAuctionSystem.dto.AuctionResultRequest;
import com.example.realTimeAuctionSystem.dto.AuctionResultResponse;
import com.example.realTimeAuctionSystem.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auction")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @PostMapping("/debit")
    public ResponseEntity<DebitForAuctionResponse> debitFoAuction(@RequestBody DebitForAuctionRequest request){
        DebitForAuctionResponse response =  walletService.debitForAuction(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);

    }
    // Get the list of the Loser transactions in Auction

    @GetMapping("/loser/list")
    public ResponseEntity<List<AuctionResultResponse>> getLoserResponse(@RequestBody AuctionResultRequest auctionResultRequest){
       List<AuctionResultResponse> response =  walletService.getAllLoser(auctionResultRequest);
       return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // Get the list of the Winner transaction in Auction
    @GetMapping("/winner/list")
    public ResponseEntity<List<AuctionResultResponse>> getWinnerResponse(@RequestBody AuctionResultRequest auctionResultRequest){
        List<AuctionResultResponse> responses = walletService.getWinner(auctionResultRequest);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    // Refund of Loser Money
    @PostMapping("/refund")
    public ResponseEntity<String> refundMoneyOfLooser(@RequestBody AuctionResultRequest auctionResultRequest){
        String response = walletService.refundMoneyOfLooser(auctionResultRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // Settlement and refund of the winner money
    @PostMapping("/settle")
    public ResponseEntity<String> settleWinner(@RequestBody AuctionResultRequest auctionResultRequest){
        String response = walletService.refundAndSettlementOfWinner(auctionResultRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
