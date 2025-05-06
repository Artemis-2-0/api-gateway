package com.brihaspathee.artemis.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 21, April 2025
 * Time: 15:13
 * Project: artemis
 * Package Name: com.brihaspathee.artemis.config
 * To change this template use File | Settings | File and Code Template
 */
@Slf4j
@Component
public class StartupLogger {

    @Value("${application.auth-service.host}")
    private String authServiceHost;

    @Value("${application.auth-service.port}")
    private String authServicePort;

    @Value("${application.user-info.user-id}")
    private String userIdHeader;

    /**
     * Logs a message indicating that the Account Management Service has successfully started
     * and is ready for use. This method is triggered automatically when the application
     * is fully initialized and ready to serve requests.
     *
     * The method listens for the {@link ApplicationReadyEvent} which is published by the
     * Spring Framework when the application context has completed its startup process.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void logStartupComplete() {
        log.info("✅ API Gateway is UP and ready!");
    }

    @PostConstruct
    public void logStartupValues() {
        log.info("====== Startup Config Check ======");
        log.info("Auth Service Host: {}", authServiceHost);
        log.info("Auth Service Port: {}", authServicePort);
        log.info("User ID Header: {}", userIdHeader);
        log.info("==================================");
    }
}
