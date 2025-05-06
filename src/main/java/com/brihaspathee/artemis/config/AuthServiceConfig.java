package com.brihaspathee.artemis.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 21, April 2025
 * Time: 21:58
 * Project: artemis
 * Package Name: com.brihaspathee.artemis.config
 * To change this template use File | Settings | File and Code Template
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "application.auth-service")
public class AuthServiceConfig {

    private String host;
    private String port;
}
