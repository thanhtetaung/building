package com.flextech.building.config;

import com.flextech.building.authentication.AuthenticationHandler;
import com.flextech.building.authentication.JwtAccessAuthenticationTokenConverter;
import com.flextech.building.authentication.LogoutSuccessHandler;
import com.flextech.building.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.web.server.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.LogoutWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import reactor.core.publisher.Mono;

import javax.crypto.spec.SecretKeySpec;

@EnableWebFluxSecurity
@EnableReactiveMongoAuditing
@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAccessAuthenticationTokenConverter jwtAccessAuthenticationTokenConverter;

    @Bean
    public AuthenticationHandler authenticationHandler() {
        return new AuthenticationHandler();
    }

    @Bean
    ReactiveAuditorAware<String> auditorAware() {
        return () -> ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getDetails)
                .filter(p -> p instanceof User)
                .map(User.class::cast)
                .map(User::getUsername)
                .switchIfEmpty(Mono.just("System"));
    }

    @Bean
    public ServerBearerTokenAuthenticationConverter serverBearerTokenAuthenticationConverter() {
        return new ServerBearerTokenAuthenticationConverter();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public NimbusReactiveJwtDecoder reactiveJwtDecoder(Environment env) {
        SecretKeySpec secretKey = new SecretKeySpec(env.getProperty("jwt.secret.key").getBytes(), "HmacSHA1");
        return NimbusReactiveJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return new LogoutSuccessHandler();
    }

    @Bean
    public LogoutWebFilter logoutWebFilter(LogoutSuccessHandler logoutSuccessHandler) {
        LogoutWebFilter filter = new LogoutWebFilter();
        filter.setRequiresLogoutMatcher(ServerWebExchangeMatchers.pathMatchers("/logout"));
        filter.setLogoutSuccessHandler(logoutSuccessHandler);
        return filter;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         LogoutWebFilter logoutWebFilter,
                                                         AuthenticationHandler authenticationHandler) {
        ReactiveAuthenticationManager authenticationManager;

        return http
                .authorizeExchange()
                .pathMatchers("/actuator/**", "/*/login", "/*/register", "/swagger-ui.html", "/webjars/**", "/api-docs/**")
                    .permitAll()
                .pathMatchers(HttpMethod.OPTIONS)
                    .permitAll()
                .and()
                .httpBasic().disable()
                .csrf().disable()
                .formLogin().disable()
                .logout().disable()
                .authorizeExchange()
                .anyExchange().authenticated()
                .and()
                .addFilterAt(logoutWebFilter, SecurityWebFiltersOrder.LOGOUT)
//                        .exceptionHandling()
//                            .authenticationEntryPoint(authenticationFailureHandler)
//                            .accessDeniedHandler(authenticationFailureHandler)
//                .and()
                .oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(jwtAccessAuthenticationTokenConverter)
                .and()
                .authenticationEntryPoint(authenticationHandler)
                .accessDeniedHandler(authenticationHandler)
                .and().build();

    }

}
