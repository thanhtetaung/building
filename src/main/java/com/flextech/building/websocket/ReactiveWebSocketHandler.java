package com.flextech.building.websocket;

import com.flextech.building.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class ReactiveWebSocketHandler implements WebSocketHandler {

    private Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private Map<String, User> userMap = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        log.info("connect");
        Mono<User> task1 = ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getDetails)
                .filter(p -> p instanceof User)
                .map(User.class::cast)
                .doOnNext(user -> {
                    userMap.put(session.getId(), user);
                    sessions.put(user.getId(), session);
                });


        return session.receive()
                .doOnNext(message -> {
                    log.info("message : " + message.getPayloadAsText());
                })
                .doOnComplete(() -> {
                    User user =userMap.get(session.getId());
                    if (user != null) {
                        sessions.remove(user.getId());
                    }
                    userMap.remove(session.getId());
                    log.info("disconnected");
                }).zipWith(task1, (webSocketMessage, user) -> user)
                .then();

    }

    public Mono<Void> sendStatusUpdatedEvent(String userId, String executionArn) {
        return Mono.justOrEmpty(sessions.get(userId))
                .flatMap(session -> session.send(
                        Mono.just(
                            session.textMessage("{\"executionArn\": \"" + executionArn + "\"}")
                        )
                    )
                );

    }
}
