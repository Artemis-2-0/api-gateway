package com.brihaspathee.artemis.web.resource.impl;

import com.brihaspathee.artemis.web.model.AuthenticationRequest;
import com.brihaspathee.artemis.web.model.AuthenticationResponse;
import com.brihaspathee.artemis.web.resource.interfaces.AuthenticateAPI;
import com.brihaspathee.artemis.web.response.ArtemisApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 16, June 2022
 * Time: 5:42 AM
 * Project: artemis
 * Package Name: com.brihaspathee.artemis.web.resource.impl
 * To change this template use File | Settings | File and Code Template
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthenticationResource implements AuthenticateAPI {

    private final RestTemplate restTemplate;

    @Override
    public ResponseEntity<ArtemisApiResponse<AuthenticationResponse>> authenticate(AuthenticationRequest authenticationRequest) {
        log.info("Inside the controller");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AuthenticationRequest> entity = new HttpEntity<>(authenticationRequest, headers);
        //ResponseEntity<ArtemisApiResponse> responseEntity = restTemplate.postForEntity("http://localhost:8081/artemis/authenticate", entity, ArtemisApiResponse.class);
        ResponseEntity<ArtemisApiResponse<AuthenticationResponse>> apiResponse = restTemplate.exchange(
                "http://localhost:8081/artemis/authenticate",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<ArtemisApiResponse<AuthenticationResponse>>() {});
        log.info("Response:{}", apiResponse.getBody());
        return apiResponse;
    }
}
