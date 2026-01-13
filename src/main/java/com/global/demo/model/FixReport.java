package com.global.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class FixReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String messageType;
    private String allocId;
    private String status;
    private String symbol;
    private String side;
    private Double quantity;
    private Double price;
    private String transType;
    private String text;
    private LocalDateTime receivedAt;

    public FixReport() {
    }

    public FixReport(String messageType, String allocId, String status, String text) {
        this.messageType = messageType;
        this.allocId = allocId;
        this.status = status;
        this.text = text;
        this.receivedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getAllocId() {
        return allocId;
    }

    public String getStatus() {
        return status;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }
}
