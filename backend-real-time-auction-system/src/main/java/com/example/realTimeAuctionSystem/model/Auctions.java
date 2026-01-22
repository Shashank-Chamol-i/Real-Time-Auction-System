package com.example.realTimeAuctionSystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Auctions {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String itemName;

    @Column(nullable = false,precision = 19,scale = 2)
    private BigDecimal basePrice = BigDecimal.ZERO;

    @Column(nullable = false,precision = 19,scale = 2)
    private BigDecimal currentHighestBid = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private AuctionStatus auctionStatus;


    private Instant startedTime;

    private Instant startedAt;

    private Instant endTime;

    @Version
    private long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",foreignKey = @ForeignKey(name = "fk_auctions_user"))
    private  Users user;


    @OneToMany(mappedBy = "auction",cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnore()
    List<Bids> bidsList = new ArrayList<>();


    @OneToMany(mappedBy = "auction",cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnore()
    List<AuctionEvents> auctionEventsList = new ArrayList<>();
}
