package com.example.realTimeAuctionSystem.repository;

import com.example.realTimeAuctionSystem.model.Bids;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidRepository extends JpaRepository<Bids,String> {
    Bids findByIdempotencyKey(String id);
}
