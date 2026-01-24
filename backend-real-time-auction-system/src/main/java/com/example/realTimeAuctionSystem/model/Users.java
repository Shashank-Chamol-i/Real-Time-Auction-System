package com.example.realTimeAuctionSystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank(message = "Username cannot be blank")
    @Column(nullable = false,unique = true)
    private String username;


    @NotBlank(message = "Email cannot be blank")
    @Column(nullable = false,unique = true)
    private String email;

    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder.Default
    @Column(nullable = false,precision = 19,scale = 2)
    private BigDecimal walletBalance = BigDecimal.ZERO;

    @Builder.Default
    @Column(nullable = false, precision = 19,scale = 2)
    private BigDecimal lockedBalance = BigDecimal.ZERO;

    private boolean active;


    @CreationTimestamp
    private Instant createdAt;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnore()
    List<Bids> bidsList = new ArrayList<>();


    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnore()
    List<WalletTransactions> walletTransactionsList = new ArrayList<>();


    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnore()
    List<Auctions> auctionsList = new ArrayList<>();
}
