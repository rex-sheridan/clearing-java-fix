package com.global.demo.service;

import com.global.demo.model.Trade;
import java.util.List;

public interface TradeService {
    Trade createTrade(Trade trade);

    Trade createTrade(Trade trade, String tradeId);

    Trade processAllocation(String tradeId, String allocationId);

    Trade confirmTrade(String tradeId);

    List<Trade> getAllTrades();

    List<com.global.demo.model.TradeRecord> getAllTradeRecords();
}
