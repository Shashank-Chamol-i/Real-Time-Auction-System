package com.example.realTimeAuctionSystem.service;

import com.example.realTimeAuctionSystem.model.AuctionStatus;
import com.example.realTimeAuctionSystem.model.Auctions;
import com.example.realTimeAuctionSystem.repository.AuctionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuctionSchedulerService {

    private final AuctionRepository auctionRepository;
    private final AuctionLifeCycleService auctionLifeCycleService;

    // Runs every 2 seconds
    @Scheduled(fixedDelay = 2000)
    @Transactional
    public void autoStart() {
        Instant now = Instant.now();
        log.info("Auction Scheduler running at {}", now);

        List<Auctions> auctions = auctionRepository.findByAuctionStatusAndStartedTimeLessThanEqual(AuctionStatus.CREATED, now);
        log.info("Auctions ready to start = {}", auctions.size());

        for (Auctions auction : auctions) {
            try {
                Instant dbTime = auction.getStartedTime();

                if (!dbTime.isAfter(now)) { // dbTime <= now
                    log.info("Starting auction {} scheduled at {}", auction.getId(), dbTime);
                    auctionLifeCycleService.startAuction(auction.getId());
                    log.info("Auction {} automatically started", auction.getId());
                }
            } catch (Exception e) {
                log.error("Failed to start auction {}", auction.getId(), e);
            }
        }
    }

    @Scheduled(fixedDelay = 2000)
    @Transactional
    public void autoClose(){
        Instant now = Instant.now();
        log.info("Auction Scheduler running at {}",now);
        List<Auctions> auctions  = auctionRepository.findByAuctionStatusAndEndTimeLessThanEqual(AuctionStatus.LIVE,Instant.now());

        for(Auctions auction : auctions){
            try{
                Instant dbTime = auction.getEndTime();
                if(!dbTime.isAfter(now)){
                    log.info("Ending Auction {} scheduled at {}",auction.getId(),dbTime);
                    auctionLifeCycleService.closeAuction(auction.getId());
                    log.info("Auction {} automatically closed :",auction.getId());
                }
            }catch (Exception e){
                log.error("Failed to close auction {} ",auction.getId(),e);
            }
        }
    }
}
