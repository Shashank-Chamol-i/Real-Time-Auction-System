package com.example.realTimeAuctionSystem.repository;

import com.example.realTimeAuctionSystem.model.Auctions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionRepository extends JpaRepository<Auctions,String> {
}
