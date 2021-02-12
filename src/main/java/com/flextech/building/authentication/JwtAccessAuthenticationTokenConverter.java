package com.flextech.building.authentication;

import com.flextech.building.entity.User;
import com.flextech.building.repository.UserTokenRepository;
import com.flextech.building.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class JwtAccessAuthenticationTokenConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>>  {

    @Autowired
    private UserTokenRepository userTokenRepository;

    @Autowired
    private UserService userService;

    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
        return Mono.just(jwt)
                .flatMap(j -> {
                    return userTokenRepository.findByToken(j.getTokenValue());
                })
                .switchIfEmpty(Mono.error(new BadJwtException("Invalid Authorization Token.")))
                .map(userToken -> userToken.getUser())
                .cast(User.class)
                .map(user -> {
                    return createAccessAuthenticationToken(user, jwt);
                });
    }


    private AccessAuthenticationToken createAccessAuthenticationToken(User user, Jwt jwt) {
        AccessAuthenticationToken token = new AccessAuthenticationToken(user, user.getPassword(), user.getAuthorities());
        token.setDetails(user);
        token.setTokenValue(jwt.getTokenValue());
        return token;
    }

    private void validateToken(String token) {
        userTokenRepository.findByToken(token);
    }
}
