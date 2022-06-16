package com.brihaspathee.artemis.exception;

import com.brihaspathee.artemis.web.model.AuthenticationResponse;
import com.brihaspathee.artemis.web.resource.impl.AuthenticationResource;
import com.brihaspathee.artemis.web.response.ArtemisApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 16, June 2022
 * Time: 4:13 PM
 * Project: artemis
 * Package Name: com.brihaspathee.artemis.exception
 * To change this template use File | Settings | File and Code Template
 */
@Slf4j
@ControllerAdvice
public class ApiErrorHandler {

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ArtemisApiResponse<AuthenticationResource>> handleHttpClientErrorException(HttpClientErrorException exception) throws JsonProcessingException {
        log.info("Inside http client error exception: {}", exception.getRawStatusCode());
        if(exception.getRawStatusCode() == 401){
            ArtemisApiResponse apiResponse = ArtemisApiResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .message("UNAUTHORIZED")
                    .status(HttpStatus.UNAUTHORIZED)
                    .statusCode(401)
                    .response(AuthenticationResponse.builder()
                            .authMessage("Authentication Failed")
                            .isAuthenticated(false)
                            .build())
                    .build();
            return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);
        }else{
            ArtemisApiResponse apiResponse = ArtemisApiResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .message("Unknown exception occured")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .statusCode(500)
                    .response(AuthenticationResponse.builder()
                            .authMessage("Authentication Failed")
                            .isAuthenticated(false)
                            .build())
                    .build();
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }

//        //AuthenticateResponse exceptionList = AuthenticateResponse.builder().authMessage("Invalid user name and password").build();
//        AuthenticationErrorResponse errorResponse = AuthenticationErrorResponse.builder()
//                .errorCode(HttpStatus.UNAUTHORIZED.toString())
//                .errorMessage("Not authorized")
//                .build();
//        ObjectMapper objectMapper = new ObjectMapper();
//        String error = objectMapper.writeValueAsString(errorResponse);
//        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }
}
