package com.global.demo;

import com.global.demo.model.Trade;
import com.global.demo.model.TradeRepository;
import com.global.demo.service.TradeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.field.*;
import quickfix.fix44.AllocationInstruction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@org.springframework.test.context.TestPropertySource(properties = {
        "fix.acceptor.config=acceptor-test.cfg",
        "fix.initiator.config=initiator-test.cfg"
})
class FixMessagingIntegrationTest {

    @Autowired
    private TradeService tradeService;

    @Autowired
    private TradeRepository tradeRepository;

    @Test
    void testEndToEndFixFlow() throws Exception {
        // Prepare a trade
        String symbol = "CDX.IG.41";
        BigDecimal qty = new BigDecimal("1000000");
        String side = "BUY";

        // Create a FIX AllocationInstruction
        AllocationInstruction alloc = new AllocationInstruction();
        String allocId = UUID.randomUUID().toString().substring(0, 8);
        alloc.set(new AllocID(allocId));
        alloc.set(new AllocTransType(AllocTransType.NEW));
        alloc.set(new AllocType(1));
        alloc.set(new Side(Side.BUY));
        alloc.set(new Quantity(qty.doubleValue()));
        alloc.set(new AvgPx(0));
        alloc.set(new TradeDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))));
        alloc.set(new Symbol(symbol));

        // Wait for sessions to be logged on
        SessionID sessionID = new SessionID("FIX.4.4", "MEMBER_ONE", "GLOBAL_CLEAR");
        await().atMost(10, TimeUnit.SECONDS).until(() -> {
            Session session = Session.lookupSession(sessionID);
            return session != null && session.isLoggedOn();
        });

        // Send message
        boolean sent = Session.sendToTarget(alloc, sessionID);
        assertTrue(sent, "Message should be sent successfully");

        // Wait for the acceptor to process and update DB
        await().atMost(5, TimeUnit.SECONDS).until(() -> {
            return tradeRepository.findByAllocationId(allocId).isPresent();
        });

        // Verify the trade in DB
        Optional<Trade> tradeOpt = tradeRepository.findByAllocationId(allocId);
        assertTrue(tradeOpt.isPresent());
        Trade trade = tradeOpt.get();
        assertEquals("ALLOCATED", trade.getStatus());
        assertEquals(symbol, trade.getSymbol());
        assertEquals(qty.stripTrailingZeros(), trade.getQuantity().stripTrailingZeros());
    }
}
