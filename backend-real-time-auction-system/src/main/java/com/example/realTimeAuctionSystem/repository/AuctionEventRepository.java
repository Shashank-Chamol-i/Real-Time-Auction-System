package com.example.realTimeAuctionSystem.repository;

import com.example.realTimeAuctionSystem.model.AuctionEvents;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionEventRepository extends JpaRepository<AuctionEvents , String> {

}
