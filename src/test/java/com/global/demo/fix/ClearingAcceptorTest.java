package com.global.demo.fix;

import com.global.demo.service.TradeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import quickfix.FieldNotFound;
import quickfix.SessionID;
import quickfix.field.*;
import quickfix.fix44.AllocationInstruction;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ClearingAcceptorTest {

    @Mock
    private TradeService tradeService;

    private ClearingAcceptor clearingAcceptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        clearingAcceptor = new ClearingAcceptor(tradeService);
    }

    @Test
    void testOnMessageAllocationInstruction() throws FieldNotFound {
        AllocationInstruction message = new AllocationInstruction();
        message.set(new AllocID("12345"));
        message.set(new AllocTransType(AllocTransType.NEW));
        message.set(new AllocType(1));
        message.set(new Side(Side.BUY));
        message.set(new Symbol("GLOBAL"));
        message.set(new Quantity(100.0));
        message.set(new AvgPx(50.0));
        message.set(new TradeDate("20260112"));

        SessionID sessionId = new SessionID("FIX.4.4", "SENDER", "TARGET");

        // We can't easily mock Session.sendToTarget because it's static
        // However, we can verify the service calls
        clearingAcceptor.onMessage(message, sessionId);

        verify(tradeService, times(1)).createTrade(any());
        verify(tradeService, times(1)).processAllocation(anyString(), anyString());
    }
}
