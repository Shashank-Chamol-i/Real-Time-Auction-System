package com.example.realTimeAuctionSystem.repository;

import com.example.realTimeAuctionSystem.model.Bids;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BidRepository extends JpaRepository<Bids,String> , JpaSpecificationExecutor<Bids> {
    Bids findByIdempotencyKey(String id);
}
