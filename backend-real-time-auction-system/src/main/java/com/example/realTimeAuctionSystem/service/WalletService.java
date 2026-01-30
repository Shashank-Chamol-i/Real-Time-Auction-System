package com.example.realTimeAuctionSystem.service;

import com.example.realTimeAuctionSystem.dto.DebitForAuctionRequest;
import com.example.realTimeAuctionSystem.dto.DebitForAuctionResponse;
import com.example.realTimeAuctionSystem.dto.AuctionResultRequest;
import com.example.realTimeAuctionSystem.dto.AuctionResultResponse;
import com.example.realTimeAuctionSystem.exception.InsufficientAmount;
import com.example.realTimeAuctionSystem.exception.NoSuchExist;
import com.example.realTimeAuctionSystem.model.*;
import com.example.realTimeAuctionSystem.repository.AuctionRepository;
import com.example.realTimeAuctionSystem.repository.UserRepository;
import com.example.realTimeAuctionSystem.repository.WalletTransactionRepository;

import com.example.realTimeAuctionSystem.specification.WalletSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    @Transactional
    public DebitForAuctionResponse debitForAuction(DebitForAuctionRequest request){

        Users user = userRepository.findById(request.getUserId()).orElseThrow(()->new NoSuchExist("No Such Users Exist : "));
        Auctions auction = auctionRepository.findById(request.getAuctionId()).orElseThrow(()-> new NoSuchExist("No Such Auction Exist :"));


       if(user.getWalletBalance().compareTo(request.getAmount())<0){
           throw new InsufficientAmount("Insufficient Balance : ");
       }

        BigDecimal currentAmount =  user.getWalletBalance().subtract(request.getAmount());
        BigDecimal lockedAmount = user.getLockedBalance()==null ? BigDecimal.ZERO: user.getLockedBalance().add(request.getAmount());

       user.setWalletBalance(currentAmount);
       user.setLockedBalance(lockedAmount);
       userRepository.save(user);

        WalletTransactions walletTransaction = WalletTransactions.builder()
                .amount(request.getAmount())
                .createdAt(Instant.now())
                .walletTransactionStatus(WalletTransactionStatus.COMPLETED)
                .walletTransactionType(WalletTransactionType.LOCK)
                .user(user)
                .auction(auction)
                .build();

        WalletTransactions savedTransactions = walletTransactionRepository.save(walletTransaction);

        return new DebitForAuctionResponse(
                savedTransactions.getId(),
                savedTransactions.getAmount(),
                savedTransactions.getCreatedAt(),
                savedTransactions.getWalletTransactionStatus().name(),
                savedTransactions.getWalletTransactionType().name(),
                savedTransactions.getUser().getId()
        );

    }

    public List<AuctionResultResponse> getAllLoser(AuctionResultRequest auctionResultRequest){
        Specification<WalletTransactions>specification =
            Specification.allOf(WalletSpecification.loserForAuction(auctionResultRequest.getAuctionId(), auctionResultRequest.getWinnerId()));

        return walletTransactionRepository.findAll(specification).stream()
                .map(entity -> new AuctionResultResponse(
                        entity.getUser().getId(),
                        entity.getAuction().getId(),
                        entity.getAmount()
                )).toList();
    }
    public List<AuctionResultResponse> getWinner(AuctionResultRequest winnerRequest){
        Specification<WalletTransactions> specification =
                Specification.allOf(WalletSpecification.WinnerForAuction(winnerRequest.getAuctionId(),winnerRequest.getWinnerId()));

        return walletTransactionRepository.findAll(specification).stream()
                .map(entity ->new AuctionResultResponse(
                        entity.getUser().getId(),
                        entity.getAuction().getId(),
                        entity.getAmount()
                )).toList();

    }

    @Transactional
    public String refundMoneyOfLooser(AuctionResultRequest auctionResultRequest){
        Auctions auction = auctionRepository.findById(auctionResultRequest.getAuctionId()).orElseThrow(()->new NoSuchExist("No such Auction Exist :"));
        List<AuctionResultResponse> auctionResultResponseList = getAllLoser(auctionResultRequest);
        Map<String , BigDecimal> refundMap = auctionResultResponseList.stream()
                .collect(Collectors.groupingBy(
                        AuctionResultResponse::getUserId,
                        Collectors.mapping(
                                AuctionResultResponse::getAmount,
                                Collectors.reducing(BigDecimal.ZERO,BigDecimal::add)
                        )
                ));
        refundMap.forEach((userId,amount)->{
            Users user =userRepository.findById(userId).orElseThrow(()-> new NoSuchExist("No such User Exist : "));
            WalletTransactions walletTransactions = WalletTransactions.builder()
                    .user(user)
                    .auction(auction)
                    .amount(amount)
                    .walletTransactionType(WalletTransactionType.RELEASED)
                    .walletTransactionStatus(WalletTransactionStatus.COMPLETED)
                    .build();

            user.setWalletBalance(user.getWalletBalance().add(amount));
            user.setLockedBalance(user.getLockedBalance().subtract(amount));

            userRepository.save(user);
            walletTransactionRepository.save(walletTransactions);
        });
    return  "Refunds processed successfully for " + refundMap.size() ;
    }


    @Transactional
    public String refundAndSettlementOfWinner(AuctionResultRequest winnerRequest){
        Auctions auction =  auctionRepository.findById(winnerRequest.getAuctionId()).orElseThrow(()-> new NoSuchExist("No such Auction Exist :"));
        List<AuctionResultResponse> responses =  getWinner(winnerRequest);

        BigDecimal totalLocked = responses.stream()
                .map(AuctionResultResponse::getAmount)
                .reduce(BigDecimal.ZERO,BigDecimal::add);

        BigDecimal settleAmount =  auction.getCurrentHighestBid();

        BigDecimal refundAmount = totalLocked.subtract(settleAmount);

        Users user =  userRepository.findById(winnerRequest.getWinnerId()).orElseThrow(()-> new NoSuchExist("No such user exist : "));

        WalletTransactions walletTransactions = WalletTransactions.builder()
                .user(user)
                .auction(auction)
                .amount(settleAmount)
                .walletTransactionStatus(WalletTransactionStatus.COMPLETED)
                .walletTransactionType(WalletTransactionType.SETTLEMENT)
                .build();
        walletTransactionRepository.save(walletTransactions);
        user.setLockedBalance(user.getLockedBalance().subtract(settleAmount));

        if(refundAmount.compareTo(BigDecimal.ZERO)>0){

            WalletTransactions walletTransaction = WalletTransactions.builder()
                    .user(user)
                    .auction(auction)
                    .amount(refundAmount)
                    .walletTransactionStatus(WalletTransactionStatus.COMPLETED)
                    .walletTransactionType(WalletTransactionType.RELEASED)
                    .build();
            walletTransactionRepository.save(walletTransaction);
            user.setLockedBalance(user.getLockedBalance().subtract(refundAmount));
            user.setWalletBalance(user.getWalletBalance().add(refundAmount));

        }
        userRepository.save(user);

        return "MONEY  SETTLEMENT  COMPLETED : ";
    }

}
