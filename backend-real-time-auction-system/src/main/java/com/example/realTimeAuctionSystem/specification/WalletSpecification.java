package com.example.realTimeAuctionSystem.specification;


import com.example.realTimeAuctionSystem.model.WalletTransactionStatus;
import com.example.realTimeAuctionSystem.model.WalletTransactionType;
import com.example.realTimeAuctionSystem.model.WalletTransactions;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;


public class WalletSpecification {

    public static Specification<WalletTransactions> loserForAuction(String auctionId , String winnerId){
        return (root, query, criteriaBuilder) -> {
            Subquery<Long> refundExist = query.subquery(Long.class);
            Root<WalletTransactions> subRoot = refundExist.from(WalletTransactions.class);

            refundExist.select(criteriaBuilder.literal(1L))
                    .where(
                            criteriaBuilder.equal(subRoot.get("auction").get("id"),root.get("auction").get("id")),
                            criteriaBuilder.equal(subRoot.get("user").get("id"),root.get("user").get("id")),
                            criteriaBuilder.equal(subRoot.get("walletTransactionType"),WalletTransactionType.RELEASED)
                    );

            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("auction").get("id"),auctionId),
                    criteriaBuilder.equal(root.get("walletTransactionType"),WalletTransactionType.LOCK),
                    criteriaBuilder.equal(root.get("walletTransactionStatus"),WalletTransactionStatus.COMPLETED),
                    criteriaBuilder.notEqual(root.get("user").get("id"),winnerId),
                    criteriaBuilder.not(criteriaBuilder.exists(refundExist))
            );
        };

    }
    public static Specification<WalletTransactions> WinnerForAuction(String auctionId , String winnerId){
        return (root, query, criteriaBuilder) -> {

            Subquery<Long> refundExist = query.subquery(Long.class);
            Root<WalletTransactions> subRoot = refundExist.from(WalletTransactions.class);

            refundExist.select(criteriaBuilder.literal(1L))
                    .where(
                            criteriaBuilder.equal(subRoot.get("auction").get("id"),auctionId),
                            criteriaBuilder.equal(subRoot.get("user").get("id"),winnerId),
                            criteriaBuilder.equal(subRoot.get("walletTransactionType"),WalletTransactionType.SETTLEMENT)
                    );

            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("auction").get("id"),auctionId),
                    criteriaBuilder.equal(root.get("walletTransactionType"),WalletTransactionType.LOCK),
                    criteriaBuilder.equal(root.get("walletTransactionStatus"),WalletTransactionStatus.COMPLETED),
                    criteriaBuilder.equal(root.get("user").get("id"),winnerId),
                    criteriaBuilder.not(criteriaBuilder.exists(refundExist))
            );
        };
    }

}
