package com.flextech.building.authentication;

import com.flextech.building.repository.UserTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Transactional(propagation = Propagation.SUPPORTS)
public class LogoutSuccessHandler implements ServerLogoutSuccessHandler {

    @Autowired
    private UserTokenRepository userTokenRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
    public Mono<Void> onLogoutSuccess(WebFilterExchange exchange, Authentication authentication) {
        return Mono.just(authentication)
                .filter(auth -> auth instanceof AccessAuthenticationToken)
                .cast(AccessAuthenticationToken.class)
                .flatMap(token -> userTokenRepository.findByToken(token.getTokenValue()))
                .flatMap(userTokenRepository::delete)
                .then(createLogoutResponse(exchange));
    }

    private Mono<Void> createLogoutResponse(WebFilterExchange exchange) {
        ServerHttpResponse response = exchange.getExchange().getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        DataBuffer buf = response.bufferFactory().wrap("{}".getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buf));
    }
}
