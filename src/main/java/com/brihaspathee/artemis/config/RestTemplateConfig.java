package com.brihaspathee.artemis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 16, June 2022
 * Time: 4:02 PM
 * Project: artemis
 * Package Name: com.brihaspathee.artemis.config
 * To change this template use File | Settings | File and Code Template
 */
@Component
public class RestTemplateConfig {

    @Bean
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }
}
