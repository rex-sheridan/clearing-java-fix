package com.global.demo.service;

import com.global.demo.model.Trade;
import com.global.demo.model.TradeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TradeServiceImpl implements TradeService {
    private static final Logger log = LoggerFactory.getLogger(TradeServiceImpl.class);
    private final TradeRepository tradeRepository;
    private final TradeKafkaProducer tradeKafkaProducer;

    public TradeServiceImpl(TradeRepository tradeRepository, TradeKafkaProducer tradeKafkaProducer) {
        this.tradeRepository = tradeRepository;
        this.tradeKafkaProducer = tradeKafkaProducer;
    }

    @Override
    @Transactional
    public Trade createTrade(Trade trade) {
        if (trade.getTradeId() == null) {
            trade.setTradeId("TRD-" + UUID.randomUUID().toString().substring(0, 8));
        }
        trade.setStatus("SUBMITTED");
        log.info("Creating trades: {}", trade.getTradeId());
        return tradeRepository.save(trade);
    }

    @Override
    @Transactional
    public Trade createTrade(Trade trade, String tradeId) {
        trade.setTradeId(tradeId);
        trade.setStatus("SUBMITTED");
        log.info("Creating trades: {}", trade.getTradeId());
        return tradeRepository.save(trade);
    }

    @Override
    @Transactional
    public Trade processAllocation(String tradeId, String allocationId) {
        log.info("Processing allocation for trade: {}, allocationId: {}", tradeId, allocationId);
        Trade trade = tradeRepository.findByTradeId(tradeId)
                .orElseThrow(() -> new RuntimeException("Trade not found: " + tradeId));

        trade.setAllocationId(allocationId);
        trade.setStatus("ALLOCATED");
        return tradeRepository.save(trade);
    }

    @Override
    @Transactional
    public Trade confirmTrade(String tradeId) {
        log.info("Confirming trade: {}", tradeId);
        Trade trade = tradeRepository.findByTradeId(tradeId)
                .orElseThrow(() -> new RuntimeException("Trade not found: " + tradeId));

        trade.setStatus("CONFIRMED");
        Trade savedTrade = tradeRepository.save(trade);

        tradeKafkaProducer.sendConfirmedTrade(savedTrade);

        return savedTrade;
    }

    @Override
    public List<Trade> getAllTrades() {
        return tradeRepository.findAll();
    }

    @Override
    public List<com.global.demo.model.TradeRecord> getAllTradeRecords() {
        return tradeRepository.findAll().stream()
                .map(com.global.demo.model.TradeRecord::fromEntity)
                .toList();
    }

    public Trade getLatestTrade() {
        List<Trade> trades = tradeRepository.findAll();
        return trades.isEmpty() ? null : trades.getLast();
    }
}
