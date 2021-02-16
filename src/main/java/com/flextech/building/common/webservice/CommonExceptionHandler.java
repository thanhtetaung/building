package com.flextech.building.common.webservice;

import com.flextech.building.common.webservice.response.ErrorDetail;
import com.flextech.building.common.webservice.response.ErrorResponse;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@CommonsLog
@RestControllerAdvice
public class CommonExceptionHandler {

    public CommonExceptionHandler() {
        System.out.println("constructor");
    }


    /**
     * Handle username notfound exception
     *
     * @param exception
     * @return a {@code ErrorResponse}
     */
    @ExceptionHandler(value = UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Mono<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException exception) {
        log.error(exception.getMessage(), exception);
        return Mono.just(ErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .message(exception.getMessage())
                .build());
    }

    /**
     * Handle BadCredentialsException
     *
     * @param exception
     * @return a {@code ErrorResponse}
     */
    @ExceptionHandler(value = BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Mono<ErrorResponse> handleBadCredentialsException(BadCredentialsException exception) {
        log.error(exception.getMessage(), exception);
        return Mono.just(ErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .message(exception.getMessage())
                .build());
    }


    /**
     * Method that check against {@code @Valid} Objects passed to controller endpoints
     *
     * @param exception
     * @return a {@code ErrorResponse}
     */
    @ExceptionHandler(value = WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ErrorResponse> handleException(WebExchangeBindException exception) {
        log.error(exception.getMessage(), exception);
        List<ErrorDetail> details = exception.getBindingResult().getFieldErrors().stream()
                .map(err -> new ErrorDetail(err.getField(), err.getRejectedValue(), err.getDefaultMessage()))
                .distinct()
                .collect(Collectors.toList());

        return Mono.just(new ErrorResponse().builder()
                .errors(details)
                .message(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .status(HttpStatus.BAD_REQUEST.value())
                .build());
    }

    /**
     * Handle unprocessable json data exception
     *
     * @param exception
     * @return a {@code ErrorResponse}
     */
    @ExceptionHandler(value = ServerWebInputException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public Mono<ErrorResponse> handleUnprocessableMsgException(ServerWebInputException exception) {
        log.error(exception.getMessage(), exception);
        return Mono.just(new ErrorResponse().builder()
                .message("Unprocessable Input Data.")
                .details(exception.getMessage())
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .build());
    }

    /**
     * Handle InvalidInputException exception
     *
     * @param exception
     * @return a {@code ErrorResponse}
     */
    @ExceptionHandler(value = InvalidInputException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public Mono<ErrorResponse> handleInvalidInputException(InvalidInputException exception) {
        log.error(exception.getMessage(), exception);
        return Mono.just(new ErrorResponse().builder()
                .message(exception.getMessage())
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .build());
    }

    /**
     * Handle InvalidInputException exception
     *
     * @param exception
     * @return a {@code ErrorResponse}
     */
    @ExceptionHandler(value = ChildExistException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ErrorResponse> handleChildExistException(ChildExistException exception) {
        log.error(exception.getMessage(), exception);
        return Mono.just(new ErrorResponse().builder()
                .message(exception.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .build());
    }

    /**
     * Handle data not found exception
     *
     * @param exception
     * @return a {@code ErrorResponse}
     */
    @ExceptionHandler(value = DataNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<Void> handleDataNotFoundException(DataNotFoundException exception) {
        log.error(exception.getMessage(), exception);
        return Mono.empty();
    }


    /**
     * Handle decoding exception
     *
     * @param exception
     * @return a {@code ErrorResponse}
     */
    @ExceptionHandler(value = DecodingException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public Mono<ErrorResponse> handleDecodingException(DecodingException exception) {
        log.error(exception.getMessage(), exception);
        return Mono.just(new ErrorResponse().builder()
                .message(exception.getMessage())
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .build());
    }

    /**
     * Handle all other exception
     *
     * @param exception
     * @return a {@code ErrorResponse}
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<ErrorResponse> handleOtherException(Exception exception) {
        log.error(exception.getMessage(), exception);
        return Mono.just(new ErrorResponse().builder()
                .message("System Error.")
                .details(exception.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build());
    }
}
