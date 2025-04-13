package com.brihaspathee.artemis.route;

import com.brihaspathee.artemis.domain.entity.Route;
import com.brihaspathee.artemis.domain.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 11, April 2025
 * Time: 11:41
 * Project: artemis
 * Package Name: com.brihaspathee.artemis.route
 * To change this template use File | Settings | File and Code Template
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ArtemisRouteDefinitionRepository implements RouteDefinitionRepository {

    /**
     * A repository interface for performing CRUD operations on Route entities.
     * Used to manage and access route data in the application.
     */
    private final RouteRepository routeRepository;

    /**
     * Retrieves all enabled route definitions from the repository and converts them
     * into a reactive Flux stream of RouteDefinition objects.
     *
     * @return a Flux stream containing the route definitions for all enabled routes.
     */
    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        log.info("Getting all routes...");
        List<RouteDefinition> routeDefinitions = routeRepository.findByEnabledTrue()
                .stream()
                .map(this::convertToRouteDefinition)
                .toList();
        return Flux.fromIterable(routeDefinitions);
    }

    /**
     * Saves a given {@code Mono<RouteDefinition>} into the repository.
     *
     * @param route a {@code Mono<RouteDefinition>} representing the route definition to be saved
     * @return a {@code Mono<Void>} indicating the completion of the save operation
     */
    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return Mono.empty();
    }

    /**
     * Deletes a route with the specified route ID.
     *
     * @param routeId a {@code Mono<String>} representing the unique identifier of the route to be deleted
     * @return a {@code Mono<Void>} indicating the completion of the delete operation
     */
    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return Mono.empty();
    }

    /**
     * Converts a {@link Route} entity into a {@link RouteDefinition}, which defines
     * core properties for routing such as ID, URI, predicates, and filters.
     *
     * @param route the {@link Route} entity to be converted. This object contains
     *              attributes like route ID, URI, predicates, and filters that
     *              detail the routing configurations.
     * @return a {@link RouteDefinition} object constructed based on the attributes
     *         from the provided {@link Route} entity.
     */
    private RouteDefinition convertToRouteDefinition(Route route) {
        RouteDefinition routeDefinition = new RouteDefinition();
        routeDefinition.setId(route.getRouteId());
        routeDefinition.setUri(URI.create(route.getUri()));

        // Add Predicates
        List<PredicateDefinition> predicates = new ArrayList<>();
        predicates.add(new PredicateDefinition("Path="+route.getPredicates().trim()));

        routeDefinition.setPredicates(predicates);

        // Add filters
        if(route.getFilters() != null) {
            List<FilterDefinition> filters = new ArrayList<>();
            filters.add(new FilterDefinition(route.getFilters().trim()));
            routeDefinition.setFilters(filters);
        }

//        routeDefinition.setOrder((route.getIsSecured() ? -1 : 0));
        log.info("Route Definition: " + routeDefinition);
        return routeDefinition;
    }
}
