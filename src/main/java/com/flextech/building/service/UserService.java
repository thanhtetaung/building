package com.flextech.building.service;

import com.flextech.building.authentication.JWTUtil;
import com.flextech.building.common.webservice.InvalidInputException;
import com.flextech.building.entity.User;
import com.flextech.building.entity.UserToken;
import com.flextech.building.repository.UserRepository;
import com.flextech.building.repository.UserTokenRepository;
import com.flextech.building.webservice.request.AuthRequest;
import com.flextech.building.webservice.request.ChangePasswordRequest;
import com.flextech.building.webservice.request.RegisterRequest;
import com.flextech.building.webservice.request.UpdateProfileRequest;
import com.flextech.building.webservice.response.AuthResponse;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Locale;

@SecurityScheme(
        name = "Authorization",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        in = SecuritySchemeIn.HEADER,
        bearerFormat = "JWT"
)
@RequestMapping(value = "/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class UserService {

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTokenRepository userTokenRepository;

    @Autowired
    private MessageSource messageSource;

    private AuthResponse createAuthResponse(UserToken token) {
        return AuthResponse.builder()
                .id(token.getUser().getId())
                .username(token.getUser().getUsername())
                .firstName(token.getUser().getFirstName())
                .lastName(token.getUser().getLastName())
                .token(token.getToken())
                .build();
    }

    private Mono<UserToken> createUserToken(User user) {
        UserToken token = UserToken.builder()
                .user(user)
                .token(JWTUtil.generateToken(user))
                .build();
        return userTokenRepository.save(token);
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<AuthResponse> login(@RequestBody @Validated AuthRequest request) {
        return userRepository.findByUsername(request.getUsername())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new UsernameNotFoundException(messageSource.getMessage("error.validation.username.invalid", null, Locale.getDefault())))))
                .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .switchIfEmpty(Mono.error(new BadCredentialsException(messageSource.getMessage("error.validation.password.invalid", null, Locale.getDefault()))))
                .flatMap(this::createUserToken)
                .map(this::createAuthResponse);
    }

    @SecurityRequirement(name = "Authorization")
    @PostMapping(value = "/changePassword", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Void> changePassword(Authentication authentication, @Validated @RequestBody ChangePasswordRequest request) {
        return Mono.just(authentication.getDetails())
                .cast(User.class)
                .filter(user -> passwordEncoder.matches(request.getOldPassword(), user.getPassword()))
                .switchIfEmpty(Mono.error(new InvalidInputException(messageSource.getMessage("error.validation.oldPassword.invalid", null, Locale.getDefault()))))
                .doOnNext(user -> user.setPassword(passwordEncoder.encode(request.getNewPassword())))
                .flatMap(userRepository::save)
                .then();
    }

    @SecurityRequirement(name = "Authorization")
    @GetMapping("/profile")
    public Mono<User> profileEnquiry(Authentication authentication) {
        return Mono.just(authentication.getDetails())
                .cast(User.class);
    }

    @SecurityRequirement(name = "Authorization")
    @PutMapping(value = "/profile", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<User> updateProfile(@Validated @RequestBody UpdateProfileRequest request, Authentication authentication) {
        return Mono.just(authentication.getDetails())
                .cast(User.class)
                .doOnNext(user -> {
                    user.setFirstName(request.getFirstName());
                    user.setLastName(request.getLastName());
                })
                .flatMap(userRepository::save);
    }


    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<User> register(@Validated @RequestBody RegisterRequest request) {
        return Mono.just(request)
                .map(req -> mapper.map(req, User.class))
                .doOnNext(user -> user.setPassword(passwordEncoder.encode(request.getPassword())))
                .flatMap(userRepository::save)
                .onErrorMap(DuplicateKeyException.class, e -> new InvalidInputException(
                    messageSource.getMessage("error.validation.username.duplicate", null, Locale.getDefault())
                ));
    }

}
