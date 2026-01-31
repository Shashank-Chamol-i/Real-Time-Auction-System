package com.example.realTimeAuctionSystem.component;

import com.example.realTimeAuctionSystem.model.AuctionEvents;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuctionWebSocketPublisher {
    private final SimpMessagingTemplate messagingTemplate;

    public void publishAuctionEvent(AuctionEvents event){
        String destination = "topic/auction/"+event.getAuction().getId();
        messagingTemplate.convertAndSend(destination,event.getPayload());
    }
}
