package com.example.realTimeAuctionSystem.repository;

import com.example.realTimeAuctionSystem.model.WalletTransactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WalletTransactionRepository extends JpaRepository<WalletTransactions,String> , JpaSpecificationExecutor<WalletTransactions> {
}
