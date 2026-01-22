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
@Table(name = "bids",uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id","auction_id","idempotency_key"})})
public class Bids {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id",nullable = false,foreignKey = @ForeignKey(name = "fk_bids_auction"))
    private Auctions auction;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false,foreignKey = @ForeignKey(name = "fk_bids_user"))
    private Users user;

    @Column(nullable = false,precision = 19,scale = 4)
    private BigDecimal amount = BigDecimal.ZERO;

    @CreationTimestamp
    private Instant bidTime;

    @Column(nullable = false)
    private String idempotencyKey;
}
