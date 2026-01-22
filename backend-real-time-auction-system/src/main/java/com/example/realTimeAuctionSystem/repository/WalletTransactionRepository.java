package com.example.realTimeAuctionSystem.repository;

import com.example.realTimeAuctionSystem.model.WalletTransactions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletTransactionRepository extends JpaRepository<WalletTransactions,String> {
}
