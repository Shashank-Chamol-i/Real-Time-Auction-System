package com.example.realTimeAuctionSystem.service;

import com.example.realTimeAuctionSystem.component.AuctionWebSocketPublisher;
import com.example.realTimeAuctionSystem.dto.BidPlacedDomainEvent;
import com.example.realTimeAuctionSystem.dto.DebitForAuctionRequest;
import com.example.realTimeAuctionSystem.dto.UserBidRequest;
import com.example.realTimeAuctionSystem.exception.IllegalStateException;
import com.example.realTimeAuctionSystem.exception.InsufficientAmount;
import com.example.realTimeAuctionSystem.exception.NoSuchExist;
import com.example.realTimeAuctionSystem.model.*;
import com.example.realTimeAuctionSystem.repository.AuctionEventRepository;
import com.example.realTimeAuctionSystem.repository.AuctionRepository;
import com.example.realTimeAuctionSystem.repository.BidRepository;
import com.example.realTimeAuctionSystem.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BiddingService {
    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;
    private final WalletService walletService;
    private final UserRepository userRepository;
    private final AuctionEventRepository auctionEventRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ObjectMapper objectMapper;

    @Transactional
    public String placeBid(UserBidRequest userBidRequest){

        if(userBidRequest.getIdempotencyKey() == null){
            userBidRequest.setIdempotencyKey(String.valueOf(UUID.randomUUID()));
        }
        Bids existingBid = bidRepository.findByIdempotencyKey(userBidRequest.getIdempotencyKey());

        if(existingBid!=null){
            return "Bid Already Processed : "+existingBid.getAmount();
        }

        Users user = userRepository.findById(userBidRequest.getUserId()).orElseThrow(()-> new NoSuchExist("No such user Exist : "));


        Auctions auction = auctionRepository.findById(userBidRequest.getAuctionId())
                .orElseThrow(()->new NoSuchExist("No Such Auction Exist : "));

         final BigDecimal previousHighestBid = auction.getCurrentHighestBid()==null ? BigDecimal.ZERO : auction.getCurrentHighestBid();

        if(auction.getAuctionStatus()!= AuctionStatus.LIVE){
            throw new IllegalStateException("Auction is not currently Live : ");
        }

        if(userBidRequest.getAmount().compareTo(auction.getCurrentHighestBid())<=0){
                throw  new InsufficientAmount("Current Highest is higher than the user amount ");
        }
        DebitForAuctionRequest auctionRequest  = new DebitForAuctionRequest(
                userBidRequest.getUserId(),
                userBidRequest.getAuctionId(),
                userBidRequest.getAmount()
        );
        auction.setCurrentHighestBid(userBidRequest.getAmount());
        auction.setUser(user);

        try{
            auctionRepository.save(auction);
        }catch (OptimisticEntityLockException e){
            throw new ConcurrentModificationException("Bid lost due to concurrent Update :");
        }


        walletService.debitForAuction(auctionRequest);
        Bids bid = Bids.builder()
                .amount(auctionRequest.getAmount())
                .bidTime(Instant.now())
                .idempotencyKey(userBidRequest.getIdempotencyKey())
                .auction(auction)
                .user(user)
                .build();

        bidRepository.save(bid);


        Map<String,Object> payloadMap = new HashMap<>();
        payloadMap.put("bidId",bid.getId());
        payloadMap.put("bidderId",user.getId());
        payloadMap.put("bidAmount",userBidRequest.getAmount());
        payloadMap.put("bidderName",user.getUsername());
        payloadMap.put("previousHighestBid",previousHighestBid);
        payloadMap.put("timestamp",bid.getBidTime());

        JsonNode payloadNode = objectMapper.valueToTree(payloadMap);

        AuctionEvents auctionEvents = AuctionEvents.builder()
                .auctionEventType(AuctionEventType.BID_PLACED)
                .createdAt(Instant.now())
                .payload(payloadNode)
                .auction(auction)
                .build();

        auctionEventRepository.save(auctionEvents);

        applicationEventPublisher.publishEvent(new BidPlacedDomainEvent(auctionEvents.getId()));

        return "Successfully placed bid amount of "+auctionRequest.getAmount();
    }
}
