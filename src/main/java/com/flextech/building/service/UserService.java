package com.flextech.building.service;

import com.flextech.building.authentication.JWTUtil;
import com.flextech.building.common.webservice.InvalidInputException;
import com.flextech.building.entity.Otp;
import com.flextech.building.entity.User;
import com.flextech.building.entity.UserToken;
import com.flextech.building.entity.enums.Indicator;
import com.flextech.building.repository.OtpRepository;
import com.flextech.building.repository.UserRepository;
import com.flextech.building.repository.UserTokenRepository;
import com.flextech.building.webservice.request.*;
import com.flextech.building.webservice.response.AuthResponse;
import freemarker.template.TemplateException;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import javax.mail.MessagingException;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@SecurityScheme(
        name = "Authorization",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        in = SecuritySchemeIn.HEADER,
        bearerFormat = "JWT"
)
@RequestMapping(value = "/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@Slf4j
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

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private EmailService emailService;

    @Value("${support.email}")
    private String supportEmail;

    @Value("${verification.email.subject}")
    private String verificationMailSubject;

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
                .flatMap(user -> this.checkEmailVerification(user, request.getOtp()))
                .doOnNext(user -> user.setPassword(passwordEncoder.encode(request.getPassword())))
                .flatMap(userRepository::save)
                .onErrorMap(DuplicateKeyException.class, e -> new InvalidInputException(
                    messageSource.getMessage("error.validation.username.duplicate", null, Locale.getDefault())
                ));
    }

    private Mono<User> checkEmailVerification(User user, String otp) {
        return otpRepository.findByOtpAndLinkIdAndUsedInd(otp, user.getEmail(), Indicator.Y)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new InvalidInputException(messageSource.getMessage("error.validation.email.notVerified", null, Locale.getDefault())))))
                .map(o -> user);
    }

    @GetMapping(value = "/requestVerification")
    public Mono<Void> requestVerification(@NotNull(message = "{error.validation.email.empty}")
                             @Email(message = "{error.validation.email.invalid}")
                             @Valid String email) {

        return otpRepository.deleteAllByLinkId(email)
                .then(Mono.defer(() -> Mono.just(this.createOtp(email))))
                .flatMap(otpRepository::save)
                .flatMap(this::sendVerificationMail)
                .then(Mono.empty());

    }

    private Mono<Otp> sendVerificationMail(Otp otp) {
        Map<String, Object> params = new HashMap<>();
        params.put("email", otp.getLinkId());
        params.put("otp", otp.getOtp());
        try {
            emailService.sendMessage(supportEmail, otp.getLinkId(), verificationMailSubject, "verification.ftl", params);
        } catch (IOException | TemplateException | MessagingException e) {
           log.error(e.getMessage(), e);
           return Mono.error(e);
        }
        return Mono.just(otp);
    }

    private Otp createOtp(String email) {
        return Otp.builder()
                .linkId(email)
                .usedInd(Indicator.N)
                .otp(RandomStringUtils.randomAlphanumeric(6))
                .expireDateTime(LocalDateTime.now().plusMinutes(15))
                .build();
    }

    @PostMapping(value = "/verify", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Otp> verify(@RequestBody @Validated VerificationRequest request) {
        return otpRepository.findByOtpAndLinkId(request.getOtp(), request.getEmail())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new InvalidInputException(messageSource.getMessage("error.validation.otp.invalid", null, Locale.getDefault())))))
                .filter(otp -> otp.getUsedInd() == Indicator.N)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new InvalidInputException(messageSource.getMessage("error.validation.otp.used", null, Locale.getDefault())))))
                .filter(otp -> otp.getExpireDateTime().isAfter(LocalDateTime.now()))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new InvalidInputException(messageSource.getMessage("error.validation.otp.expired", null, Locale.getDefault())))))
                .flatMap(otp -> {
                    otp.setUsedInd(Indicator.Y);
                    return otpRepository.save(otp);
                });

    }

}
