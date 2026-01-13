package com.global.demo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Trade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tradeId;
    private String allocationId;
    private String symbol;
    private BigDecimal quantity;
    private BigDecimal price;
    private String currency;
    private String side; // BUY, SELL

    private String status; // SUBMITTED, ALLOCATED, CONFIRMED, REJECTED
    private String processingNote;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Trade() {
    }

    public static TradeBuilder builder() {
        return new TradeBuilder();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getAllocationId() {
        return allocationId;
    }

    public void setAllocationId(String allocationId) {
        this.allocationId = allocationId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProcessingNote() {
        return processingNote;
    }

    public void setProcessingNote(String processingNote) {
        this.processingNote = processingNote;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static class TradeBuilder {
        private String symbol;
        private BigDecimal quantity;
        private String side;
        private String tradeId;
        private String allocationId;

        public TradeBuilder symbol(String symbol) {
            this.symbol = symbol;
            return this;
        }

        public TradeBuilder quantity(BigDecimal quantity) {
            this.quantity = quantity;
            return this;
        }

        public TradeBuilder side(String side) {
            this.side = side;
            return this;
        }

        public TradeBuilder tradeId(String tradeId) {
            this.tradeId = tradeId;
            return this;
        }

        public TradeBuilder allocationId(String allocationId) {
            this.allocationId = allocationId;
            return this;
        }

        public Trade build() {
            Trade trade = new Trade();
            trade.setSymbol(symbol);
            trade.setQuantity(quantity);
            trade.setSide(side);
            trade.setTradeId(tradeId);
            trade.setAllocationId(allocationId);
            return trade;
        }
    }
}
