package com.global.demo.service;

import com.global.demo.model.Trade;
import com.global.demo.model.TradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TradeServiceTest {

    @Mock
    private TradeRepository tradeRepository;

    private TradeServiceImpl tradeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tradeService = new TradeServiceImpl(tradeRepository);
    }

    @Test
    void testCreateTrade() {
        Trade trade = new Trade();
        trade.setSymbol("GLOBAL");
        trade.setQuantity(new BigDecimal("100"));
        trade.setSide("BUY");

        when(tradeRepository.save(any(Trade.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trade createdTrade = tradeService.createTrade(trade);

        assertNotNull(createdTrade.getTradeId());
        assertEquals("SUBMITTED", createdTrade.getStatus());
        verify(tradeRepository, times(1)).save(trade);
    }

    @Test
    void testProcessAllocation() {
        String tradeId = "TRD-123";
        String allocId = "ALLOC-456";
        Trade trade = new Trade();
        trade.setTradeId(tradeId);
        trade.setStatus("SUBMITTED");

        when(tradeRepository.findByTradeId(tradeId)).thenReturn(Optional.of(trade));
        when(tradeRepository.save(any(Trade.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trade allocatedTrade = tradeService.processAllocation(tradeId, allocId);

        assertEquals("ALLOCATED", allocatedTrade.getStatus());
        assertEquals(allocId, allocatedTrade.getAllocationId());
        verify(tradeRepository, times(1)).save(trade);
    }

    @Test
    void testConfirmTrade() {
        String tradeId = "TRD-123";
        Trade trade = new Trade();
        trade.setTradeId(tradeId);
        trade.setStatus("ALLOCATED");

        when(tradeRepository.findByTradeId(tradeId)).thenReturn(Optional.of(trade));
        when(tradeRepository.save(any(Trade.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trade confirmedTrade = tradeService.confirmTrade(tradeId);

        assertEquals("CONFIRMED", confirmedTrade.getStatus());
        verify(tradeRepository, times(1)).save(trade);
    }
}
