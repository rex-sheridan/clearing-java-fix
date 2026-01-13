package com.global.demo.service;

import com.global.demo.model.Trade;
import java.util.List;

public interface TradeService {
    Trade createTrade(Trade trade);

    Trade processAllocation(String tradeId, String allocationId);

    Trade confirmTrade(String tradeId);

    List<Trade> getAllTrades();
}
