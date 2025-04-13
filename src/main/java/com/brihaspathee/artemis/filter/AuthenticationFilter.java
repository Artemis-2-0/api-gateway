package com.brihaspathee.artemis.filter;

import com.brihaspathee.artemis.dto.auth.AuthorizationRequest;
import com.brihaspathee.artemis.dto.auth.UserDto;
import com.brihaspathee.artemis.web.response.ArtemisAPIResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 09, April 2025
 * Time: 14:25
 * Project: artemis
 * Package Name: com.brihaspathee.artemis.filter
 * To change this template use File | Settings | File and Code Template
 */
@Slf4j
@Component
@Order(-1)
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config>{

    /**
     * A WebClient instance used to perform HTTP requests and handle responses.
     * It provides asynchronous and non-blocking communication with external services.
     * Utilized within the AuthenticationFilter class for request processing.
     */
    private final WebClient webClient;

    /**
     * Represents the header key used to retrieve the username information
     * from the application's configuration properties.
     *
     * The value of this variable is injected from the 'application.user-info.username'
     * property defined in the application's configuration files.
     */
    @Value("${application.user-info.username}")
    private String usernameHeader;

    /**
     * Represents the header value associated with the service ID configuration.
     * This value is injected from the application properties using the
     * configuration key "application.user-info.service-id".
     */
    @Value("${application.user-info.service-id}")
    private String serviceIdHeader;

    /**
     * Represents the header key used to retrieve the account type information
     * from the incoming HTTP request in the `UserContextInterceptor` class.
     * This value is injected through the application properties using the
     * Spring @Value annotation, mapped to the property key
     * `application.user-info.account-type`.
     *
     * This account type is utilized within the interceptor to extract
     * and include the user's account type in the user context for request handling.
     */
    @Value("${application.user-info.account-type}")
    private String accountTypeHeader;

    /**
     * Constructs an instance of AuthenticationFilter with the provided WebClient.
     *
     * @param webClientBuilder the WebClient instance used for executing asynchronous
     *                  and non-blocking HTTP requests to external services.
     */
    public AuthenticationFilter(WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClient = webClientBuilder
                .baseUrl("http://localhost:6094/api/v1/artemis/auth/secured")
                .build();
    }

    /**
     * Applies the authentication filter using the provided configuration settings.
     *
     * @param config the configuration object that contains settings for the filter's behavior
     * @return the configured {@code GatewayFilter} instance as the output of the filter application
     */
    @Override
    public GatewayFilter apply(Config config) {
        log.info("AuthenticationFilter applied");
        return (this::authenticate);
    }

    /**
     * Authenticates a request by validating the provided Bearer token in the Authorization header.
     * If the token is valid, enriches the request with user-specific information and forwards it to the next filter in the chain.
     * In the case of an invalid or missing token, the request is denied with an appropriate response.
     *
     * @param exchange Represents the current server-side HTTP request and response being processed within the web filter pipeline.
     * @param chain The filter chain to allow further processing of the current web exchange.
     * @return A {@code Mono<Void>} that completes when the request has either been successfully authenticated and forwarded to the next filter in the chain,
     *         or denied with an error response.
     */
    private Mono<Void> authenticate(ServerWebExchange exchange,
                                    GatewayFilterChain chain) {
        log.info("Authenticating request...");
        ServerHttpRequest request = exchange.getRequest();
        /*
            This will give the full url that the user tried to access
            i.e. http://localhost:6092/api/v1/artemis/account
         */
        String url = request.getURI().toString();
        /*
            if the user accessed the URL - http://localhost:6092/api/v1/artemis/account
            path will be - /api/v1/artemis/account
         */
        String path = request.getPath().toString();
        /*
            This gives any query parameters
            if the user accessed the URL - http://localhost:7092/api/v1/artemis/account?id=123&type=premium&status=active
            query will be - id=123&type=premium&status=active
         */
        String query = request.getQueryParams().toString();
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String accountType = request.getHeaders().getFirst(accountTypeHeader);
        log.info("Account Type: {}", accountType);
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.error("Invalid or missing Authorization header");
            return unauthorizedResponse(exchange);
        }
        String token = authHeader.substring(7);
        log.info("Token: {}", token);
        log.info("URL: {}", url);
        log.info("Path: {}", path);
        log.info("Query: {}", query);
        log.info("Auth Header: {}", authHeader);
        log.info("Authenticating request...");
        AuthorizationRequest authorizationRequest = AuthorizationRequest.builder()
                .resourceUri(path)
                .build();
        return webClient.post()
                .uri("/resource/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(BodyInserters.fromValue(authorizationRequest))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ArtemisAPIResponse<UserDto>>() {})
                .flatMap(response -> {
                    UserDto userDto = response.getResponse();
                    if(userDto != null) {
                        log.info("User authenticated successfully");
                        /*
                            - **Enrichment**: Injects additional information (e.g., `userId`, `username`, etc.) into
                            the request headers based on some external data source (e.g., `userDto` object).
                            - **Immutability Handling**: Both `ServerHttpRequest` and `ServerWebExchange` are
                            immutable, so the `mutate()` methods are used to create modified versions.
                            - **Reactive Processing**: Ensures the modifications stay compatible with the non-blocking,
                            reactive nature of the web framework.
                         */
                        ServerHttpRequest updatedRequest = exchange.getRequest().mutate()
                                .header("X-USER-NAME", userDto.getUsername())
                                .build();
                        return chain.filter(exchange.mutate().request(updatedRequest).build());
                    } else {
                        log.error("User authentication failed");
                        return forbiddenResponse(exchange);
                    }
                }).onErrorResume( e -> {
                    log.error("Error while authenticating request: {}", e.getMessage());
                    return unauthorizedResponse(exchange);
                });
    }

    /**
     * Sends an unauthorized response by setting the HTTP status to 401 (Unauthorized)
     * and completing the response.
     *
     * @param exchange the ServerWebExchange representing the web request and response
     * @return a Mono<Void> indicating the completion of the response handling
     */
    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    /**
     * Sends a forbidden response with HTTP status code 403.
     *
     * @param exchange the server web exchange containing the request and response objects
     * @return a {@code Mono<Void>} indicating that the response has been completed
     */
    private Mono<Void> forbiddenResponse(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        return exchange.getResponse().setComplete();
    }

    /**
     * The Config class serves as a configuration holder for the {@code AuthenticationFilter}.
     * It is used to encapsulate configuration-specific details related to the filter's behavior.
     * This class can be extended with custom configuration fields as needed
     * to enhance the functionality of the authentication filter.
     */
    public static class Config {
        public Config() {}
        // You can add custom config fields here if needed
    }
}
