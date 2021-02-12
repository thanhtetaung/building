package com.flextech.building.authentication;


import org.springframework.security.core.AuthenticationException;

public class InvalidTokenException extends AuthenticationException {

    public InvalidTokenException() {
        super("Invalid Token.");
    }

    public InvalidTokenException(String message) {
        super(message);
    }
}
