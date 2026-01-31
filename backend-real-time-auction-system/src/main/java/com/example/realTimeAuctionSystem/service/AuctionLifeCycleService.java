package com.example.realTimeAuctionSystem.service;

import com.example.realTimeAuctionSystem.dto.CreateAuctionRequest;
import com.example.realTimeAuctionSystem.exception.AuctionExpire;
import com.example.realTimeAuctionSystem.exception.NoSuchExist;
import com.example.realTimeAuctionSystem.model.AuctionEventType;
import com.example.realTimeAuctionSystem.model.AuctionEvents;
import com.example.realTimeAuctionSystem.model.AuctionStatus;
import com.example.realTimeAuctionSystem.model.Auctions;
import com.example.realTimeAuctionSystem.repository.AuctionEventRepository;
import com.example.realTimeAuctionSystem.repository.AuctionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuctionLifeCycleService {

    private final AuctionRepository auctionRepository;
    private final AuctionEventRepository auctionEventRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public String createAuction(CreateAuctionRequest auctionRequest){
        if(!auctionRequest.getEndTime().isAfter(auctionRequest.getStartTime())){
            throw new AuctionExpire("End Time Must be after start time");
        }

        Auctions auctions = Auctions.builder()
                .auctionStatus(AuctionStatus.CREATED)
                .itemName(auctionRequest.getItemName())
                .basePrice(auctionRequest.getBaseAmount())
                .currentHighestBid(auctionRequest.getBaseAmount())
                .startedTime(auctionRequest.getStartTime())
                .endTime(auctionRequest.getEndTime())
                .build();

        Auctions savedAuction = auctionRepository.save(auctions);


        Map <String , Object> payloadMap = new HashMap<>();
        payloadMap.put("auctionId",auctions.getId());
        payloadMap.put("createdAt",auctions.getStartedTime());
        payloadMap.put("endTime",auctions.getEndTime());
        payloadMap.put("auctionStatus",auctions.getAuctionStatus());

        JsonNode payloadNode = objectMapper.valueToTree(payloadMap);


        AuctionEvents auctionEvents = AuctionEvents.builder()
                .auctionEventType(AuctionEventType.AUCTION_CREATED)
                .createdAt(Instant.now())
                .payload(payloadNode)
                .auction(auctions)
                .build();

        auctionEventRepository.save(auctionEvents);
        return "Auction Id : "+savedAuction.getId()+"\nAuction Created Successfully : ";


    }

    @Transactional
    public String startAuction(String auctionId) {
           Auctions auction =  auctionRepository.findById(auctionId)
                    .orElseThrow(()->new AuctionExpire("No Such Auction Exist : "));

           if(auction.getAuctionStatus()!=AuctionStatus.CREATED){
               throw new AuctionExpire("Auction not created yet : ");
           }

           auction.setAuctionStatus(AuctionStatus.LIVE);
           auction.setStartedAt(Instant.now());

           Auctions savedAuction =  auctionRepository.save(auction);


           Map<String , Object> payloadMap = new HashMap<>();
         payloadMap.put("auctionId",auction.getId());
         payloadMap.put("startTime",auction.getStartedAt());
         payloadMap.put("basePrice",auction.getBasePrice());
         payloadMap.put("auctionStatus",auction.getAuctionStatus());

         JsonNode payloadNode = objectMapper.valueToTree(payloadMap);
         AuctionEvents auctionEvents  = AuctionEvents.builder()
                 .auctionEventType(AuctionEventType.AUCTION_STARTED)
                 .createdAt(Instant.now())
                 .payload(payloadNode)
                 .auction(auction)
                 .build();

           auctionEventRepository.save(auctionEvents);
           return "Auction is "+savedAuction.getAuctionStatus()+"......";
    }

    @Transactional
    public String closeAuction(String auctionId) {
       Auctions auction =  auctionRepository.findById(auctionId)
                .orElseThrow(()->new NoSuchExist("No Such Auction Exist : "));

       if(auction.getAuctionStatus()!=AuctionStatus.LIVE){
           throw  new AuctionExpire("Auction is not live :");
       }

       auction.setAuctionStatus(AuctionStatus.CLOSED);
       Auctions savedAuction = auctionRepository.save(auction);


       Map<String , Object> payloadMap = new HashMap<>();
       payloadMap.put("auctionId",auction.getId());
       payloadMap.put("endTime",Instant.now());
       payloadMap.put("winnerId",auction.getUser().getId());
       payloadMap.put("finalAmount",auction.getCurrentHighestBid());
       payloadMap.put("auctionStatus",auction.getAuctionStatus());

       JsonNode payloadNode = objectMapper.valueToTree(payloadMap);



       AuctionEvents auctionEvents = AuctionEvents.builder()
               .auctionEventType(AuctionEventType.AUCTION_CLOSED)
               .createdAt(Instant.now())
               .payload(payloadNode)
               .auction(auction)
               .build();

        auctionEventRepository.save(auctionEvents);

       return "Auction is "+savedAuction.getAuctionStatus()+"....";
    }

    @Transactional
    public String settleAuction(String auctionId) {
       Auctions auction =  auctionRepository.findById(auctionId)
                .orElseThrow(()-> new NoSuchExist("No such Auction Exist : "));

       if(auction.getAuctionStatus()!=AuctionStatus.CLOSED){
           throw  new AuctionExpire("Auction is not Closed Yet : ");
       }
       auction.setAuctionStatus(AuctionStatus.SETTLED);

       Auctions savedAuction = auctionRepository.save(auction);


       Map<String , Object> payloadMap = new HashMap<>();
       payloadMap.put("auctionId",auction.getId());
       payloadMap.put("settleTime",Instant.now());
       payloadMap.put("auctionStatus",AuctionEventType.AUCTION_SETTLED);
       JsonNode payloadNode = objectMapper.valueToTree(payloadMap);

       AuctionEvents auctionEvents = AuctionEvents.builder()
               .auctionEventType(AuctionEventType.AUCTION_SETTLED)
               .createdAt(Instant.now())
               .payload(payloadNode)
               .auction(auction)
               .build();

        auctionEventRepository.save(auctionEvents);

       return "Auction is "+savedAuction.getAuctionStatus()+".....";
    }
}
