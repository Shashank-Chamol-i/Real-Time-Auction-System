package com.example.realTimeAuctionSystem.component;


import com.example.realTimeAuctionSystem.dto.BidPlacedDomainEvent;
import com.example.realTimeAuctionSystem.exception.NoSuchExist;
import com.example.realTimeAuctionSystem.model.AuctionEvents;
import com.example.realTimeAuctionSystem.repository.AuctionEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionEventAfterCommitListener {
    private final AuctionEventRepository auctionEventRepository;
    private final AuctionWebSocketPublisher webSocketPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBidPlaced (BidPlacedDomainEvent event){
        AuctionEvents auctionEvents =
                auctionEventRepository.findById(event.getAuctionEventId()).orElseThrow(()-> new NoSuchExist("No Such Auction Exist : "));

        webSocketPublisher.publishAuctionEvent(auctionEvents);
        log.info("Broadcasting auction event {} ",event.getAuctionEventId());
    }
}
