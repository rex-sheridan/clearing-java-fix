package com.global.demo.fix;

import com.global.demo.model.FixReport;
import com.global.demo.model.FixReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import quickfix.*;
import quickfix.field.*;
import quickfix.fix44.AllocationReport;
import quickfix.fix44.Confirmation;

@Component
@Profile("initiator")
public class MemberInitiator extends MessageCracker implements Application {
    private static final Logger log = LoggerFactory.getLogger(MemberInitiator.class);
    private final FixReportRepository fixReportRepository;

    public MemberInitiator(FixReportRepository fixReportRepository) {
        this.fixReportRepository = fixReportRepository;
    }

    @Override
    public void onCreate(SessionID sessionId) {
        log.info("Initiator Session created: {}", sessionId);
    }

    @Override
    public void onLogon(SessionID sessionId) {
        log.info("Initiator Logon: {}", sessionId);
    }

    @Override
    public void onLogout(SessionID sessionId) {
        log.info("Initiator Logout: {}", sessionId);
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
        log.info("Initiator received message: {}", message.getClass().getSimpleName());
        crack(message, sessionId);
    }

    public void onMessage(AllocationReport message, SessionID sessionId) throws FieldNotFound {
        String allocId = message.getAllocID().getValue();
        int status = message.getAllocStatus().getValue();
        String text = message.isSetField(Text.FIELD) ? message.getText().getValue() : "";

        log.info("Member received AllocationReport for AllocID: {}. Status: {}", allocId, status);

        FixReport report = new FixReport("AllocationReport", allocId, String.valueOf(status), text);
        if (message.isSetField(Symbol.FIELD))
            report.setSymbol(message.getSymbol().getValue());
        if (message.isSetField(Side.FIELD))
            report.setSide(String.valueOf(message.getSide().getValue()));
        if (message.isSetField(Quantity.FIELD))
            report.setQuantity(message.getQuantity().getValue());
        if (message.isSetField(AvgPx.FIELD))
            report.setPrice(message.getAvgPx().getValue());
        if (message.isSetField(AllocTransType.FIELD))
            report.setTransType(String.valueOf(message.getAllocTransType().getValue()));

        fixReportRepository.save(report);
    }

    public void onMessage(Confirmation message, SessionID sessionId) throws FieldNotFound {
        String confirmId = message.getConfirmID().getValue();
        String text = message.isSetField(Text.FIELD) ? message.getText().getValue() : "";

        log.info("Member received Confirmation for ConfirmID: {}", confirmId);

        fixReportRepository
                .save(new FixReport("Confirmation", "N/A", "CONFIRMED", "ConfirmID: " + confirmId + " " + text));
    }
}
