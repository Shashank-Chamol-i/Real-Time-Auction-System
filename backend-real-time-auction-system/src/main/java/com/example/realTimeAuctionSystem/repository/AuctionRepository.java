package com.example.realTimeAuctionSystem.repository;

import com.example.realTimeAuctionSystem.model.AuctionStatus;
import com.example.realTimeAuctionSystem.model.Auctions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface AuctionRepository extends JpaRepository<Auctions,String> {
    List<Auctions> findByAuctionStatusAndStartedTimeLessThanEqual(AuctionStatus auctionStatus, Instant startedTime);
    List<Auctions> findByAuctionStatusAndEndTimeLessThanEqual(AuctionStatus auctionStatus,Instant endTime);
}
