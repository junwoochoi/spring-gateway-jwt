package com.alpha.auth.auth;

import lombok.Getter;
import lombok.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import static java.util.stream.Collectors.joining;

@Getter
public class TokenUser {
    public static final CharSequence COMMA = ",";
    private String roles;
    private String name;

    public TokenUser(Authentication authentication) {
        this(authentication.getName(), authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(joining(COMMA)));
    }

    public TokenUser(@NonNull String name, @NonNull String roles) {
        this.roles = roles;
        this.name = name;
    }

}
