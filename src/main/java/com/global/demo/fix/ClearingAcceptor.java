package com.global.demo.fix;

import com.global.demo.model.Trade;
import com.global.demo.service.TradeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import quickfix.*;
import quickfix.field.*;
import quickfix.fix44.AllocationInstruction;
import quickfix.fix44.AllocationReport;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@Profile("acceptor")
public class ClearingAcceptor extends MessageCracker implements Application {
    private static final Logger log = LoggerFactory.getLogger(ClearingAcceptor.class);
    private final TradeService tradeService;

    public ClearingAcceptor(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @Override
    public void onCreate(SessionID sessionId) {
        log.info("Acceptor Session created: {}", sessionId);
    }

    @Override
    public void onLogon(SessionID sessionId) {
        log.info("Acceptor Logon: {}", sessionId);
    }

    @Override
    public void onLogout(SessionID sessionId) {
        log.info("Acceptor Logout: {}", sessionId);
    }

    @Override
    public void toAdmin(Message message, SessionID sessionId) {
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionId)
            throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
    }

    @Override
    public void toApp(Message message, SessionID sessionId) throws DoNotSend {
    }

    @Override
    public void fromApp(Message message, SessionID sessionId)
            throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        log.info("Acceptor received message: {}", message.getClass().getSimpleName());
        crack(message, sessionId);
    }

    public void onMessage(AllocationInstruction message, SessionID sessionId) throws FieldNotFound {
        String allocId = message.getAllocID().getValue();
        String tradeId = message.getTradeDate().getValue() + "-" + allocId;

        log.info("Received AllocationInstruction. AllocID: {}", allocId);

        Trade trade = Trade.builder()
                .tradeId(tradeId)
                .allocationId(allocId)
                .symbol(message.getSymbol().getValue())
                .quantity(BigDecimal.valueOf(message.getQuantity().getValue()))
                .side(message.getSide().getValue() == Side.BUY ? "BUY" : "SELL")
                .build();

        tradeService.createTrade(trade);
        tradeService.processAllocation(tradeId, allocId);

        sendAllocationReport(message, sessionId);
    }

    private void sendAllocationReport(AllocationInstruction instruction, SessionID sessionId) throws FieldNotFound {
        AllocationReport report = new AllocationReport();
        report.set(new AllocReportID(UUID.randomUUID().toString().substring(0, 8)));
        report.set(instruction.getAllocID());
        report.set(new AllocTransType(AllocTransType.NEW));
        report.set(new AllocReportType(3)); // SELLSIDE_CALCULATED_USING_PRELIMINARY
        report.set(instruction.getSide());
        report.set(instruction.getQuantity());
        report.set(new AvgPx(0));
        report.set(instruction.getTradeDate());
        report.set(instruction.getSymbol());
        report.set(new AllocStatus(AllocStatus.ACCEPTED));
        report.set(new Text("Allocation Report for " + instruction.getAllocID().getValue()));

        try {
            Session.sendToTarget(report, sessionId);
            log.info("Sent AllocationReport for AllocID: {}", instruction.getAllocID().getValue());
        } catch (SessionNotFound e) {
            log.error("Session not found: {}", sessionId, e);
        }
    }
}
