package com.global.demo.web;

import com.global.demo.model.Trade;
import com.global.demo.service.TradeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.field.*;
import quickfix.fix44.AllocationInstruction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Controller
@Profile("initiator")
public class MemberFirmController {
    private static final Logger log = LoggerFactory.getLogger(MemberFirmController.class);
    private final TradeService tradeService;

    public MemberFirmController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @GetMapping("/")
    public String index() {
        return "submit-trade";
    }

    @PostMapping("/submit-trade")
    public String submitTrade(@RequestParam String symbol,
            @RequestParam BigDecimal quantity,
            @RequestParam String side) {
        log.info("Submitting trade for {} {} {}", side, quantity, symbol);

        Trade trade = Trade.builder()
                .symbol(symbol)
                .quantity(quantity)
                .side(side)
                .build();

        // In the initiator process, we just create it in the local DB (optional)
        // and send the FIX message.
        tradeService.createTrade(trade);

        sendFixAllocation(trade);

        return "redirect:/";
    }

    private void sendFixAllocation(Trade trade) {
        AllocationInstruction alloc = new AllocationInstruction();
        alloc.set(new AllocID(UUID.randomUUID().toString().substring(0, 8)));
        alloc.set(new AllocTransType(AllocTransType.NEW));
        alloc.set(new AllocType(1)); // CALCULATED_PRELIMINARY_LEVEL
        alloc.set(new Side(trade.getSide().equals("BUY") ? Side.BUY : Side.SELL));
        alloc.set(new Quantity(trade.getQuantity().doubleValue()));
        alloc.set(new AvgPx(0));
        alloc.set(new TradeDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))));
        alloc.set(new Symbol(trade.getSymbol()));

        SessionID sessionID = new SessionID("FIX.4.4", "MEMBER_ONE", "GLOBAL_CLEAR");
        try {
            Session session = Session.lookupSession(sessionID);
            if (session != null && session.isLoggedOn()) {
                Session.sendToTarget(alloc, sessionID);
                log.info("Sent FIX AllocationInstruction for {}", trade.getTradeId());
            } else {
                log.warn("FIX Session not logged on: {}. Message queued or failed.", sessionID);
            }
        } catch (Exception e) {
            log.error("Failed to send FIX message", e);
        }
    }
}
