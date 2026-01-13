package com.global.demo.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {
    Optional<Trade> findByTradeId(String tradeId);

    Optional<Trade> findByAllocationId(String allocationId);
}
