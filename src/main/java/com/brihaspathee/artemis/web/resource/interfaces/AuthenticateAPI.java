package com.brihaspathee.artemis.web.resource.interfaces;

import com.brihaspathee.artemis.web.model.AuthenticationRequest;
import com.brihaspathee.artemis.web.model.AuthenticationResponse;
import com.brihaspathee.artemis.web.response.ArtemisApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 16, June 2022
 * Time: 5:39 AM
 * Project: artemis
 * Package Name: com.brihaspathee.artemis.web.resource.interfaces
 * To change this template use File | Settings | File and Code Template
 */
@RequestMapping("/artemis")
@Validated
public interface AuthenticateAPI {

    /**
     * Authenticate the user
     * @param authenticationRequest
     * @return
     */
    @Operation(
            method = "POST",
            description = "Authenticate the user",
            tags = {"authentication"}
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully authenticate the user",
                            content = {
                                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ArtemisApiResponse.class))
                            }
                    )
            }
    )
    @PostMapping("/authenticate")
    ResponseEntity<ArtemisApiResponse<AuthenticationResponse>> authenticate(@RequestBody AuthenticationRequest authenticationRequest);
}
