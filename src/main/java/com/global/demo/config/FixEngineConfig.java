package com.global.demo.config;

import com.global.demo.fix.ClearingAcceptor;
import com.global.demo.fix.MemberInitiator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import quickfix.*;

import java.io.InputStream;

@Configuration
public class FixEngineConfig {
    private static final Logger log = LoggerFactory.getLogger(FixEngineConfig.class);

    @Bean
    @Profile("acceptor")
    public ThreadedSocketAcceptor acceptor(ClearingAcceptor clearingAcceptor,
            @Value("${fix.acceptor.config:acceptor.cfg}") String configFile) throws Exception {
        log.info("Starting FIX Acceptor with config: {}", configFile);
        InputStream inputStream = FixEngineConfig.class.getClassLoader().getResourceAsStream(configFile);
        SessionSettings settings = new SessionSettings(inputStream);

        MessageStoreFactory storeFactory = new FileStoreFactory(settings);
        LogFactory logFactory = new FileLogFactory(settings);
        MessageFactory messageFactory = new DefaultMessageFactory();

        ThreadedSocketAcceptor acceptor = new ThreadedSocketAcceptor(
                clearingAcceptor, storeFactory, settings, logFactory, messageFactory);

        acceptor.start();
        return acceptor;
    }

    @Bean
    @Profile("initiator")
    public ThreadedSocketInitiator initiator(MemberInitiator memberInitiator,
            @Value("${fix.initiator.config:initiator.cfg}") String configFile) throws Exception {
        log.info("Starting FIX Initiator with config: {}", configFile);
        InputStream inputStream = FixEngineConfig.class.getClassLoader().getResourceAsStream(configFile);
        SessionSettings settings = new SessionSettings(inputStream);

        MessageStoreFactory storeFactory = new FileStoreFactory(settings);
        LogFactory logFactory = new FileLogFactory(settings);
        MessageFactory messageFactory = new DefaultMessageFactory();

        ThreadedSocketInitiator initiator = new ThreadedSocketInitiator(
                memberInitiator, storeFactory, settings, logFactory, messageFactory);

        initiator.start();
        return initiator;
    }
}
