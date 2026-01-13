package com.global.demo.fix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import quickfix.*;
import quickfix.fix44.AllocationReport;
import quickfix.fix44.Confirmation;

@Component
@Profile("initiator")
public class MemberInitiator extends MessageCracker implements Application {
    private static final Logger log = LoggerFactory.getLogger(MemberInitiator.class);

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

    @Handler
    public void onMessage(AllocationReport message, SessionID sessionId) throws FieldNotFound {
        log.info("Member received AllocationReport for AllocID: {}. Status: {}",
                message.getAllocID().getValue(), message.getAllocStatus().getValue());
    }

    @Handler
    public void onMessage(Confirmation message, SessionID sessionId) throws FieldNotFound {
        log.info("Member received Confirmation for ConfirmID: {}", message.getConfirmID().getValue());
    }
}
