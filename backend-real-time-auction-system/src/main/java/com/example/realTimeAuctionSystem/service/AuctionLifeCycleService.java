package com.example.realTimeAuctionSystem.service;

import com.example.realTimeAuctionSystem.dto.CreateAuctionRequest;
import com.example.realTimeAuctionSystem.exception.AuctionExpire;
import com.example.realTimeAuctionSystem.exception.NoSuchExist;
import com.example.realTimeAuctionSystem.model.AuctionStatus;
import com.example.realTimeAuctionSystem.model.Auctions;
import com.example.realTimeAuctionSystem.repository.AuctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuctionLifeCycleService {

    private final AuctionRepository auctionRepository;

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

        return "Auction Id : "+savedAuction.getId()+"\nAuction Created Successfully : ";
    }

    public String startAuction(String auctionId) {
           Auctions auction =  auctionRepository.findById(auctionId)
                    .orElseThrow(()->new AuctionExpire("No Such Auction Exist : "));

           if(auction.getAuctionStatus()!=AuctionStatus.CREATED){
               throw new AuctionExpire("Auction not created yet : ");
           }

           auction.setAuctionStatus(AuctionStatus.LIVE);
           auction.setStartedAt(Instant.now());

           Auctions savedAuction =  auctionRepository.save(auction);
           return "Auction is "+savedAuction.getAuctionStatus()+"......";
    }

    public String closeAuction(String auctionId) {
       Auctions auction =  auctionRepository.findById(auctionId)
                .orElseThrow(()->new NoSuchExist("No Such Auction Exist : "));

       if(auction.getAuctionStatus()!=AuctionStatus.LIVE){
           throw  new AuctionExpire("Auction is not live :");
       }

       auction.setAuctionStatus(AuctionStatus.CLOSED);
       Auctions savedAuction = auctionRepository.save(auction);

       return "Auction is "+savedAuction.getAuctionStatus()+"....";
    }

    public String settleAuction(String auctionId) {
       Auctions auction =  auctionRepository.findById(auctionId)
                .orElseThrow(()-> new NoSuchExist("No such Auction Exist : "));

       if(auction.getAuctionStatus()!=AuctionStatus.CLOSED){
           throw  new AuctionExpire("Auction is not Closed Yet : ");
       }
       auction.setAuctionStatus(AuctionStatus.SETTLED);

       Auctions savedAuction = auctionRepository.save(auction);

       return "Auction is "+savedAuction.getAuctionStatus()+".....";
    }
}
