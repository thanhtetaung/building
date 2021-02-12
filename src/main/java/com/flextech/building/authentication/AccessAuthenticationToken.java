package com.flextech.building.authentication;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@Setter
public class AccessAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private String tokenValue;

    public AccessAuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }
    public AccessAuthenticationToken(Object principal, Object credentials,
                                     Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }
}
