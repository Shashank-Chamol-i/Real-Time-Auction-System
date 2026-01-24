package com.example.realTimeAuctionSystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class WalletTransactions {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false,foreignKey = @ForeignKey(name = "fk_walletTransactions_user"))
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id",nullable = false,foreignKey = @ForeignKey(name = "fk_walletTransaction_auction"))
    private Auctions auction;

    @Builder.Default
    @Column(nullable = false,precision = 19,scale = 4)
    private BigDecimal amount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private WalletTransactionType walletTransactionType;

    @Enumerated(EnumType.STRING)
    private WalletTransactionStatus walletTransactionStatus;

    @CreationTimestamp
    private Instant createdAt;
}
