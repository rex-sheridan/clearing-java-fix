package com.global.demo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * A Java 21 Record representing an immutable trade summary.
 * Demonstrates the use of records for cleaner DTOs.
 */
public record TradeRecord(
        String tradeId,
        String symbol,
        BigDecimal quantity,
        String side,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
    public static TradeRecord fromEntity(Trade trade) {
        return new TradeRecord(
                trade.getTradeId(),
                trade.getSymbol(),
                trade.getQuantity(),
                trade.getSide(),
                trade.getStatus(),
                trade.getCreatedAt(),
                trade.getUpdatedAt());
    }
}
