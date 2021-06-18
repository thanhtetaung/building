package com.flextech.building.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flextech.building.common.webservice.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
public class AuthenticationHandler implements ServerAuthenticationFailureHandler, ServerAuthenticationEntryPoint, ServerAccessDeniedHandler {

    @Autowired
    private  ObjectMapper objectMapper;


    public Mono<Void> createErrorResponse(ServerHttpResponse response, String message, HttpStatus status) {
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        response.getHeaders().setAccessControlAllowOrigin("*");

        ErrorResponse errorResponse = new ErrorResponse().builder()
                .message(message)
                .status(status.value())
                .build();
        String responseStr = "";
        try {
            responseStr = objectMapper.writeValueAsString(errorResponse);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }

        DataBuffer buf = response.bufferFactory().wrap(responseStr.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buf));
    }

    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange exchange, AuthenticationException exception) {
        ServerHttpResponse response = exchange.getExchange().getResponse();
        return createErrorResponse(response, exception.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        ServerHttpResponse response = exchange.getResponse();
        return createErrorResponse(response, "Invalid Authorization Token", HttpStatus.UNAUTHORIZED);
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        ServerHttpResponse response = exchange.getResponse();
        return createErrorResponse(response, "Access Denied.", HttpStatus.FORBIDDEN);
    }



    /**
     * Handle invalid token exception
     *
     * @param exception
     * @return a {@code ErrorResponse}
     */
    @ExceptionHandler(value = InvalidTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Mono<ErrorResponse> handleDataNotFoundException(InvalidTokenException exception) {
        log.error(exception.getMessage(), exception);
        return Mono.just(ErrorResponse.builder()
                .message(exception.getMessage())
                .status(HttpStatus.UNAUTHORIZED.value())
                .build());
    }
}
